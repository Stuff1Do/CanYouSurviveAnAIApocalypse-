package com.cysaaa.util;

import java.awt.Font;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public final class LabelFontFitter {

    private LabelFontFitter() {}

    // Steps the label's font down from maxSize to minSize until its HTML text fits the label's height.
    // Returns the text's height at the chosen size, or -1 if the label isn't laid out yet / isn't HTML.
    public static int fitToHeight(JLabel label, int maxSize, int minSize) {
        Insets insets = label.getInsets();
        int width = label.getWidth() - insets.left - insets.right;
        int height = label.getHeight() - insets.top - insets.bottom;
        if (width <= 0 || height <= 0) return -1;

        Font base = label.getFont();
        int textHeight = -1;
        for (int size = maxSize; size >= minSize; size--) {
            label.setFont(base.deriveFont((float) size));
            // JLabel caches the parsed HTML as a View; ask it how tall it is when wrapped to our width
            View view = (View) label.getClientProperty(BasicHTML.propertyKey);
            if (view == null) return -1;
            view.setSize(width, 0);
            textHeight = (int) Math.ceil(view.getPreferredSpan(View.Y_AXIS));
            if (textHeight <= height) break;
        }
        return textHeight;
    }
}
