package com.cysaaa.gui;

public class LifelineIconPanel extends ImagePanel {
    private String activeImagePath;
    private String disabledImagePath;
    private boolean used = false;

    public LifelineIconPanel(String activeImagePath, String disabledImagePath) {
        super(activeImagePath); // adjust if ImagePanel's constructor differs
        this.activeImagePath = activeImagePath;
        this.disabledImagePath = disabledImagePath;
    }

    // For the special lifeline icon: called when host changes
    public void setActiveImage(String newActivePath, String newDisabledPath) {
        this.activeImagePath = newActivePath;
        this.disabledImagePath = newDisabledPath;
        setImage(used ? disabledImagePath : activeImagePath); // adjust if ImagePanel's swap method has a different name
        repaint();
    }

    public void setUsed(boolean used) {
        this.used = used;
        setImage(used ? disabledImagePath : activeImagePath);
        repaint();
    }

    public boolean isUsed() { return used; }
}