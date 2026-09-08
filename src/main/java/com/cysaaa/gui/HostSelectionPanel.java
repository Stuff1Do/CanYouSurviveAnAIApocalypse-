package com.cysaaa.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class HostSelectionPanel extends BackgroundPanel {

    public HostSelectionPanel(JPanel cardContainer, CardLayout cardLayout) {
        super("/backgrounds/HostSelection.png");

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);
        setOpaque(false);

    
        ImagePanel host1 = new ImagePanel("/buttons/host1.png", "/buttons/host1hover.png");
        layout.addPixel(this, host1, 46, 324, 773, 620);
        host1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Host 1 selected!");
                // e.g. cardLayout.show(cardContainer, "GAMEPLAY");
                // store selected host somewhere before switching
            }
        });

        ImagePanel host2 = new ImagePanel("/buttons/host2.png", "/buttons/host2hover.png");
        layout.addPixel(this, host2, 715, 324, 511, 728);
        host2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Host 2 selected!");
                // e.g. cardLayout.show(cardContainer, "GAMEPLAY");
                // store selected host somewhere before switching
            }
        });

        ImagePanel host3 = new ImagePanel("/buttons/host3.png", "/buttons/host3hover.png");
        layout.addPixel(this, host3, 1118, 327, 760, 616);
        host3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Host 3 selected!");
                // e.g. cardLayout.show(cardContainer, "GAMEPLAY");
                // store selected host somewhere before switching
            }
        });
    }
}