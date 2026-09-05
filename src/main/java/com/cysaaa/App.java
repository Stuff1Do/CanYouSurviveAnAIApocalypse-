package com.cysaaa;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cysaaa.gui.BackgroundPanel;
import com.cysaaa.gui.ImageButton;
import com.cysaaa.util.DebugUtil;
import com.cysaaa.gui.ButtonConfig;

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
        

        //set up frame
        JFrame frame = new JFrame("Can You Survive an AI Apocalypse?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(screenSize.width, screenSize.height);
        frame.setResizable(false);

        //set up background
        BackgroundPanel bgPanel = new BackgroundPanel("/backgrounds/splash_screen.png");
        bgPanel.setBounds(0, 0, screenSize.width, screenSize.height);
        bgPanel.setLayout(new GridBagLayout());
        bgPanel.setOpaque(false);

        //using gbc, we change layout os its 40% to the left and 60% to the right
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.45; // 45%
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.55; // 55%
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;

    
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setOpaque(false);
        leftColumn.setBorder(BorderFactory.createEmptyBorder(40, 40, 0, 0));
        leftColumn.setPreferredSize(new Dimension(0, 0));

        Image rawTitleImage = new ImageIcon(App.class.getResource("/labels/title_text.png")).getImage();
        Image scaledTitleImage = rawTitleImage.getScaledInstance(765, -1, Image.SCALE_SMOOTH); // -1 keeps aspect ratio
        JLabel titleLabel = new JLabel(new ImageIcon(scaledTitleImage));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        leftColumn.add(titleLabel);
        leftColumn.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        //add in buttons
        for (ButtonConfig cfg : ButtonConfig.values()) {
            ImageButton btn = new ImageButton(cfg.imagePath, cfg.getWidth(), cfg.getHeight());
            btn.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT); 
            buttonPanel.add(btn);
            buttonPanel.add(Box.createVerticalStrut(15));
        }

        buttonPanel.setMaximumSize(buttonPanel.getPreferredSize());
        buttonPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        leftColumn.add(buttonPanel);


        //placeholder for right visual, to be added later
        JPanel rightCell = new JPanel();
        rightCell.setOpaque(false);

        //add both columns to bgPanel
        bgPanel.add(leftColumn, leftGbc);
        bgPanel.add(rightCell, rightGbc);
        //debug outlines for layouting, dont mind
        
        /* 
            DebugUtil.outline(leftColumn, Color.RED);
        DebugUtil.outline(buttonPanel, Color.YELLOW);
        DebugUtil.outline(rightCell, Color.BLUE);
        */
        
            
        //show
        frame.setContentPane(bgPanel);
        frame.setVisible(true);
    }
}
