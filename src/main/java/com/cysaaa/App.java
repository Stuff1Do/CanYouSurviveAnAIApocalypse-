package com.cysaaa;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.cysaaa.gui.MainMenuPanel;
import com.cysaaa.terminal.TApp;
import com.cysaaa.gui.HostSelectionPanel;
import com.cysaaa.gui.InstructionsPanel;

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

            MainMenuPanel mainMenuPanel = new MainMenuPanel(cardContainer, cardLayout);
            mainMenuPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            InstructionsPanel instructionsPanel = new InstructionsPanel(cardContainer, cardLayout);
            instructionsPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            HostSelectionPanel hostSelectionPanel = new HostSelectionPanel(cardContainer, cardLayout);
            hostSelectionPanel.setBounds(0, 0, screenSize.width, screenSize.height);

            cardContainer.add(mainMenuPanel, "MENU");
            cardContainer.add(instructionsPanel, "INSTRUCTIONS");
            cardContainer.add(hostSelectionPanel, "HOST_SELECTION");

            frame.setContentPane(cardContainer);
            frame.setVisible(true);
        }
       
    }
}