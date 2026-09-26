package com.cysaaa;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.cysaaa.terminal.TApp;
import com.cysaaa.util.QuestionLoader;
import com.cysaaa.gui.screens.*;

public class App {
    public static void main(String[] args) {
        if(args.length > 0){
           TApp terminalApp = new TApp();
           terminalApp.run();
        }else{

            QuestionLoader.loadQuestions("/data/questions.csv");

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
            JFrame frame = new JFrame("Who wants To Survive an AI Apocalypse?");
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(screenSize.width, screenSize.height);
            frame.setResizable(false);

            JPanel cardContainer = new JPanel(new CardLayout());
            CardLayout cardLayout = (CardLayout) cardContainer.getLayout();

            SplashScreenPanel splashScreenPanel = new SplashScreenPanel(cardContainer, cardLayout);
            splashScreenPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            GameplayPanel gameplayPanel = new GameplayPanel(cardContainer, cardLayout);
            gameplayPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            MainMenuPanel mainMenuPanel = new MainMenuPanel(cardContainer, cardLayout, gameplayPanel);
            mainMenuPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            InstructionsPanel instructionsPanel = new InstructionsPanel(cardContainer, cardLayout);
            instructionsPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            

            ProgressPanel progressPanel = new ProgressPanel(cardContainer, cardLayout, gameplayPanel);
            progressPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            gameplayPanel.setProgressPanel(progressPanel); // NEW — resolves the circular reference

            HostSelectionPanel hostSelectionPanel = new HostSelectionPanel(cardContainer, cardLayout, progressPanel); // NEW param
            hostSelectionPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            GameOverPanel gameOverPanel = new GameOverPanel(cardContainer, cardLayout, gameplayPanel);
            gameOverPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            CyberneticScreen cybernetic = new CyberneticScreen(cardContainer, cardLayout);
            cybernetic.setBounds(0, 0, screenSize.width, screenSize.height);

            SyntheticScreen synthetic = new SyntheticScreen(cardContainer, cardLayout);
            synthetic.setBounds(0, 0, screenSize.width, screenSize.height);

            VictoryScreen victory = new VictoryScreen(cardContainer, cardLayout);
            victory.setBounds(0, 0, screenSize.width, screenSize.height);

            cardContainer.add(gameOverPanel, "GAME_OVER");
            cardContainer.add(splashScreenPanel, "SPLASH");                                         
            cardContainer.add(mainMenuPanel, "MENU");
            cardContainer.add(instructionsPanel, "INSTRUCTIONS");
            cardContainer.add(hostSelectionPanel, "HOST_SELECTION");
            cardContainer.add(progressPanel, "PROGRESS");
            cardContainer.add(gameplayPanel, "GAMEPLAY");
            cardContainer.add(cybernetic, "CHECKPOINT_1");
            cardContainer.add(synthetic, "CHECKPOINT_2");
            cardContainer.add(victory, "VICTORY");
            

            frame.setContentPane(cardContainer);
            frame.setVisible(true);
            cardLayout.show(cardContainer, "SPLASH");
        }

    }
}