package com.cysaaa.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.Timer;


public class DialogueUtil {

    private final JLabel targetLabel;
    private final int millisPerChar;
    private final int wrapWidth;

    private Timer typewriterTimer;
    private String currentFullText = "";
    private List<String> currentLines = new ArrayList<>(); // currentFullText split into width-fitting lines

    // auto-fit font (off unless enableAutoFit is called)
    private boolean autoFit = false;
    private int maxFontSize;
    private int minFontSize;

    public DialogueUtil(JLabel targetLabel, int millisPerChar, int wrapWidth, String fontPath, float fontSize) {
        this.targetLabel = targetLabel;
        this.millisPerChar = millisPerChar;
        this.wrapWidth = wrapWidth;

        Font font = loadFont(fontPath, fontSize);
        targetLabel.setFont(font);
    }


    // Sizes the font to the largest that fits the label.
    // Refits whenever the label is resized (PercentLayout scales it with the window).
    public void enableAutoFit(int maxFontSize, int minFontSize) {
        this.autoFit = true;
        this.maxFontSize = maxFontSize;
        this.minFontSize = minFontSize;
        targetLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fitToLabel();
            }
        });
    }

    // Measures the FULL dialogue (not the partially typed text), so the font size and
    // size is fixed before typing starts and the text doesn't shrink mid-type.
    private void fitToLabel() {
        if (!autoFit) return;
        String shownText = targetLabel.getText();

        // measure with the raw text — the HTML wraps it to the label width on its own
        targetLabel.setText(wrapInHtmlTag(currentFullText));
        LabelFontFitter.fitToHeight(targetLabel, maxFontSize, minFontSize);
        currentLines = wrapToLines(currentFullText); // line breaks for the chosen font + width

        boolean typing = typewriterTimer != null && typewriterTimer.isRunning();
        if (typing) {
            targetLabel.setText(shownText); // next tick re-renders with the new lines anyway
        } else if (!currentFullText.isEmpty()) {
            // already fully typed — re-render so the lines match the resized label
            targetLabel.setText(wrapInHtmlTag(String.join("<br>", currentLines)));
        }
    }

    //loads fonts
    private Font loadFont(String path, float size) {
        try (InputStream is = DialogueUtil.class.getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("Font not found at: " + path + " — using fallback.");
                return new Font("SansSerif", Font.BOLD, (int) size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }

    //typewriter effect
    public void type(String fullText) {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }

        currentFullText = fullText;
        currentLines = wrapToLines(fullText);
        fitToLabel(); // picks the font size, then recomputes currentLines for it
        targetLabel.setText(""); // fitToLabel leaves the full line shown; blank it so it doesn't flash before typing
        int[] charIndex = {0};

        typewriterTimer = new Timer(millisPerChar, e -> {
            charIndex[0]++;
            // line breaks are decided up front, so a word never jumps to the next line mid-type
            targetLabel.setText(wrapInHtmlTag(visibleLines(charIndex[0])));

            if (charIndex[0] >= totalChars()) {
                typewriterTimer.stop();
            }
        });
        typewriterTimer.start();
    }

    // Stops typing and empties the label (e.g. when the screen is hidden, so old lines don't linger)
    public void clear() {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
        }
        currentFullText = "";
        currentLines = new ArrayList<>();
        targetLabel.setText("");
    }

    public void skipToEnd() {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
            targetLabel.setText(wrapInHtmlTag(String.join("<br>", currentLines)));
        }
    }

    // Splits text into lines that fit the label's width at its current font:
    // keeps adding words to a line until the next word would overflow, then starts a new line.
    private List<String> wrapToLines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;

        FontMetrics fm = targetLabel.getFontMetrics(targetLabel.getFont());
        // small safety margin so the HTML renderer never re-wraps a line we measured as fitting
        int maxWidth = (int) (labelInnerWidth() * 0.95);

        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (word.isEmpty()) continue;
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (line.length() > 0 && fm.stringWidth(candidate) > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    // First `count` characters of currentLines, joined with <br> (never cuts an HTML tag in half)
    private String visibleLines(int count) {
        StringBuilder sb = new StringBuilder();
        int remaining = count;
        for (int i = 0; i < currentLines.size() && remaining > 0; i++) {
            String line = currentLines.get(i);
            if (i > 0) sb.append("<br>");
            sb.append(line, 0, Math.min(remaining, line.length()));
            remaining -= line.length();
        }
        return sb.toString();
    }

    private int totalChars() {
        int total = 0;
        for (String line : currentLines) total += line.length();
        return total;
    }

    // Label width in real screen pixels (falls back to wrapWidth before it's laid out)
    private int labelInnerWidth() {
        java.awt.Insets insets = targetLabel.getInsets();
        int available = targetLabel.getWidth() - insets.left - insets.right;
        return available > 0 ? available : wrapWidth;
    }

    // Swing's HTML renderer (non-W3C mode, which JLabel always uses) draws CSS "px" 1.3x larger
    // than real screen pixels, so divide by this to get a div that's actually the label's width.
    private static final float SWING_CSS_PX_SCALE = 1.3f;

    // Div width follows the label's real (window-scaled) width; a fixed px width overflows
    // and gets clipped when the window is smaller than the design size.
    // Falls back to wrapWidth before the label is laid out.
    private int currentWrapWidth() {
        return (int) (labelInnerWidth() / SWING_CSS_PX_SCALE);
    }

    // Just wraps already-formatted text in the html/div container
    private String wrapInHtmlTag(String alreadyWrappedText) {
        return "<html><div style='width: " + currentWrapWidth() + "px; text-align: center; "
            + "word-wrap: break-word; overflow-wrap: break-word;'>"
            + alreadyWrappedText
            + "</div></html>";
    }
}