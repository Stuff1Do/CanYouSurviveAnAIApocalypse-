package com.cysaaa.gui.screens;

import com.cysaaa.gui.components.BackgroundPanel;
import com.cysaaa.gui.components.PercentLayout;
import com.cysaaa.gui.components.ImagePanel;
import com.cysaaa.util.StateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VictoryScreen extends BackgroundPanel {

    private PercentLayout percentLayout;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private ImagePanel playAgainButton;
    private ImagePanel mainMenuButton;

    public VictoryScreen(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/victory_screen.png");

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        percentLayout.setDesignSize(1920, 1080);
        setLayout(percentLayout);

        ImagePanel mainMenuButton = new ImagePanel("/endings/mainmenu1.png", "/endings/mainmenu1_hover.png");
        mainMenuButton.setPixelPreciseHitTest(true);
        mainMenuButton.setHoverHighlight(true);
        mainMenuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().reset();
                cardLayout.show(mainPanel, "MENU");
            }
        });
        percentLayout.addPixel(this, mainMenuButton, 382, 732, 369, 97);

        ImagePanel playAgainButton = new ImagePanel("/endings/playagain2.png", "/endings/playagain2_hover.png");
        playAgainButton.setPixelPreciseHitTest(true);
        playAgainButton.setHoverHighlight(true);
        playAgainButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().reset();
                cardLayout.show(mainPanel, "HOST_SELECTION");
            }
        });
        percentLayout.addPixel(this, playAgainButton, 792, 732, 369, 97);
    }
}