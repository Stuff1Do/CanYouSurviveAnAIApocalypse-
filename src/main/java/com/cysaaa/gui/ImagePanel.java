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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage toDraw = (hovering && hoverImage != null) ? hoverImage : image;
        if (toDraw != null) {
            g.drawImage(toDraw, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
