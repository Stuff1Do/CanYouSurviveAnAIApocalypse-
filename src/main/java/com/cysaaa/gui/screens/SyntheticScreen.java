package com.cysaaa.gui.screens;

import com.cysaaa.gui.components.BackgroundPanel;
import com.cysaaa.gui.components.PercentLayout;
import com.cysaaa.gui.components.ImagePanel;
import com.cysaaa.util.StateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SyntheticScreen extends BackgroundPanel {

    private PercentLayout percentLayout;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private ImagePanel playAgainButton;
    private ImagePanel mainMenuButton;

    public SyntheticScreen(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/checkpoint2.png");

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        percentLayout.setDesignSize(1920, 1080);
        setLayout(percentLayout);

        ImagePanel exitButton = new ImagePanel("/endings/exit1.png");
        exitButton.setPixelPreciseHitTest(true);
        exitButton.setHoverHighlight(true);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StateManager.getInstance().reset();
                cardLayout.show(mainPanel, "MENU");
            }
        });
        percentLayout.addPixel(this, exitButton, 776, 748, 451, 140);
    }
}