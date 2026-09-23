package com.cysaaa.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SplashScreenPanel extends JPanel {
    private static final int SPLASH_DURATION_MS = 14000;
    private static final int FADE_DURATION_MS = 700;
    private static final int FRAME_MS = 40;
    private static final String LOADING_FONT = "Chakra Petch";
    private static final Color LOADING_PINK = new Color(255, 20, 190);

    private final JPanel cardContainer;
    private final java.awt.CardLayout cardLayout;
    private final Timer animationTimer;
    private final BackgroundPanel titleLayer;
    private long startedAt;
    private int animationOffset;
    private float opacity = 1f;
    private boolean fading;
    private String loadingStatus = "LOADING...";

    public SplashScreenPanel(JPanel cardContainer, java.awt.CardLayout cardLayout) {
        this.cardContainer = cardContainer;
        this.cardLayout = cardLayout;
        setPreferredSize(new Dimension(1920, 1080));
        setLayout(new BorderLayout());

        JLayeredPane layers = new JLayeredPane();
        layers.setPreferredSize(new Dimension(1920, 1080));
        layers.setLayout(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/splashscreen/backgroundpanel.png");
        backgroundPanel.setBounds(0, 0, 1920, 1080);
        layers.add(backgroundPanel, Integer.valueOf(0));

        titleLayer = new BackgroundPanel("/splashscreen/splash2.png");
        titleLayer.setOpaque(false);
        titleLayer.setBounds(0, 0, 1920, 1080);
        layers.add(titleLayer, Integer.valueOf(1));

        layers.add(new LoaderOverlay(), Integer.valueOf(2));
        add(layers, BorderLayout.CENTER);

        animationTimer = new Timer(FRAME_MS, this::advanceAnimation);
        startedAt = System.currentTimeMillis();
        animationTimer.start();
    }

    private void advanceAnimation(ActionEvent event) {
        long elapsed = System.currentTimeMillis() - startedAt;
        if (!fading && elapsed >= SPLASH_DURATION_MS - FADE_DURATION_MS) {
            fading = true;
            startedAt = System.currentTimeMillis();
            elapsed = 0;
        }

        if (fading) {
            opacity = Math.max(0f, 1f - (float) elapsed / FADE_DURATION_MS);
            if (opacity == 0f) {
                animationTimer.stop();
                cardLayout.show(cardContainer, "MENU");
                return;
            }
        }

        animationOffset = (animationOffset + 12) % 96;
        titleLayer.setVisible((System.currentTimeMillis() / 450) % 2 == 0);
        updateLoadingStatus();
        repaint();
    }

    private void updateLoadingStatus() {
        long elapsed = System.currentTimeMillis() - startedAt;
        float progress = fading ? 1f : Math.min(1f, (float) elapsed / (SPLASH_DURATION_MS - FADE_DURATION_MS));
        if (progress >= 0.90f) {
            loadingStatus = "READY";
        } else if (progress >= 0.75f) {
            loadingStatus = "FINALIZING...";
        } else if (progress >= 0.45f) {
            loadingStatus = "INITIALIZING...";
        } else {
            loadingStatus = "LOADING...";
        }
    }

    private class LoaderOverlay extends JPanel {
        private LoaderOverlay() {
            setBounds(0, 0, 1920, 1080);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0, 0, 0, Math.round((1f - opacity) * 255)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g.setPaint(new GradientPaint(0, getHeight() - 260, new Color(8, 0, 18, 0),
                    0, getHeight(), new Color(8, 0, 18, 190)));
            g.fillRect(0, getHeight() - 260, getWidth(), 260);

            long elapsed = System.currentTimeMillis() - startedAt;
            float progress = fading ? 1f : Math.min(1f, (float) elapsed / (SPLASH_DURATION_MS - FADE_DURATION_MS));
            paintLoader(g, progress);
            g.dispose();
        }
    }

    private void paintLoader(Graphics2D g, float progress) {
        int loaderWidth = Math.min(620, getWidth() - 120);
        int loaderX = (getWidth() - loaderWidth) / 2;
        int loaderY = getHeight() / 2 + 300;
        int loaderHeight = 12;

        g.setColor(new Color(13, 3, 20, 180));
        g.fillRoundRect(loaderX, loaderY, loaderWidth, loaderHeight, 6, 6);

        int filledWidth = Math.round(loaderWidth * progress);
        if (filledWidth > 0) {
            g.setPaint(new GradientPaint(loaderX, loaderY, new Color(255, 120, 225),
                    loaderX + loaderWidth, loaderY, new Color(255, 20, 165)));
            g.fillRoundRect(loaderX, loaderY, filledWidth, loaderHeight, 8, 8);
        }

        if (filledWidth > 8) {
            int shineX = loaderX + Math.min(filledWidth - 8, Math.max(0, animationOffset));
            g.setColor(new Color(255, 235, 252, 210));
            g.fillRoundRect(shineX, loaderY + 2, 6, loaderHeight - 4, 4, 4);
        }

        paintLoadingText(g, loaderX + loaderWidth / 2, loaderY - 18);

        g.setColor(LOADING_PINK);
        g.setFont(new Font(LOADING_FONT, Font.BOLD, 16));
        String percentage = Math.round(progress * 100) + "%";
        int textX = loaderX + (loaderWidth - g.getFontMetrics().stringWidth(percentage)) / 2;
        g.drawString(percentage, textX, loaderY + loaderHeight + 32);
    }

    private void paintLoadingText(Graphics2D g, int centerX, int baseline) {
        String loadingText = loadingStatus;
        g.setFont(new Font(LOADING_FONT, Font.BOLD, 24));
        int textX = centerX - g.getFontMetrics().stringWidth(loadingText) / 2;

        g.setColor(LOADING_PINK);
        g.drawString(loadingText, textX, baseline);
    }
}