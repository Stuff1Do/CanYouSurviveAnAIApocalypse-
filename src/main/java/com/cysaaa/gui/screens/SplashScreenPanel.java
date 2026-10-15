package com.cysaaa.gui.screens;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.cysaaa.gui.components.*;
import com.cysaaa.util.AudioManager;

public class SplashScreenPanel extends JPanel {

    private static final int DESIGN_W = 1920;
    private static final int DESIGN_H = 1080;

    private static final int SPLASH_DURATION_MS = 9500;
    private static final int FADE_DURATION_MS = 660;
    private static final int FRAME_MS = 40;

    private static final String LOADING_FONT = "Chakra Petch";
    private static final Color LOADING_PINK = new Color(255, 20, 190);

    private static final String TITLE_IMAGE = "/splashscreen/splash2.png";

    // Neon flicker timing
    private static final int FIRST_FLICKER_MIN_MS = 1800;
    private static final int FIRST_FLICKER_MAX_MS = 3400;
    private static final int FLICKER_DELAY_MIN_MS = 1000;
    private static final int FLICKER_DELAY_MAX_MS = 2000;
    private static final int FLICKER_DURATION_MIN_MS = 240;
    private static final int FLICKER_DURATION_MAX_MS = 460;

    private static final Color[] SPARK_COLORS = {
        new Color(255, 70, 210),
        new Color(100, 230, 255),
        new Color(255, 235, 255)
    };

    // Fallback only (design space) if the title image can't be scanned.
    private static final int TITLE_SPARK_LEFT = 85;
    private static final int TITLE_SPARK_RIGHT = 1110;
    private static final int TITLE_SPARK_TOP = 110;
    private static final int TITLE_SPARK_BOTTOM = 355;

    private final JPanel cardContainer;
    private final CardLayout cardLayout;

    private final Timer animationTimer;
    private final FlickerTitlePanel titleLayer;

    private final ArrayList<Spark> sparks = new ArrayList<>();
    private final Random random = new Random();

    // Edge pixels of the real title, in 1920x1080 design space: {x, y}
    private final ArrayList<float[]> titleEdgePoints = new ArrayList<>();

    private long startedAt;
    private long nextFlickerAt;
    private long flickerEndsAt;
    private long nextFlickerToggleAt;

    private int animationOffset;
    private float opacity = 1f;

    private boolean fading;
    private boolean flickering;
    private boolean flickerLit = true;

    private String loadingStatus = "LOADING...";

    public SplashScreenPanel(JPanel cardContainer, CardLayout cardLayout) {

        this.cardContainer = cardContainer;
        this.cardLayout = cardLayout;

        setPreferredSize(new Dimension(DESIGN_W, DESIGN_H));
        setLayout(new BorderLayout());

        scanTitleEdges(TITLE_IMAGE);

        // Layers positioned with PercentLayout so they scale with the window.
        PercentLayout percent = new PercentLayout();
        percent.setDesignSize(DESIGN_W, DESIGN_H);

        JLayeredPane layers = new JLayeredPane();
        layers.setPreferredSize(new Dimension(DESIGN_W, DESIGN_H));
        layers.setLayout(percent);

        BackgroundPanel backgroundPanel =
                new BackgroundPanel("/splashscreen/backgroundpanel.png");
        titleLayer = new FlickerTitlePanel(TITLE_IMAGE);
        LoaderOverlay loaderOverlay = new LoaderOverlay();

        layers.setLayer(backgroundPanel, 0);
        layers.setLayer(titleLayer, 1);
        layers.setLayer(loaderOverlay, 2);

        percent.addPixel(layers, backgroundPanel, 0, 0, DESIGN_W, DESIGN_H);
        percent.addPixel(layers, titleLayer, 0, 0, DESIGN_W, DESIGN_H);
        percent.addPixel(layers, loaderOverlay, 0, 0, DESIGN_W, DESIGN_H);

        add(layers, BorderLayout.CENTER);
        AudioManager.playBackground("loadintro.wav");

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent event) {
                AudioManager.playBackground("loadintro.wav");
            }
        });

        startedAt = System.currentTimeMillis();
        nextFlickerAt = startedAt
                + randomBetween(FIRST_FLICKER_MIN_MS, FIRST_FLICKER_MAX_MS);

        animationTimer = new Timer(FRAME_MS, this::advanceAnimation);
        animationTimer.start();
    }

    // =========================================================
    // TITLE EDGE SCAN (puts sparks on the actual lettering)
    // =========================================================

    private void scanTitleEdges(String path) {

        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                return;
            }

            BufferedImage img = ImageIO.read(url);
            if (img == null) {
                return;
            }

            int w = img.getWidth();
            int h = img.getHeight();
            boolean hasAlpha = img.getColorModel().hasAlpha();

            float sx = DESIGN_W / (float) w;
            float sy = DESIGN_H / (float) h;

            final int step = 2;     // sampling stride
            final int probe = 3;    // neighbor distance for edge test

            for (int y = probe; y < h - probe; y += step) {
                for (int x = probe; x < w - probe; x += step) {

                    if (!isLit(img, x, y, hasAlpha, 170)) {
                        continue;
                    }

                    boolean edge =
                            !isLit(img, x - probe, y, hasAlpha, 170)
                            || !isLit(img, x + probe, y, hasAlpha, 170)
                            || !isLit(img, x, y - probe, hasAlpha, 170)
                            || !isLit(img, x, y + probe, hasAlpha, 170);

                    if (edge) {
                        titleEdgePoints.add(new float[] { x * sx, y * sy });
                    }
                }
            }

        } catch (IOException | RuntimeException ex) {
            titleEdgePoints.clear();
        }
    }

    private static boolean isLit(BufferedImage img, int x, int y,
                                 boolean hasAlpha, int threshold) {

        int argb = img.getRGB(x, y);

        if (hasAlpha) {
            return (argb >>> 24) >= threshold;
        }

        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (r + g + b) / 3 >= 90;
    }

    // =========================================================
    // ANIMATION CONTROLLER
    // =========================================================

    private void advanceAnimation(ActionEvent event) {

        long now = System.currentTimeMillis();
        long elapsed = now - startedAt;

        if (!fading
                && elapsed >= SPLASH_DURATION_MS - FADE_DURATION_MS) {

            fading = true;
            startedAt = now;
            elapsed = 0;

            flickering = false;
            titleLayer.setOpacity(1f);
        }

        if (fading) {

            opacity = Math.max(0f, 1f - (float) elapsed / FADE_DURATION_MS);

            if (opacity <= 0f) {
                animationTimer.stop();
                cardLayout.show(cardContainer, "MENU");
                return;
            }

        } else {
            updateTitleFlicker(now);
        }

        updateSparks();

        animationOffset = (animationOffset + 12) % 96;

        updateLoadingStatus();

        titleLayer.repaint();
        repaint();
    }

    // =========================================================
    // NEON TITLE FLICKER
    // =========================================================

    private void updateTitleFlicker(long now) {

        if (!flickering && now >= nextFlickerAt) {

            flickering = true;

            flickerEndsAt = now + randomBetween(
                    FLICKER_DURATION_MIN_MS, FLICKER_DURATION_MAX_MS);

            nextFlickerToggleAt = now;
            flickerLit = true;

            spawnSparks(randomBetween(9, 15));
        }

        if (!flickering) {
            return;
        }

        if (now >= flickerEndsAt) {

            flickering = false;
            titleLayer.setOpacity(1f);

            nextFlickerAt = now + randomBetween(
                    FLICKER_DELAY_MIN_MS, FLICKER_DELAY_MAX_MS);

            return;
        }

        if (now >= nextFlickerToggleAt) {

            flickerLit = random.nextFloat() > 0.42f;

            if (flickerLit) {

                titleLayer.setOpacity(0.68f + random.nextFloat() * 0.32f);

            } else {

                titleLayer.setOpacity(random.nextFloat() * 0.12f);

                if (random.nextFloat() < 0.55f) {
                    spawnSparks(randomBetween(1, 3));
                }
            }

            nextFlickerToggleAt = now + randomBetween(35, 90);
        }
    }

    // =========================================================
    // SPARK PARTICLES (design-space coordinates)
    // =========================================================

    private void spawnSparks(int count) {

        for (int i = 0; i < count; i++) {

            float x;
            float y;

            if (!titleEdgePoints.isEmpty()) {

                float[] p = titleEdgePoints.get(
                        random.nextInt(titleEdgePoints.size()));

                x = p[0];
                y = p[1];

            } else {

                x = TITLE_SPARK_LEFT + random.nextFloat()
                        * (TITLE_SPARK_RIGHT - TITLE_SPARK_LEFT);
                y = TITLE_SPARK_TOP + random.nextFloat()
                        * (TITLE_SPARK_BOTTOM - TITLE_SPARK_TOP);
            }

            double angle = random.nextDouble() * Math.PI * 2;
            float speed = 0.8f + random.nextFloat() * 2.6f;

            float vx = (float) Math.cos(angle) * speed;
            float vy = (float) Math.sin(angle) * speed - 0.4f;

            float length = 7f + random.nextFloat() * 15f;
            int lifetime = randomBetween(160, 330);

            Color color = SPARK_COLORS[random.nextInt(SPARK_COLORS.length)];

            sparks.add(new Spark(x, y, vx, vy, length, lifetime, color));
        }
    }

    private void updateSparks() {

        Iterator<Spark> iterator = sparks.iterator();

        while (iterator.hasNext()) {

            Spark spark = iterator.next();
            spark.update();

            if (!spark.isAlive()) {
                iterator.remove();
            }
        }
    }

    // Caller must already have scaled g to design space.
    private void paintSparks(Graphics2D graphics) {

        for (Spark spark : sparks) {

            float lifeRatio =
                    Math.max(0f, spark.life / (float) spark.maxLife);

            float alpha = lifeRatio * opacity;

            if (alpha <= 0f) {
                continue;
            }

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            float speed = (float) Math.sqrt(
                    spark.vx * spark.vx + spark.vy * spark.vy);

            if (speed < 0.01f) {
                speed = 0.01f;
            }

            float tailX = spark.x - (spark.vx / speed) * spark.length;
            float tailY = spark.y - (spark.vy / speed) * spark.length;

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, Math.min(1f, alpha * 0.45f)));

            g.setStroke(new BasicStroke(
                    5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.setColor(spark.color);

            g.drawLine(Math.round(tailX), Math.round(tailY),
                       Math.round(spark.x), Math.round(spark.y));

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, Math.min(1f, alpha)));

            g.setStroke(new BasicStroke(
                    1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.setColor(Color.WHITE);

            g.drawLine(Math.round(tailX), Math.round(tailY),
                       Math.round(spark.x), Math.round(spark.y));

            g.fillOval(Math.round(spark.x - 2f),
                       Math.round(spark.y - 2f), 4, 4);

            g.dispose();
        }
    }

    // =========================================================
    // LOADING STATUS
    // =========================================================

    private void updateLoadingStatus() {

        long elapsed = System.currentTimeMillis() - startedAt;

        float progress = fading
                ? 1f
                : Math.min(1f, (float) elapsed
                        / (SPLASH_DURATION_MS - FADE_DURATION_MS));

        if (progress >= 0.90f) {
            loadingStatus = "READY";
        } else if (progress >= 0.75f) {
            loadingStatus = "FINALIZING...";
        } else if (progress >= 0.45f) {
            loadingStatus = "INITIALIZING...";
        } else {
            loadingStatus = "LOADING...";
        }
    }

    // =========================================================
    // LOADER OVERLAY
    // =========================================================

    private class LoaderOverlay extends JPanel {

        private LoaderOverlay() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Dark overlay during the final fade.
            g.setColor(new Color(0, 0, 0,
                    Math.round((1f - opacity) * 255)));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, opacity));

            g.setPaint(new GradientPaint(
                    0, getHeight() - 260, new Color(8, 0, 18, 0),
                    0, getHeight(), new Color(8, 0, 18, 190)));

            g.fillRect(0, getHeight() - 260, getWidth(), 260);

            // Sparks live in design space; scale them to the real size.
            Graphics2D sparkG = (Graphics2D) g.create();
            sparkG.scale(getWidth() / (double) DESIGN_W,
                         getHeight() / (double) DESIGN_H);
            paintSparks(sparkG);
            sparkG.dispose();

            long elapsed = System.currentTimeMillis() - startedAt;

            float progress = fading
                    ? 1f
                    : Math.min(1f, (float) elapsed
                            / (SPLASH_DURATION_MS - FADE_DURATION_MS));

            paintLoader(g, progress, getWidth(), getHeight());

            g.dispose();
        }
    }

    // =========================================================
    // LOADING BAR
    // =========================================================

    private void paintLoader(Graphics2D g, float progress,
                             int width, int height) {

        int loaderWidth = Math.min(620, width - 120);
        int loaderX = (width - loaderWidth) / 2;
        int loaderY = height / 2 + 300;
        int loaderHeight = 12;

        g.setColor(new Color(13, 3, 20, 180));
        g.fillRoundRect(loaderX, loaderY, loaderWidth, loaderHeight, 6, 6);

        int filledWidth = Math.round(loaderWidth * progress);

        if (filledWidth > 0) {

            g.setPaint(new GradientPaint(
                    loaderX, loaderY, new Color(255, 120, 225),
                    loaderX + loaderWidth, loaderY, new Color(255, 20, 165)));

            g.fillRoundRect(loaderX, loaderY, filledWidth, loaderHeight, 8, 8);
        }

        if (filledWidth > 8) {

            int shineX = loaderX
                    + Math.min(filledWidth - 8, Math.max(0, animationOffset));

            g.setColor(new Color(255, 235, 252, 210));
            g.fillRoundRect(shineX, loaderY + 2, 6, loaderHeight - 4, 4, 4);
        }

        paintLoadingText(g, loaderX + loaderWidth / 2, loaderY - 18);

        g.setColor(LOADING_PINK);
        g.setFont(new Font(LOADING_FONT, Font.BOLD, 16));

        String percentage = Math.round(progress * 100) + "%";

        int textX = loaderX
                + (loaderWidth - g.getFontMetrics().stringWidth(percentage)) / 2;

        g.drawString(percentage, textX, loaderY + loaderHeight + 32);
    }

    private void paintLoadingText(Graphics2D g, int centerX, int baseline) {

        g.setFont(new Font(LOADING_FONT, Font.BOLD, 24));

        int textX = centerX
                - g.getFontMetrics().stringWidth(loadingStatus) / 2;

        g.setColor(LOADING_PINK);
        g.drawString(loadingStatus, textX, baseline);
    }

    // =========================================================
    // NEON TITLE PANEL
    // =========================================================

    private static class FlickerTitlePanel extends BackgroundPanel {

        private float titleOpacity = 1f;

        private FlickerTitlePanel(String imagePath) {
            super(imagePath);
            setOpaque(false);
        }

        private void setOpacity(float value) {
            titleOpacity = Math.max(0f, Math.min(1f, value));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, titleOpacity));

            super.paintComponent(g);

            g.dispose();
        }
    }

    // =========================================================
    // INDIVIDUAL ELECTRIC SPARK
    // =========================================================

    private static class Spark {

        private float x;
        private float y;
        private float vx;
        private float vy;

        private final float length;
        private final long maxLife;
        private long life;
        private final Color color;

        private Spark(float x, float y, float vx, float vy,
                      float length, long lifetime, Color color) {

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

            vy += 0.09f;
            vx *= 0.985f;

            life -= FRAME_MS;
        }

        private boolean isAlive() {
            return life > 0;
        }
    }

    private int randomBetween(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}