package com.cysaaa.gui.screens;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.cysaaa.gui.components.*;
import com.cysaaa.util.AudioManager;

public class MainMenuPanel extends BackgroundPanel {

    // ---------------------------------------------------------
    // CONFIGURATION
    // ---------------------------------------------------------

    private static final String BACKGROUND_IMAGE =
            "/backgrounds/background_mainmenu.png";

    private static final String TITLE_IMAGE =
            "/splashscreen/splash2.png";

    private static final int DESIGN_WIDTH = 1920;
    private static final int DESIGN_HEIGHT = 1080;

    // PercentLayout position of the title panel.
    // The visible title has a 20-pixel inset inside this panel.
    private static final int TITLE_PANEL_X = 65;
    private static final int TITLE_PANEL_Y = 150;
    private static final int TITLE_PANEL_WIDTH = 940;
    private static final int TITLE_PANEL_HEIGHT = 245;

    private final CrowFlock crows;
    private static final Color[] SPARK_COLORS = {
        new Color(255, 40, 200),
        new Color(110, 220, 255),
        new Color(255, 245, 255)
    };

    // ---------------------------------------------------------
    // ANIMATION FIELDS
    // ---------------------------------------------------------

    private final WindEffect wind = new WindEffect(70);

    private final CharacterAnimator characters;
    private final AmbientEffects ambient;
    private final long animStart = System.currentTimeMillis();

    private final Random random = new Random();

    private final ArrayList<Spark> sparks = new ArrayList<>();

    private BufferedImage titleOverlay;

    private TitleOverlayPanel titlePanel;

    private float titleOpacity = 1f;

    private boolean flickering;

    private long nextFlickerAt;
    private long flickerEndsAt;
    private long nextFlickerToggleAt;

    // ---------------------------------------------------------
    // ANIMATION TIMER
    // ---------------------------------------------------------

    private final Timer windTimer = new Timer(33, e -> {
        wind.update();
        updateTitleEffect();
        repaint();
    });

    // ---------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------

    public MainMenuPanel(
            JPanel cardContainer,
            CardLayout cardLayout,
            GameplayPanel gameplayPanel) {

        super(BACKGROUND_IMAGE);

        // One shared copy of the background for the animators.
        BufferedImage menuImage = loadBackground();
        characters = new CharacterAnimator(menuImage);
        ambient = new AmbientEffects(menuImage);
        crows = new CrowFlock(characters);
        

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        setLayout(layout);
        setOpaque(false);

        // Load the title image BEFORE creating the title component.
        loadTitleOverlay();

        // -----------------------------------------------------
        // TITLE -- POSITIONED USING PERCENTLAYOUT
        // -----------------------------------------------------

        titlePanel = new TitleOverlayPanel();

        layout.addPixel(
                this,
                titlePanel,
                TITLE_PANEL_X,
                TITLE_PANEL_Y,
                TITLE_PANEL_WIDTH,
                TITLE_PANEL_HEIGHT
        );

        nextFlickerAt = System.currentTimeMillis()
                + randomBetween(1800, 3400);

        // -----------------------------------------------------
        // PLAY BUTTON
        // -----------------------------------------------------

        ImagePanel playButton = new ImagePanel(
                "/buttons/start_button.png",
                "/buttons/startHover.png");

        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameplayPanel.startNewGame();
                cardLayout.show(cardContainer, "HOST_SELECTION");
            }
        });

        // -----------------------------------------------------
        // HOW TO PLAY BUTTON
        // -----------------------------------------------------

        ImagePanel howToPlayButton = new ImagePanel(
                "/buttons/how_to_play.png",
                "/buttons/htpHover.png");

       

        howToPlayButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardContainer, "INSTRUCTIONS");
            }
        });

        // -----------------------------------------------------
        // EXIT BUTTON
        // -----------------------------------------------------

        ImagePanel exitButton = new ImagePanel(
                "/buttons/exit_button.png",
                "/buttons/exitHover.png");

        
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        layout.addPixel(this, playButton,      185, 460, 565, 175);
        layout.addPixel(this, howToPlayButton, 241, 651, 447, 138);
        layout.addPixel(this, exitButton,      265, 800, 406, 126);

        // -----------------------------------------------------
        // MENU LIFECYCLE
        // -----------------------------------------------------

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                AudioManager.playBackground("mainbg.wav");

                nextFlickerAt = System.currentTimeMillis()
                        + randomBetween(1800, 3400);

                windTimer.start();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                windTimer.stop();

                sparks.clear();

                flickering = false;
                titleOpacity = 1f;

                titlePanel.repaint();
                repaint();
            }
        });
    }

    // Places a button so its RESTING size is (w, h) at (x, y), leaving room
   
    // ---------------------------------------------------------
    // LOAD BACKGROUND FOR THE ANIMATORS
    // ---------------------------------------------------------

    private static BufferedImage loadBackground() {

        try (InputStream in = MainMenuPanel.class
                .getResourceAsStream(BACKGROUND_IMAGE)) {

            if (in == null) {
                throw new RuntimeException(
                        "Menu background not found: " + BACKGROUND_IMAGE);
            }

            return ImageIO.read(in);

        } catch (IOException e) {
            throw new RuntimeException("Cannot load menu background", e);
        }
    }

    // ---------------------------------------------------------
    // LOAD AND CROP TITLE IMAGE
    // ---------------------------------------------------------

    private void loadTitleOverlay() {

        try (InputStream input =
                     getClass().getResourceAsStream(TITLE_IMAGE)) {

            if (input == null) {
                System.err.println(
                        "Title image not found: " + TITLE_IMAGE);
                return;
            }

            BufferedImage loadedImage = ImageIO.read(input);

            if (loadedImage != null) {
                titleOverlay = trimTransparentBorders(loadedImage);
            }

        } catch (IOException e) {
            System.err.println(
                    "Unable to load title image: " + e.getMessage());
        }
    }

    private BufferedImage trimTransparentBorders(
            BufferedImage source) {

        int minX = source.getWidth();
        int minY = source.getHeight();

        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {

                int alpha = (source.getRGB(x, y) >>> 24) & 0xFF;

                if (alpha > 20) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);

                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        // If the image has no transparent margins, use it as-is.
        if (maxX < minX || maxY < minY) {
            return source;
        }

        return source.getSubimage(
                minX,
                minY,
                maxX - minX + 1,
                maxY - minY + 1
        );
    }

    // ---------------------------------------------------------
    // TITLE FLICKER ANIMATION
    // ---------------------------------------------------------

    private void updateTitleEffect() {

        long now = System.currentTimeMillis();

        Iterator<Spark> iterator = sparks.iterator();

        while (iterator.hasNext()) {

            Spark spark = iterator.next();
            spark.update();

            if (spark.life <= 0) {
                iterator.remove();
            }
        }

        if (!flickering && now >= nextFlickerAt) {

            flickering = true;

            flickerEndsAt = now + randomBetween(240, 460);
            nextFlickerToggleAt = now;

            spawnSparks(randomBetween(9, 16));
        }

        if (!flickering) {
            return;
        }

        if (now >= flickerEndsAt) {

            flickering = false;
            titleOpacity = 1f;

            nextFlickerAt = now + randomBetween(3200, 5600);

            return;
        }

        if (now >= nextFlickerToggleAt) {

            boolean lit = random.nextFloat() > 0.42f;

            if (lit) {

                titleOpacity =
                        0.68f + random.nextFloat() * 0.32f;

            } else {

                titleOpacity = random.nextFloat() * 0.12f;

                if (random.nextFloat() < 0.55f) {
                    spawnSparks(randomBetween(1, 3));
                }
            }

            nextFlickerToggleAt = now + randomBetween(35, 90);
        }
    }

    // ---------------------------------------------------------
    // CREATE SPARKS RELATIVE TO THE TITLE PANEL
    // ---------------------------------------------------------

    private void spawnSparks(int count) {

        if (titlePanel == null
                || titlePanel.getWidth() <= 40
                || titlePanel.getHeight() <= 40) {
            return;
        }

        int left = 20;
        int right = titlePanel.getWidth() - 20;

        int top = 20;
        int bottom = titlePanel.getHeight() - 20;

        for (int i = 0; i < count; i++) {

            float x;
            float y;

            float vx;
            float vy;

            switch (random.nextInt(4)) {

                case 0: // Top edge
                    x = randomBetween(left, right);
                    y = top;

                    vx = (random.nextFloat() - 0.5f) * 5f;
                    vy = -(1f + random.nextFloat() * 3f);
                    break;

                case 1: // Bottom edge
                    x = randomBetween(left, right);
                    y = bottom;

                    vx = (random.nextFloat() - 0.5f) * 5f;
                    vy = 1f + random.nextFloat() * 3f;
                    break;

                case 2: // Left edge
                    x = left;
                    y = randomBetween(top, bottom);

                    vx = -(1f + random.nextFloat() * 3f);
                    vy = (random.nextFloat() - 0.5f) * 5f;
                    break;

                default: // Right edge
                    x = right;
                    y = randomBetween(top, bottom);

                    vx = 1f + random.nextFloat() * 3f;
                    vy = (random.nextFloat() - 0.5f) * 5f;
                    break;
            }

            Color color = SPARK_COLORS[
                    random.nextInt(SPARK_COLORS.length)];

            sparks.add(new Spark(
                    x,
                    y,
                    vx,
                    vy,
                    randomBetween(8, 20),
                    randomBetween(220, 480),
                    color
            ));
        }
    }

    // ---------------------------------------------------------
    // DRAW SPARKS
    // ---------------------------------------------------------

    private void paintSparks(Graphics2D graphics) {

        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Spark spark : sparks) {

            float alpha = Math.max(
                    0f,
                    Math.min(
                            1f,
                            spark.life / (float) spark.maxLife));

            float speed = (float) Math.sqrt(
                    spark.vx * spark.vx
                    + spark.vy * spark.vy);

            if (speed < 0.01f) {
                speed = 0.01f;
            }

            float tailX = spark.x
                    - spark.vx / speed * spark.length;

            float tailY = spark.y
                    - spark.vy / speed * spark.length;

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    alpha * 0.45f));

            g.setColor(spark.color);

            g.setStroke(new BasicStroke(
                    5f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            g.drawLine(
                    Math.round(tailX),
                    Math.round(tailY),
                    Math.round(spark.x),
                    Math.round(spark.y));

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    alpha));

            g.setColor(Color.WHITE);

            g.setStroke(new BasicStroke(
                    1.4f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            g.drawLine(
                    Math.round(tailX),
                    Math.round(tailY),
                    Math.round(spark.x),
                    Math.round(spark.y));

            g.fillOval(
                    Math.round(spark.x - 2),
                    Math.round(spark.y - 2),
                    4,
                    4);
        }

        g.dispose();
    }

    // ---------------------------------------------------------
    // MAIN MENU BACKGROUND, CHARACTERS, AMBIENT EFFECTS, WIND
    // ---------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {

        // Draw background_mainmenu.png.
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Characters + ambient effects, scaled from image pixels
        // to the current panel size.
        Graphics2D cg = (Graphics2D) g2.create();

        cg.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        cg.scale(getWidth() / (double) characters.getWidth(),
                 getHeight() / (double) characters.getHeight());

        double t = (System.currentTimeMillis() - animStart) / 1000.0;

        characters.paint(cg, t);
        ambient.paint(cg, t);
        
        characters.paint(cg, t);
        ambient.paint(cg, t);
        crows.paint(cg, t);
        
        cg.dispose();

        // Original wind effect.
        wind.setSize(getWidth(), getHeight());
        wind.paint(g2);

        g2.dispose();

    }

    // ---------------------------------------------------------
    // RANDOM NUMBER HELPER
    // ---------------------------------------------------------

    private int randomBetween(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    // ---------------------------------------------------------
    // TITLE OVERLAY PANEL
    // ---------------------------------------------------------

    private class TitleOverlayPanel extends JPanel {

        private TitleOverlayPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            if (titleOverlay != null) {

                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        titleOpacity));

                int inset = 20;

                g2.drawImage(
                        titleOverlay,
                        inset,
                        inset,
                        Math.max(1, getWidth() - inset * 2),
                        Math.max(1, getHeight() - inset * 2),
                        this
                );

                g2.setComposite(AlphaComposite.SrcOver);
            }

            paintSparks(g2);

            g2.dispose();
        }
    }

    // ---------------------------------------------------------
    // ELECTRIC SPARK PARTICLE
    // ---------------------------------------------------------

    private static class Spark {

        float x;
        float y;

        float vx;
        float vy;

        final float length;

        int life;
        final int maxLife;

        final Color color;

        private Spark(
                float x,
                float y,
                float vx,
                float vy,
                float length,
                int lifetime,
                Color color) {

            this.x = x;
            this.y = y;

            this.vx = vx;
            this.vy = vy;

            this.length = length;

            this.life = lifetime;
            this.maxLife = lifetime;

            this.color = color;
        }

        private void update() {

            x += vx;
            y += vy;

            vy += 0.08f;
            vx *= 0.985f;

            life -= 33;
        }
    }
}