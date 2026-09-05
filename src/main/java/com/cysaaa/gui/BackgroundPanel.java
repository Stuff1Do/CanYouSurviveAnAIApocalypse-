
package com.cysaaa.gui;


import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * InnerBackgroundPanel
 * 
 * Custom background panel class for easy background handling
 */
public class BackgroundPanel extends JPanel{
    private Image background;

    public BackgroundPanel(String location){
        background = new ImageIcon(getClass().getResource(location)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        
    }
    
}
