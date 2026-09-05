package com.cysaaa;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

import main.java.com.cysaaa.gui.BackgroundPanel;

/**
 *  Main Class 
 *
 *  Creates main menu window
 */
public class App 
{
    public static void main( String[] args )
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        /* 
        double screenWidth = screenSize.width;
        double screenHeight = screenSize.height;
        */

        JFrame frame = new JFrame("Can You Survive an AI Apocalypse?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(false);
        frame.setVisible(true);

        BackgroundPanel bgPanel = new BackgroundPanel("/background/Splash screen.png");
        bgPanel.setBounds(0, 0, screenSize.width, screenSize.height);
        bgPanel.setLayout(null);
        bgPanel.setOpaque(false);

        frame.setContentPane(bgPanel);
        frame.setLayout(null);
    }
}
