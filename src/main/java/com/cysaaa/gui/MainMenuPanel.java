package com.cysaaa.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class MainMenuPanel extends BackgroundPanel {

    public MainMenuPanel(JPanel cardContainer, CardLayout cardLayout) {
        super("/backgrounds/MainMenu.png");

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);
        setOpaque(false);

        ImagePanel playButton = new ImagePanel("/buttons/start_button.png", "/buttons/startHover.png");
        layout.addPixel(this, playButton, 230, 474, 470, 145);
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardContainer, "HOST_SELECTION");
            }
        });

        ImagePanel howToPlayButton = new ImagePanel("/buttons/how_to_play.png", "/buttons/htpHover.png");
        layout.addPixel(this, howToPlayButton, 262, 655, 406, 126);
        howToPlayButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardContainer, "INSTRUCTIONS");
            }
        });

        ImagePanel exitButton = new ImagePanel("/buttons/exit_button.png", "/buttons/exitHover.png");
        layout.addPixel(this, exitButton, 263, 799, 406, 126);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
    }
}
