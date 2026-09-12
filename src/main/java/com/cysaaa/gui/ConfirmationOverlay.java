package com.cysaaa.gui;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import com.cysaaa.util.Host;

public class ConfirmationOverlay extends JPanel {

    // Called only when the player confirms (clicks Yes).
    // The overlay closes itself either way - no need to close it manually.
    public interface ConfirmCallback {
        void onConfirm();
    }

    // Design size of the confirm box art itself (not the full 1920x1080 screen).
    // Update these to match your actual image's pixel dimensions.
    private static final int BOX_WIDTH = 700;
    private static final int BOX_HEIGHT = 410;

    public ConfirmationOverlay(Host selectedHost, ConfirmCallback onYes) {
        setOpaque(false); // no dimming - background stays fully visible behind the confirm box

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);

        // Pick the correct confirm image based on which host was selected
        String imagePath = getConfirmImagePath(selectedHost);
        ImagePanel confirmBox = new ImagePanel(imagePath);
        layout.addPixelCentered(this, confirmBox, BOX_WIDTH, BOX_HEIGHT);

        // Give the box its own local layout, scaled to the box's own pixel size
        PercentLayout boxLayout = new PercentLayout();
        boxLayout.setDesignSize(BOX_WIDTH, BOX_HEIGHT);
        confirmBox.setLayout(boxLayout);

        // Invisible click zones over the "NO" and "YES" text baked into the image.
        // Adjust these x/y/w/h values to match where NO/YES actually sit in your art.
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

    private String getConfirmImagePath(Host host) {
        switch (host) {
            case HOST_1: return "/popups/host1confirm.png";
            case HOST_2: return "/popups/host2confirm.png";
            case HOST_3: return "/popups/host3confirm.png";
            default: throw new IllegalArgumentException("Unknown host: " + host);
        }
    }

    // Removes this overlay from whatever container it was added to (e.g. the JLayeredPane)
    public void close() {
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }
}