package com.cysaaa.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private BufferedImage hoverImage;
    private boolean hovering = false;

    // Minimum alpha value (0-255) for a pixel to count as "clickable".
    // Pixels with alpha at or below this are treated as transparent/empty space.
    private static final int ALPHA_THRESHOLD = 10;

    // Set to true to make mouse events only register on non-transparent pixels
    // of the image, instead of the full rectangular bounds.
    private boolean pixelPreciseHitTest = false;

    // Use this constructor if you don't need a hover state
    public ImagePanel(String imagePath) {
        this(imagePath, null);
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

        setOpaque(false); // transparent parts of the PNG show what's behind it

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

    // Call this to enable shape-accurate (alpha-based) hover/click detection,
    // so transparent parts of the image no longer respond to mouse events.
    public void setPixelPreciseHitTest(boolean enabled) {
        this.pixelPreciseHitTest = enabled;
    }

    @Override
    public boolean contains(int x, int y) {
        if (!pixelPreciseHitTest || image == null) {
            return super.contains(x, y); // default rectangular hit test
        }

        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return false;
        }

        // Scale the panel's click coordinates to the image's actual pixel
        // coordinates, since the image is drawn stretched to the panel's size.
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
        BufferedImage toDraw = (hovering && hoverImage != null) ? hoverImage : image;
        if (toDraw != null) {
            g.drawImage(toDraw, 0, 0, getWidth(), getHeight(), this);
        }
    }
}