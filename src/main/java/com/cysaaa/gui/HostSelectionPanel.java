package com.cysaaa.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import com.cysaaa.util.Host;
import com.cysaaa.util.GameState;

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
                StateManager.getInstance().setCurrentHost(Host.HOST_1);
                StateManager.getInstance().setScreenState(GameState.PLAYING);
                //cardLayout.show(cardContainer, "GAMEPLAY");
            }
        });

        ImagePanel host2 = new ImagePanel("/buttons/host2.png", "/buttons/host2hover.png");
        layout.addPixel(this, host2, 715, 324, 511, 728);
        host2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().setCurrentHost(Host.HOST_2);
                StateManager.getInstance().setScreenState(GameState.PLAYING);
                //cardLayout.show(cardContainer, "GAMEPLAY");
            }
        });

        ImagePanel host3 = new ImagePanel("/buttons/host3.png", "/buttons/host3hover.png");
        layout.addPixel(this, host3, 1118, 327, 760, 616);
        host3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Host 3 selected!");
                StateManager.getInstance().setCurrentHost(Host.HOST_2);
                StateManager.getInstance().setScreenState(GameState.PLAYING);
                //cardLayout.show(cardContainer, "GAMEPLAY");
            }
        });
    }
}