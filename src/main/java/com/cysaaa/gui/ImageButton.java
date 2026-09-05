package com.cysaaa.gui;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ImageButton
 *
 * JButton using PNG
 */
public class ImageButton extends JButton {

    private ImageIcon normalIcon;

    public ImageButton(String imagePath, int width, int height) {
        // load and scale base image
        Image rawImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        Image scaledImage = rawImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        normalIcon = new ImageIcon(scaledImage);

        setIcon(normalIcon);
       
        // strip default button chrome so only the PNG shows
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setSize(width, height);

        //cursor feedback
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            }
        });
    }

}