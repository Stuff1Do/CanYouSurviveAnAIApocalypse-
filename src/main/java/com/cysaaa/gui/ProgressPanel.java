package com.cysaaa.gui;

import java.awt.CardLayout;
import javax.swing.JPanel;

public class ProgressPanel extends BackgroundPanel {

    public ProgressPanel(JPanel cardContainer, CardLayout cardLayout) {
        super("/backgrounds/progress.png");

        PercentLayout layout = new PercentLayout();
        layout.setDesignSize(1920, 1080);
        setLayout(layout);
        setOpaque(false);

        // TODO: add progress bar, lifeline icons, and Continue button here later
    }

    // Called right before this panel is shown, so it reflects the latest
    // question number / progress percentage from StateManager.
    // Leave empty for now - fill in once the progress bar is ready.
    public void refreshProgress() {
        // TODO: recalculate and repaint progress bar based on
        // StateManager.getInstance().getCurrentQuestionNumber()
    }
}