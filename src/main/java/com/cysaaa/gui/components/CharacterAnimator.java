package com.cysaaa.gui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Subtle character animation drawn on top of the menu background.
 * All coordinates are in background-image pixels (1920 x 1080).
 */
public class CharacterAnimator {

    private static final int PAD = 8;

    // Robot cat eyes - adjust to match your image
    private static final Rectangle EYE_L = new Rectangle(1283, 462, 16, 44);
    private static final Rectangle EYE_R = new Rectangle(1340, 458, 16, 44);

    private final BufferedImage background;
    private final ArrayList<WarpRegion> regions = new ArrayList<>();

    public CharacterAnimator(BufferedImage background) {
        this.background = background;

        // x, y, w, h, feather, sway(px), bob(px), speed, phase
        regions.add(new WarpRegion(1125, 560, 150, 250, 14, 2.5, 1.5, 1.3, 0.0)); // person
        regions.add(new WarpRegion(1050, 675, 100, 140, 10, 1.5, 0.8, 1.8, 1.0)); // small cat body
        regions.add(new WarpRegion(1040, 700,  55,  70,  8, 5.0, 0.0, 3.2, 0.5)); // small cat tail
        regions.add(new WarpRegion(1240, 380, 210, 190, 16, 3.0, 2.5, 0.9, 2.0)); // robot head
        regions.add(new WarpRegion(1260, 540, 300, 270, 16, 1.0, 1.2, 0.9, 2.0)); // robot body
    }

    public int getWidth()  { return background.getWidth(); }
    public int getHeight() { return background.getHeight(); }

    /** g must already be scaled so 1 unit = 1 background-image pixel. */
    public void paint(Graphics2D g, double t) {
        for (WarpRegion r : regions) {
            r.paint(g, t);
        }
        paintEyes(g, t);
    }

    private void paintEyes(Graphics2D g, double t) {

        float pulse = (float) (0.55 + 0.45 * Math.sin(t * 2.2));

        // Blink for ~150 ms every 4.5 s
        double cycle = t % 4.5;
        float open = cycle > 4.35
                ? Math.min(1f, (float) Math.abs((cycle - 4.425) / 0.075))
                : 1f;

        Composite old = g.getComposite();

        for (Rectangle eye : new Rectangle[] { EYE_L, EYE_R }) {

            int h = Math.max(2, Math.round(eye.height * open));
            int y = eye.y + (eye.height - h) / 2;

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.35f * pulse * open));
            g.setColor(new Color(190, 120, 255));
            g.fillRoundRect(eye.x - 8, y - 8, eye.width + 16, h + 16, 20, 20);

            g.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, Math.min(1f, 0.6f + 0.4f * pulse)));
            g.setColor(new Color(245, 230, 255));
            g.fillRoundRect(eye.x, y, eye.width, h, 8, 8);
        }

        g.setComposite(old);
    }

    // ---------------------------------------------------------
    // Feathered cutout, warped row by row, anchored at its bottom
    // ---------------------------------------------------------
    private class WarpRegion {

        final int x, y, w, h;
        final double sway, bob, speed, phase;
        final BufferedImage tile;
        final BufferedImage scratch;
        final BufferedImage[] rows;

        WarpRegion(int x, int y, int w, int h, int feather,
                   double sway, double bob, double speed, double phase) {

            this.x = x; this.y = y; this.w = w; this.h = h;
            this.sway = sway; this.bob = bob;
            this.speed = speed; this.phase = phase;

            tile = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            for (int py = 0; py < h; py++) {
                for (int px = 0; px < w; px++) {

                    int sx = x + px;
                    int sy = y + py;

                    if (sx >= background.getWidth() || sy >= background.getHeight()) {
                        continue;
                    }

                    int edge = Math.min(
                            Math.min(px, w - 1 - px),
                            Math.min(py, h - 1 - py));

                    float a = Math.min(1f, edge / (float) feather);
                    int rgb = background.getRGB(sx, sy) & 0xFFFFFF;

                    tile.setRGB(px, py, ((int) (a * 255) << 24) | rgb);
                }
            }

            rows = new BufferedImage[h];
            for (int r = 0; r < h; r++) {
                rows[r] = tile.getSubimage(0, r, w, 1);
            }

            scratch = new BufferedImage(
                    w + PAD * 2, h + PAD * 2, BufferedImage.TYPE_INT_ARGB);
        }

        void paint(Graphics2D g, double t) {

            double wave  = Math.sin(t * speed + phase);
            double wave2 = Math.sin(t * speed * 0.5 + phase + 1.3);

            Graphics2D sg = scratch.createGraphics();
            sg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            sg.setComposite(AlphaComposite.Clear);
            sg.fillRect(0, 0, scratch.getWidth(), scratch.getHeight());
            sg.setComposite(AlphaComposite.SrcOver);

            for (int row = 0; row < h; row++) {

                // 1 at the top of the region, 0 at the bottom (anchored)
                double weight = 1.0 - row / (double) h;

                double dx = sway * wave * weight * weight;
                int dy = (int) Math.round(-bob * Math.abs(wave2) * weight);

                sg.drawImage(rows[row],
                        AffineTransform.getTranslateInstance(PAD + dx, PAD + row + dy),
                        null);
            }

            sg.dispose();

            g.drawImage(scratch, x - PAD, y - PAD, null);
        }
    }
}