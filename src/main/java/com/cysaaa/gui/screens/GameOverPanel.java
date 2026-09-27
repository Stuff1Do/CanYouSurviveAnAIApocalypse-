package com.cysaaa.gui.screens;

import com.cysaaa.gui.components.BackgroundPanel;
import com.cysaaa.gui.components.PercentLayout;
import com.cysaaa.gui.components.ImagePanel;
import com.cysaaa.util.StateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameOverPanel extends BackgroundPanel {

    private PercentLayout percentLayout;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private ImagePanel playAgainButton;
    private ImagePanel mainMenuButton;

    public GameOverPanel(JPanel mainPanel, CardLayout cardLayout, GameplayPanel gameplayPanel) {
        super("/backgrounds/game_over.png");

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        percentLayout.setDesignSize(1920, 1080);
        setLayout(percentLayout);

        // Play Again -> reset state, back to host selection
        playAgainButton = new ImagePanel("/endings/playagain3.png");
        playAgainButton.setPixelPreciseHitTest(true);
        playAgainButton.setHoverHighlight(true);
        percentLayout.addPixel(this, playAgainButton, 978, 772, 369, 98);
        playAgainButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameplayPanel.startNewGame();
                cardLayout.show(mainPanel, "HOST_SELECTION");
            }
        });

        // Main Menu -> back to main menu
        mainMenuButton = new ImagePanel("/endings/mainmenu1.png");
        mainMenuButton.setPixelPreciseHitTest(true);
        mainMenuButton.setHoverHighlight(true);
        percentLayout.addPixel(this, mainMenuButton, 567, 772, 369, 98);
        mainMenuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().reset();
                cardLayout.show(mainPanel, "MENU");
            }
        });
    }
}