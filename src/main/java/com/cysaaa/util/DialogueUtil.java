package com.cysaaa.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import javax.swing.JLabel;
import javax.swing.Timer;


public class DialogueUtil {

    private final JLabel targetLabel;
    private final int millisPerChar;
    private final int wrapWidth;

    private Timer typewriterTimer;
    private String currentFullText = "";

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

        targetLabel.setText(wrapInHtmlTag(applyWordWrap(currentFullText)));
        LabelFontFitter.fitToHeight(targetLabel, maxFontSize, minFontSize);

        boolean typing = typewriterTimer != null && typewriterTimer.isRunning();
        if (typing) {
            targetLabel.setText(shownText); // next tick re-renders with the new width anyway
        } else if (!currentFullText.isEmpty()) {
            // already fully typed — re-render so the div width matches the resized label
            targetLabel.setText(wrapInHtmlTag(applyWordWrap(currentFullText)));
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
        fitToLabel();
        targetLabel.setText(""); // fitToLabel leaves the full line shown; blank it so it doesn't flash before typing
        String wrappedFullText = applyWordWrap(fullText); // compute line breaks only on completed text
        int[] charIndex = {0};

        typewriterTimer = new Timer(millisPerChar, e -> {
            charIndex[0]++;
            // reveal characters from the WRAPPED text, not the raw text,
            // so <br> tags are already in fixed positions and won't shift
            String visiblePortion = wrappedFullText.substring(0, Math.min(charIndex[0], wrappedFullText.length()));
            targetLabel.setText(wrapInHtmlTag(visiblePortion));

            if (charIndex[0] >= wrappedFullText.length()) {
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
        targetLabel.setText("");
    }

    public void skipToEnd() {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
            targetLabel.setText(wrapInHtmlTag(applyWordWrap(currentFullText)));
        }
    }

    // Inserts <br> every N words into the raw text
    private String applyWordWrap(String text) {
        int wordsPerLine = 4;
        String[] words = text.split(" ");
        StringBuilder wrapped = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            wrapped.append(words[i]);
            if ((i + 1) % wordsPerLine == 0 && i != words.length - 1) {
                wrapped.append("<br>");
            } else if (i != words.length - 1) {
                wrapped.append(" ");
            }
        }
        return wrapped.toString();
    }

    // Swing's HTML renderer (non-W3C mode, which JLabel always uses) draws CSS "px" 1.3x larger
    // than real screen pixels, so divide by this to get a div that's actually the label's width.
    private static final float SWING_CSS_PX_SCALE = 1.3f;

    // Div width follows the label's real (window-scaled) width; a fixed px width overflows
    // and gets clipped when the window is smaller than the design size.
    // Falls back to wrapWidth before the label is laid out.
    private int currentWrapWidth() {
        java.awt.Insets insets = targetLabel.getInsets();
        int available = targetLabel.getWidth() - insets.left - insets.right;
        int screenPx = available > 0 ? available : wrapWidth;
        return (int) (screenPx / SWING_CSS_PX_SCALE);
    }

    // Just wraps already-formatted text in the html/div container
    private String wrapInHtmlTag(String alreadyWrappedText) {
        return "<html><div style='width: " + currentWrapWidth() + "px; text-align: center; "
            + "word-wrap: break-word; overflow-wrap: break-word;'>"
            + alreadyWrappedText
            + "</div></html>";
    }
}