package com.cysaaa.gui.components;

public class LifelineIconPanel extends ImagePanel {
    private String activeImagePath;
    private String disabledImagePath;
    private String hoverImagePath;
    private boolean used = false;

    // Backward-compatible: no hover art
    public LifelineIconPanel(String activeImagePath, String disabledImagePath) {
        this(activeImagePath, disabledImagePath, null);
    }

    public LifelineIconPanel(String activeImagePath, String disabledImagePath, String hoverImagePath) {
        super(activeImagePath);
        this.activeImagePath = activeImagePath;
        this.disabledImagePath = disabledImagePath;
        this.hoverImagePath = hoverImagePath;
        setHoverImage(hoverImagePath);
    }

    // For the special lifeline icon: called when host changes
    public void setActiveImage(String newActivePath, String newDisabledPath) {
        setActiveImage(newActivePath, newDisabledPath, null);
    }

    public void setActiveImage(String newActivePath, String newDisabledPath, String newHoverPath) {
        this.activeImagePath = newActivePath;
        this.disabledImagePath = newDisabledPath;
        this.hoverImagePath = newHoverPath;
        setImage(used ? disabledImagePath : activeImagePath);
        setHoverImage(used ? null : hoverImagePath);
        repaint();
    }

    public void setUsed(boolean used) {
        this.used = used;
        setImage(used ? disabledImagePath : activeImagePath);
        setHoverImage(used ? null : hoverImagePath); // no hover highlight once disabled
        repaint();
    }

    public boolean isUsed() { return used; }
}