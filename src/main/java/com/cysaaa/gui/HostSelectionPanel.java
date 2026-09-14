package com.cysaaa.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import com.cysaaa.util.Host;
import com.cysaaa.util.GameState;

public class HostSelectionPanel extends BackgroundPanel {

    public HostSelectionPanel(JPanel cardContainer, CardLayout cardLayout) {
        super("/backgrounds/hostSelection.png");

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);
        setOpaque(false);

        ImagePanel host1 = new ImagePanel("/buttons/host1.png", "/buttons/host1Hover.png");
        layout.addPixel(this, host1, 81, 340, 728, 612);
        host1.setPixelPreciseHitTest(true);
        host1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showConfirmation(Host.HOST_1, cardContainer, cardLayout);
            }
        });

        ImagePanel host2 = new ImagePanel("/buttons/host2.png", "/buttons/host2Hover.png");
        layout.addPixel(this, host2, 710, 320, 506, 729);
        host2.setPixelPreciseHitTest(true);
        host2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showConfirmation(Host.HOST_2, cardContainer, cardLayout);
            }
        });

        ImagePanel host3 = new ImagePanel("/buttons/host3.png", "/buttons/host3Hover.png");
        layout.addPixel(this, host3, 1102, 329, 728, 612);
        host3.setPixelPreciseHitTest(true);
        host3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showConfirmation(Host.HOST_3, cardContainer, cardLayout);
            }
        });
    }

    // Shows a confirmation overlay on top of this panel before committing the host selection
    private void showConfirmation(Host selectedHost, JPanel cardContainer, CardLayout cardLayout) {
        JRootPane root = SwingUtilities.getRootPane(this);
        JLayeredPane layeredPane = root.getLayeredPane();

        ConfirmationOverlay overlay = new ConfirmationOverlay(
            selectedHost,
            () -> { // onYes
                StateManager.getInstance().setCurrentHost(selectedHost);
                StateManager.getInstance().setScreenState(GameState.PLAYING);
                cardLayout.show(cardContainer, "PROGRESS");
            }
        );

        overlay.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();
    }
}