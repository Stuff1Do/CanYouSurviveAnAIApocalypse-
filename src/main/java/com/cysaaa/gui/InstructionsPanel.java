package com.cysaaa.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class InstructionsPanel extends BackgroundPanel {

    public InstructionsPanel(JPanel cardContainer, CardLayout cardLayout) {
        super("/backgrounds/htpPanel.png");

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);
        setOpaque(false);

        ImagePanel backButton = new ImagePanel("/buttons/backbutton.png", "/buttons/backHover.png");
        layout.addPixel(this, backButton, 108, 87, 326, 101);

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardContainer, "MENU");
            }
        });
    }
}