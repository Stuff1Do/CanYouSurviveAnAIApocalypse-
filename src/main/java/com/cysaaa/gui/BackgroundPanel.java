
package main.java.com.cysaaa.gui;

import javax.swing.ImageIcon;

/**
 * InnerBackgroundPanel
 * 
 * Custom background panel class for easy background handling
 */
public class BackgroundPanel {
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
