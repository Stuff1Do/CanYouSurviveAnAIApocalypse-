package com.cysaaa.gui.screens;

import com.cysaaa.gui.components.BackgroundPanel;
import com.cysaaa.gui.components.PercentLayout;

public class GameOverPanel extends BackgroundPanel{

     private PercentLayout percentLayout;
    public GameOverPanel(){
        super("/backgrounds/game_over.png");

         percentLayout = new PercentLayout();

         setLayout(percentLayout);


    }
}