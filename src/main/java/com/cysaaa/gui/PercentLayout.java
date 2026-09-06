package com.cysaaa.gui;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PercentLayout implements LayoutManager2 {

    // Reference design size (e.g. the resolution your mockup was designed at)
    private int designWidth = 1920;
    private int designHeight = 1080;

    // Stores fractional bounds: x%, y%, width%, height% (all 0.0 to 1.0)
    private final Map<Component, Rectangle2DPercent> constraints = new HashMap<>();

    public static class Rectangle2DPercent {
        double x, y, w, h;
        public Rectangle2DPercent(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    // Set the design reference size (call this once before adding components)
    public void setDesignSize(int width, int height) {
        this.designWidth = width;
        this.designHeight = height;
    }

    // Add a component using percentage constraints directly (0.0 - 1.0)
    @Override
    public void addLayoutComponent(Component comp, Object constraint) {
        if (constraint instanceof Rectangle2DPercent) {
            constraints.put(comp, (Rectangle2DPercent) constraint);
        }
    }

    // Helper: add a component using pixel coordinates from your design reference
    public void addPixel(Container parent, Component comp, int x, int y, int w, int h) {
        Rectangle2DPercent r = new Rectangle2DPercent(
            x / (double) designWidth,
            y / (double) designHeight,
            w / (double) designWidth,
            h / (double) designHeight
        );
        constraints.put(comp, r);
        parent.add(comp);
    }

    @Override
    public void layoutContainer(Container parent) {
        int w = parent.getWidth();
        int h = parent.getHeight();
        for (Map.Entry<Component, Rectangle2DPercent> entry : constraints.entrySet()) {
            Component comp = entry.getKey();
            Rectangle2DPercent r = entry.getValue();
            int x = (int) (r.x * w);
            int y = (int) (r.y * h);
            int cw = (int) (r.w * w);
            int ch = (int) (r.h * h);
            comp.setBounds(x, y, cw, ch);
        }
    }

    // --- boilerplate methods required by LayoutManager2 ---
    @Override public void addLayoutComponent(String name, Component comp) {}
    @Override public void removeLayoutComponent(Component comp) { constraints.remove(comp); }
    @Override public Dimension preferredLayoutSize(Container parent) { return parent.getSize(); }
    @Override public Dimension minimumLayoutSize(Container parent) { return new Dimension(0, 0); }
    @Override public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
    @Override public float getLayoutAlignmentX(Container target) { return 0.5f; }
    @Override public float getLayoutAlignmentY(Container target) { return 0.5f; }
    @Override public void invalidateLayout(Container target) {}
}