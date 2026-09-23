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

    private static final int BOX_WIDTH = 700;
    private static final int BOX_HEIGHT = 410;

    // Existing constructor — host selection confirmation
    public ConfirmationOverlay(Host selectedHost, ConfirmCallback onYes) {
        this(getConfirmImagePath(selectedHost), onYes);
    }

    // New constructor — generic confirmation
    public ConfirmationOverlay(String imagePath, ConfirmCallback onYes) {
        setOpaque(false);

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);

        ImagePanel confirmBox = new ImagePanel(imagePath);
        layout.addPixelCentered(this, confirmBox, BOX_WIDTH, BOX_HEIGHT);

        PercentLayout boxLayout = new PercentLayout();
        boxLayout.setDesignSize(BOX_WIDTH, BOX_HEIGHT);
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