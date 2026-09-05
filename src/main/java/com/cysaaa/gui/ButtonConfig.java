package com.cysaaa.gui;

/**
 * ButtonConfig
 *
 * Button sizing, specific to each button. 
 */
public enum ButtonConfig {

    PLAY    ("/buttons/start_button.png", 300, 93),
    SETTINGS("/buttons/how_to_play.png",  260, 80),
    QUIT    ("/buttons/exit_button.png",  220, 68);

    // scale every button's size up or down, add scales when necessary
    public static final double SCALE = 1.1;

    public final String imagePath;
    public final int width;
    public final int height;

    ButtonConfig(String imagePath, int width, int height) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return (int) (width * SCALE);
    }

    public int getHeight() {
        return (int) (height * SCALE);
    }
}