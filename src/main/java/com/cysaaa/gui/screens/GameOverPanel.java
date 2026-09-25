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

    public GameOverPanel(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/game_over.png");

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        setLayout(percentLayout);

        // Play Again -> reset state, back to host selection
        playAgainButton = new ImagePanel("/buttons/play_again.png", "/buttons/play_again_hover.png");
        percentLayout.addPixel(this, playAgainButton, 978, 779, 377, 96); 
        playAgainButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().reset();
                
                cardLayout.show(mainPanel, "HOST_SELECTION");
            }
        });

        // Main Menu -> back to main menu
        mainMenuButton = new ImagePanel("/buttons/main_menu_button.png", "/buttons/main_menu_button_hover.png");
        percentLayout.addPixel(this, mainMenuButton, 567, 772, 369, 96);
        mainMenuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().reset();
                cardLayout.show(mainPanel, "MENU");
            }
        });
    }
}