package com.cysaaa.gui;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class EndingPanel extends BackgroundPanel {
    private final JPanel cardContainer;
    private final CardLayout cardLayout;
    private final String endingAsset;
    public EndingPanel(JPanel cardContainer, CardLayout cardLayout, String endingAsset) {
        super(endingAsset);
        this.cardContainer = cardContainer;
        this.cardLayout = cardLayout;
        this.endingAsset = endingAsset;
        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);

        if (endingAsset.contains("50") || endingAsset.contains("75")) {
            // 50% / 75% checkpoints: single centered EXIT button -> back to main menu
            addImageButton(layout, "/endings/exit1.png",
                776, 748, 451, 140, "MENU");
        } else if (endingAsset.contains("100")) {
            // 100% (victory) ending: Main Menu (left) + Play Again (right)
            addImageButton(layout, "/endings/mainmenu1.png",
                382, 732, 369, 97, "MENU");
            addImageButton(layout, "/endings/playagain2.png",
                792, 732, 369, 97, "HOST_SELECTION");
        } else {
            // Game Over panel: Main Menu (left) + Play Again (right)
            addImageButton(layout, "/endings/mainmenu1.png",
                567, 772, 369, 98, "MENU");
            addImageButton(layout, "/endings/playagain3.png",
                978, 772, 369, 98, "HOST_SELECTION");
        }
    }

    private void addImageButton(PercentLayout layout, String imagePath,
                                int x, int y, int width, int height,
                                String targetCard) {
        ImagePanel button = new ImagePanel(imagePath);
        button.setPixelPreciseHitTest(true);
        button.setHoverHighlight(true);
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                StateManager.getInstance().reset();
                cardLayout.show(cardContainer, targetCard);
            }
        });
        layout.addPixel(this, button, x, y, width, height);
    }

    public static String cardForCheckpoint(int checkpoint) {
        if (checkpoint >= 100) return "ENDING_100";
        if (checkpoint >= 75) return "ENDING_75";
        if (checkpoint >= 50) return "ENDING_50";
        return "ENDING_GAME_OVER";
    }
}