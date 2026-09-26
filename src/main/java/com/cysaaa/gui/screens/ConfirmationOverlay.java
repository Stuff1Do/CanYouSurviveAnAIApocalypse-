package com.cysaaa.gui.screens;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import com.cysaaa.util.Host;
import com.cysaaa.gui.components.*;

public class ConfirmationOverlay extends JPanel {

    public interface ConfirmCallback {
        void onConfirm();
    }

    private static final int HOST_BOX_WIDTH = 700;
    private static final int HOST_BOX_HEIGHT = 410;

    private final int boxWidth;
    private final int boxHeight;

    // Existing constructor — host selection confirmation (Yes/No)
    public ConfirmationOverlay(Host selectedHost, ConfirmCallback onYes) {
        this(getConfirmImagePath(selectedHost), HOST_BOX_WIDTH, HOST_BOX_HEIGHT, onYes);
    }

    // Generic Yes/No confirmation, default host-confirm-sized box
    public ConfirmationOverlay(String imagePath, ConfirmCallback onYes) {
        this(imagePath, HOST_BOX_WIDTH, HOST_BOX_HEIGHT, onYes);
    }

    // Generic Yes/No confirmation, explicit size
    public ConfirmationOverlay(String imagePath, int boxWidth, int boxHeight, ConfirmCallback onYes) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        setOpaque(false);

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);

        ImagePanel confirmBox = new ImagePanel(imagePath);
        layout.addPixelCentered(this, confirmBox, boxWidth, boxHeight);

        PercentLayout boxLayout = new PercentLayout();
        boxLayout.setDesignSize(boxWidth, boxHeight);
        confirmBox.setLayout(boxLayout);

        JPanel noHitbox = new JPanel();
        noHitbox.setOpaque(false);
        boxLayout.addPixel(confirmBox, noHitbox, 90, 290, 200, 80);
        noHitbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                close();
            }
        });

        JPanel yesHitbox = new JPanel();
        yesHitbox.setOpaque(false);
        boxLayout.addPixel(confirmBox, yesHitbox, 420, 290, 200, 80);
        yesHitbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onYes.onConfirm();
                close();
            }
        });
    }

    // New — single-button (OK only) confirmation, e.g. Parallel Processing rules popup.
    // okX/okY/okW/okH are the OK button's hitbox, measured within the image at boxWidth×boxHeight.
    public ConfirmationOverlay(String imagePath, int boxWidth, int boxHeight,
                                int okX, int okY, int okW, int okH, ConfirmCallback onOk) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        setOpaque(false);

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);

        ImagePanel confirmBox = new ImagePanel(imagePath);
        layout.addPixelCentered(this, confirmBox, boxWidth, boxHeight);

        PercentLayout boxLayout = new PercentLayout();
        boxLayout.setDesignSize(boxWidth, boxHeight);
        confirmBox.setLayout(boxLayout);

        JPanel okHitbox = new JPanel();
        okHitbox.setOpaque(false);
        boxLayout.addPixel(confirmBox, okHitbox, okX, okY, okW, okH);
        okHitbox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onOk.onConfirm();
                close();
            }
        });
    }

    private static String getConfirmImagePath(Host host) {
        switch (host) {
            case HOST_1: return "/popups/host1confirm.png";
            case HOST_2: return "/popups/host2confirm.png";
            case HOST_3: return "/popups/host3confirm.png";
            default: throw new IllegalArgumentException("Unknown host: " + host);
        }
    }

    public void close() {
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }
}