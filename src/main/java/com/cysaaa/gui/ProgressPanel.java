package com.cysaaa.gui;

import com.cysaaa.util.Host;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.cysaaa.util.GameState;

public class ProgressPanel extends BackgroundPanel {

    private PercentLayout percentLayout;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private ImagePanel progressBar;
    private LifelineIconPanel traceEliminationIcon;
    private LifelineIconPanel systemRerouteIcon;
    private LifelineIconPanel specialLifelineIcon;
    private ImagePanel continueButton;
    private ImagePanel withdrawButton;

    private static final String[] PROGRESS_IMAGES = {
        "/images/progress_00.png", "/images/progress_01.png", "/images/progress_02.png",
        "/images/progress_03.png", "/images/progress_04.png", "/images/progress_05.png",
        "/images/progress_06.png", "/images/progress_07.png", "/images/progress_08.png",
        "/images/progress_09.png", "/images/progress_10.png", "/images/progress_11.png",
        "/images/progress_12.png", "/images/progress_13.png", "/images/progress_14.png",
        "/images/progress_15.png"
    };

    public ProgressPanel(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/progress.png");

        percentLayout = new PercentLayout();
        setLayout(percentLayout);

        progressBar = new ImagePanel(PROGRESS_IMAGES[0]);
        percentLayout.addPixel(this, progressBar, 138, 188, 1593, 220);

        traceEliminationIcon = new LifelineIconPanel(
            "/images/TraceEl.png",
            "/images/TraceElDisabled.png"
        );
        percentLayout.addPixel(this, traceEliminationIcon, 709, 681, 502, 158);

        systemRerouteIcon = new LifelineIconPanel(
            "/images/SysRe.png",
            "/images/SysReDisabled.png"
        );
        percentLayout.addPixel(this, systemRerouteIcon, 1255, 683, 502, 158);

        specialLifelineIcon = new LifelineIconPanel(
            "/images/placeholder.png",
            "/images/placeholder.png"
        );
        percentLayout.addPixel(this, specialLifelineIcon, 164, 680, 502, 158);

        
        // Continue button -> back to GAMEPLAY for the next question
        continueButton = new ImagePanel("/images/continue_button.png", "/images/continue_button_hover.png");
        percentLayout.addPixel(this, continueButton, 1022, 925, 366, 93);
        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "GAMEPLAY");
            }
        });

        // Withdraw button -> go to END screen
        withdrawButton = new ImagePanel("/images/withdraw_button.png", "/images/withdraw_button_hover.png");
        percentLayout.addPixel(this, withdrawButton, 526, 922, 366, 93);
        withdrawButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JRootPane root = SwingUtilities.getRootPane(ProgressPanel.this);
                JLayeredPane layeredPane = root.getLayeredPane();

                ConfirmationOverlay overlay = new ConfirmationOverlay(
                    "/popups/withdrawConfirm.png",
                    () -> { // onYes
                        StateManager.getInstance().setScreenState(GameState.WITHDRAW);
                        cardLayout.show(mainPanel, "END");
                    }
                );

                overlay.setBounds(0, 0, getWidth(), getHeight());
                layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
                layeredPane.repaint();
            }
        });
        
       // Auto-sync every time this panel becomes visible — no manual call needed elsewhere
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                syncWithState();
            }
        });
    }

    public void refreshProgress(int questionsAnswered) {
        progressBar.setImage(PROGRESS_IMAGES[questionsAnswered]);
    }

    // Call this every time PROGRESS is about to be shown
    public void syncWithState() {
        StateManager state = StateManager.getInstance();

        int questionsAnswered = state.getCurrentQuestionNumber() - 1;
        refreshProgress(questionsAnswered);

        traceEliminationIcon.setUsed(state.isTraceEliminationUsed());
        systemRerouteIcon.setUsed(state.isSystemRerouteUsed());
        specialLifelineIcon.setUsed(state.isSpecialLifelineUsed());

        Host host = state.getCurrentHost();
    
        if (host != null) {
            specialLifelineIcon.setActiveImage(
                host.getSpecialLifeline().getActiveIconPath(),
                host.getSpecialLifeline().getDisabledIconPath()
            );
        }
    }
}