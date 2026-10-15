package com.cysaaa.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import com.cysaaa.util.AudioManager;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private BufferedImage hoverImage;
    private boolean hovering = false;
    private boolean selected = false;

    // Minimum alpha value (0-255) for a pixel to count as "clickable".
    private static final int ALPHA_THRESHOLD = 10;

    private boolean pixelPreciseHitTest = false;

    // Use this constructor if you don't need a hover state
    public ImagePanel(String imagePath) {
        this(imagePath, null);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    // Use this constructor for a hover-swap image
    public ImagePanel(String imagePath, String hoverImagePath) {
        try {
            URL imgUrl = getClass().getResource(imagePath);
            image = ImageIO.read(imgUrl);

            if (hoverImagePath != null) {
                URL hoverUrl = getClass().getResource(hoverImagePath);
                hoverImage = ImageIO.read(hoverUrl);
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AudioManager.playEffect("click.wav");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    public void setImage(String imagePath) {
        try {
            URL imgUrl = getClass().getResource(imagePath);
            image = ImageIO.read(imgUrl);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        repaint();
    }

    public void setHoverImage(String hoverImagePath) {
        try {
            if (hoverImagePath != null) {
                URL hoverUrl = getClass().getResource(hoverImagePath);
                hoverImage = ImageIO.read(hoverUrl);
            } else {
                hoverImage = null;
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        repaint();
    }

    // Use this constructor when no image should be shown yet
    public ImagePanel() {
        this.image = null;
        this.hoverImage = null;
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    public void setPixelPreciseHitTest(boolean enabled) {
        this.pixelPreciseHitTest = enabled;
    }

    public void setHoverHighlight(boolean enabled) {
        if (enabled && image != null && hoverImage == null) {
            RescaleOp brighten = new RescaleOp(1.12f, 18f, null);
            hoverImage = brighten.filter(image, null);
        }
        repaint();
    }

    @Override
    public boolean contains(int x, int y) {
        if (!pixelPreciseHitTest || image == null) {
            return super.contains(x, y);
        }

        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return false;
        }

        int imgX = (int) ((double) x / getWidth() * image.getWidth());
        int imgY = (int) ((double) y / getHeight() * image.getHeight());

        imgX = Math.min(Math.max(imgX, 0), image.getWidth() - 1);
        imgY = Math.min(Math.max(imgY, 0), image.getHeight() - 1);

        int pixel = image.getRGB(imgX, imgY);
        int alpha = (pixel >> 24) & 0xff;

        return alpha > ALPHA_THRESHOLD;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage toDraw = (hovering || selected) && hoverImage != null ? hoverImage : image;
        if (toDraw != null) {
            g.drawImage(toDraw, 0, 0, getWidth(), getHeight(), this);
        }
    }
}