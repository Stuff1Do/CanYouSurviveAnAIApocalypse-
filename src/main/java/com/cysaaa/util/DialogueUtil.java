package com.cysaaa.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import javax.swing.JLabel;
import javax.swing.Timer;


public class DialogueUtil {

    private final JLabel targetLabel;
    private final int millisPerChar;
    private final int wrapWidth;

    private Timer typewriterTimer;
    private String currentFullText = "";

    public DialogueUtil(JLabel targetLabel, int millisPerChar, int wrapWidth, String fontPath, float fontSize) {
        this.targetLabel = targetLabel;
        this.millisPerChar = millisPerChar;
        this.wrapWidth = wrapWidth;

        Font font = loadFont(fontPath, fontSize);
        targetLabel.setFont(font);
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

    public void skipToEnd() {
        if (typewriterTimer != null && typewriterTimer.isRunning()) {
            typewriterTimer.stop();
            targetLabel.setText(wrapInHtmlTag(applyWordWrap(currentFullText)));
        }
    }

    // Inserts <br> every N words into the raw text
    private String applyWordWrap(String text) {
        int wordsPerLine = 7;
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

    // Just wraps already-formatted text in the html/div container 
    private String wrapInHtmlTag(String alreadyWrappedText) {
        return "<html><div style='width: " + wrapWidth + "px; padding: 10px; text-align: left; "
            + "word-wrap: break-word; overflow-wrap: break-word;'>"
            + alreadyWrappedText
            + "</div></html>";
    }
}