package com.cysaaa;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.cysaaa.gui.MainMenuPanel;
import com.cysaaa.gui.ProgressPanel;
import com.cysaaa.terminal.TApp;
import com.cysaaa.gui.GameplayPanel;
import com.cysaaa.gui.HostSelectionPanel;
import com.cysaaa.gui.InstructionsPanel;
import com.cysaaa.gui.SplashScreenPanel;

public class App {
    public static void main(String[] args) {
        if(args.length > 0){
           TApp terminalApp = new TApp();
           terminalApp.run();
        }else{
             Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
            JFrame frame = new JFrame("Can You Survive an AI Apocalypse?");
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(screenSize.width, screenSize.height);
            frame.setResizable(false);

            JPanel cardContainer = new JPanel(new CardLayout());
            CardLayout cardLayout = (CardLayout) cardContainer.getLayout();

            SplashScreenPanel splashScreenPanel = new SplashScreenPanel(cardContainer, cardLayout);
            splashScreenPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            MainMenuPanel mainMenuPanel = new MainMenuPanel(cardContainer, cardLayout);
            mainMenuPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            InstructionsPanel instructionsPanel = new InstructionsPanel(cardContainer, cardLayout);
            instructionsPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            GameplayPanel gameplayPanel = new GameplayPanel(cardContainer, cardLayout);
            gameplayPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            ProgressPanel progressPanel = new ProgressPanel(cardContainer, cardLayout, gameplayPanel);
            progressPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            gameplayPanel.setProgressPanel(progressPanel); // NEW — resolves the circular reference

            HostSelectionPanel hostSelectionPanel = new HostSelectionPanel(cardContainer, cardLayout, progressPanel); // NEW param
            hostSelectionPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            cardContainer.add(splashScreenPanel, "SPLASH");
            cardContainer.add(mainMenuPanel, "MENU");
            cardContainer.add(instructionsPanel, "INSTRUCTIONS");
            cardContainer.add(hostSelectionPanel, "HOST_SELECTION");
            cardContainer.add(progressPanel, "PROGRESS");
            cardContainer.add(gameplayPanel, "GAMEPLAY");

            frame.setContentPane(cardContainer);
            frame.setVisible(true);
            cardLayout.show(cardContainer, "SPLASH");
        }

    }
}