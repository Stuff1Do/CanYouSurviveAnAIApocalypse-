package com.cysaaa.util;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

/**
 * DebugUtil
 *
 * outline layouts 
 */
public class DebugUtil {
    public static void outline(JComponent component, Color color) {
        component.setBorder(new LineBorder(color, 2));
    }
}