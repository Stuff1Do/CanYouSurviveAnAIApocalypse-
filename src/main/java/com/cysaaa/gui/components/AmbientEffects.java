package com.cysaaa.gui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Ambient menu effects: flickering neon signs, animated teal contour shape,
 * and chimney smoke. Coordinates are background-image pixels (1920x1080).
 */
public class AmbientEffects {

    // true = paint detected pixels solid green so you can see what each
    // effect is picking up.
    private static final boolean DEBUG_MASKS = false;

    private static final Rectangle PINK_SIGN = new Rectangle(1055, 0, 170, 450);
    private static final Rectangle RED_SIGN  = new Rectangle(1700, 180, 190, 480);
    private static final Rectangle TEAL_AREA = new Rectangle(0, 560, 420, 520);
    private static final Point SMOKE_ORIGIN  = new Point(858, 728);

    private interface Score { float of(int r, int g, int b); }

    private final Random random = new Random();
    private final ArrayList<NeonSign> signs = new ArrayList<>();
    private final TealEffect teal;
    private final ArrayList<Puff> puffs = new ArrayList<>();
    private final BufferedImage puffSprite = makePuff(128);

    private double lastT = -1;
    private double spawnTimer;

    public AmbientEffects(BufferedImage bg) {
        signs.add(new NeonSign(bg, PINK_SIGN, AmbientEffects::pinkScore,
                new Color(35, 6, 50), new Color(255, 120, 255),
                1.5, 3.5, 0.0, "pink sign", 0.35f));

        signs.add(new NeonSign(bg, RED_SIGN, AmbientEffects::redScore,
                new Color(40, 5, 8), new Color(255, 90, 70),
                2.5, 5.0, 1.7, "red sign", 1.0f));
                teal = new TealEffect(bg, TEAL_AREA);

        // Pre-run the smoke so the plume already exists on the first frame.
        for (int i = 0; i < 200; i++) {
            stepSmoke(0.05);
        }
    }

    /** g must be scaled so 1 unit = 1 background-image pixel. */
    public void paint(Graphics2D g, double t) {

        double dt = lastT < 0 ? 0 : Math.min(0.1, t - lastT);
        lastT = t;

        teal.paint(g, t);

        for (NeonSign sign : signs) {
            sign.paint(g, t);
        }

        stepSmoke(dt);
        paintSmoke(g);
    }

    // =========================================================
    // COLOR DETECTION (hue based, more tolerant than RGB maths)
    // =========================================================

    private static float tealScore(int r, int g, int b) {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float hue = clamp(1f - Math.abs(hsb[0] - 0.50f) / 0.10f);
        float sat = clamp((hsb[1] - 0.15f) / 0.25f);
        float bri = clamp((hsb[2] - 0.20f) / 0.30f);
        return hue * sat * bri;
    }

    private static float pinkScore(int r, int g, int b) {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float hue = clamp(1f - Math.abs(hsb[0] - 0.88f) / 0.08f);
        float sat = clamp((hsb[1] - 0.30f) / 0.30f);
        float bri = clamp((hsb[2] - 0.55f) / 0.25f);
        return hue * sat * bri;
    }

    private static float redScore(int r, int g, int b) {
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float d = Math.min(hsb[0], 1f - hsb[0]);            // distance to red
        float hue = clamp(1f - d / 0.06f);
        float sat = clamp((hsb[1] - 0.40f) / 0.30f);
        float bri = clamp((hsb[2] - 0.40f) / 0.30f);
        return hue * sat * bri;
    }

    // =========================================================
    // NEON SIGN FLICKER (tubes go dark, glow cuts out, then surges back)
    // =========================================================

    private class NeonSign {

        final Rectangle bounds;
        final BufferedImage darkMask;
        final BufferedImage glowMask;
        final double minGap, maxGap, phase;
        final float glowScale;
        boolean bursting;
        double nextBurstAt, burstEndsAt, nextToggleAt;
        float level = 1f;

        NeonSign(BufferedImage bg, Rectangle bounds, Score score,
                 Color dark, Color glow,
                 double minGap, double maxGap, double phase, String name, float glowScale) {

            this.bounds = bounds;
            this.minGap = minGap;
            this.maxGap = maxGap;
            this.phase = phase;
            this.glowScale = glowScale;

            if (DEBUG_MASKS) {
                dark = Color.GREEN;
                glow = Color.GREEN;
            }

            darkMask = buildMask(bg, bounds, score, dark, name);

            BufferedImage tubes = buildMask(bg, bounds, score, glow, null);
            glowMask = glowOf(tubes, 21, 4.0f);

            nextBurstAt = 0.8 + phase + random.nextDouble() * 1.5;
        }

        private void update(double t) {

            if (!bursting) {
                level = 1f;

                if (t >= nextBurstAt) {
                    bursting = true;
                    burstEndsAt = t + 0.30 + random.nextDouble() * 0.45;
                    nextToggleAt = t;
                }
                return;
            }

            if (t >= burstEndsAt) {
                bursting = false;
                level = 1f;
                nextBurstAt = t + minGap + random.nextDouble() * (maxGap - minGap);
                return;
            }

            if (t >= nextToggleAt) {
                level = random.nextFloat() < 0.5f
                        ? random.nextFloat() * 0.1f           // dropout
                        : 0.6f + random.nextFloat() * 0.4f;   // surge
                nextToggleAt = t + 0.03 + random.nextDouble() * 0.06;
            }
        }

        void paint(Graphics2D g, double t) {

            update(t);

            Composite old = g.getComposite();

            if (DEBUG_MASKS) {
                g.drawImage(darkMask, bounds.x, bounds.y, null);
                g.setComposite(old);
                return;
            }

            // Tubes go dark during dropouts
            float dark = 1f - level;
            if (dark > 0.02f) {
                g.setComposite(alpha(Math.min(1f, dark * 0.95f)));
                g.drawImage(darkMask, bounds.x, bounds.y, null);
            }

            // Breathing glow that cuts out with the flicker
            float glow = level * glowScale * (float) (0.45 + 0.25 * Math.sin(t * 3.0 + phase));
            if (glow > 0.02f) {
                g.setComposite(alpha(Math.min(1f, glow)));
                g.drawImage(glowMask, bounds.x, bounds.y, null);
            }

            g.setComposite(old);
        }
    }

    // =========================================================
    // TEAL CONTOUR SHAPE: glow pulse + traveling ripple + bright sweep
    // =========================================================

    private static class TealEffect {

        private static final double SWEEP_PERIOD = 4.0;

        final Rectangle area;
        final BufferedImage mask;
        final BufferedImage glow;
        final BufferedImage[] rows;
        final BufferedImage rippleScratch;
        final BufferedImage sweepScratch;

        TealEffect(BufferedImage bg, Rectangle area) {

            this.area = area;

            Color tint = DEBUG_MASKS ? Color.GREEN : new Color(140, 255, 240);

            mask = buildMask(bg, area, AmbientEffects::tealScore, tint, "teal shape");
            glow = glowOf(mask, 25, 4.0f);

            rows = new BufferedImage[area.height];
            for (int r = 0; r < area.height; r++) {
                rows[r] = mask.getSubimage(0, r, area.width, 1);
            }

            rippleScratch = new BufferedImage(
                    area.width, area.height, BufferedImage.TYPE_INT_ARGB);
            sweepScratch = new BufferedImage(
                    area.width, area.height, BufferedImage.TYPE_INT_ARGB);
        }

        void paint(Graphics2D g, double t) {

            Composite old = g.getComposite();

            if (DEBUG_MASKS) {
                g.drawImage(mask, area.x, area.y, null);
                g.setComposite(old);
                return;
            }

            // 1) Pulsing glow around every teal line
            float pulse = (float) (0.50 + 0.30 * Math.sin(t * 2.0));
            g.setComposite(alpha(pulse));
            g.drawImage(glow, area.x, area.y, null);

            // 2) Ripple: lines wave sideways in a traveling wave
            Graphics2D rg = rippleScratch.createGraphics();
            rg.setComposite(AlphaComposite.Clear);
            rg.fillRect(0, 0, area.width, area.height);
            rg.setComposite(AlphaComposite.SrcOver);

            for (int row = 0; row < area.height; row++) {
                int dx = (int) Math.round(4.0 * Math.sin(row * 0.045 - t * 3.2));
                rg.drawImage(rows[row], dx, row, null);
            }
            rg.dispose();

            g.setComposite(alpha(0.75f));
            g.drawImage(rippleScratch, area.x, area.y, null);

            // 3) Bright band sweeping diagonally across the lines
            double p = (t % SWEEP_PERIOD) / SWEEP_PERIOD;
            double diag = area.width + area.height;
            double center = -250 + p * (diag + 500);
            double half = 180;

            float a = (float) ((center - half) / 2.0);
            float b = (float) ((center + half) / 2.0);

            Graphics2D sg = sweepScratch.createGraphics();
            sg.setComposite(AlphaComposite.Clear);
            sg.fillRect(0, 0, area.width, area.height);

            sg.setComposite(AlphaComposite.SrcOver);
            sg.drawImage(mask, 0, 0, null);

            sg.setComposite(AlphaComposite.SrcIn);
            sg.setPaint(new LinearGradientPaint(
                    a, a, b, b,
                    new float[] { 0f, 0.5f, 1f },
                    new Color[] {
                        new Color(140, 255, 240, 0),
                        new Color(255, 255, 255, 255),
                        new Color(140, 255, 240, 0)
                    }));
            sg.fillRect(0, 0, area.width, area.height);
            sg.dispose();

            g.setComposite(AlphaComposite.SrcOver);
            g.drawImage(sweepScratch, area.x, area.y, null);

            g.setComposite(old);
        }
    }

    // =========================================================
    // CHIMNEY SMOKE
    // =========================================================

    private static class Puff {
        double x, y, vx, vy, age, life, seed;
    }

    private void stepSmoke(double dt) {

        spawnTimer += dt;

        while (spawnTimer >= 0.5) {
            spawnTimer -= 0.10;

            Puff p = new Puff();
            p.x = SMOKE_ORIGIN.x + (random.nextDouble() - 0.5) * 10;
            p.y = SMOKE_ORIGIN.y;
            p.vx = 8 + random.nextDouble() * 12;
            p.vy = -(30 + random.nextDouble() * 18);
            p.life = 5.0 + random.nextDouble() * 2.5;
            p.seed = random.nextDouble() * 10;
            puffs.add(p);
        }

        Iterator<Puff> it = puffs.iterator();

        while (it.hasNext()) {
            Puff p = it.next();
            p.age += dt;

            if (p.age >= p.life) {
                it.remove();
                continue;
            }

            p.x += (p.vx + Math.sin(p.age * 1.4 + p.seed) * 16) * dt;
            p.y += p.vy * dt;
        }
    }

    private void paintSmoke(Graphics2D g) {

        Composite old = g.getComposite();

        for (Puff p : puffs) {

            double r = p.age / p.life;

            float a = r < 0.12
                    ? (float) (r / 0.12)
                    : (float) ((1.0 - r) /  0.88);

            a *= 0.5f;

            int radius = (int) (16 + 70 * r);

            g.setComposite(alpha(a));

            g.drawImage(puffSprite,
                    (int) p.x - radius, (int) p.y - radius,
                    radius * 2, radius * 2, null);
        }

        g.setComposite(old);
    }

    private static BufferedImage makePuff(int size) {

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setPaint(new RadialGradientPaint(
            size / 2f, size / 2f, size / 2f,
            new float[] { 0f, 0.55f, 1f },
            new Color[] {
                new Color(90, 85, 100, 200),
                new Color(90, 85, 100, 100),
                new Color(90, 85, 100, 0)
            }));
        g.fillRect(0, 0, size, size);
        g.dispose();
        return img;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Alpha = how strongly each pixel matches the score. Every pixel carries
     * the tint color (alpha 0 where no match) so blurring stays clean.
     * If name != null, logs how many pixels matched.
     */
    private static BufferedImage buildMask(BufferedImage bg, Rectangle area,
                                           Score score, Color tint, String name) {

        BufferedImage m = new BufferedImage(
                area.width, area.height, BufferedImage.TYPE_INT_ARGB);

        int tintRgb = tint.getRGB() & 0xFFFFFF;
        int matched = 0;

        for (int y = 0; y < area.height; y++) {
            for (int x = 0; x < area.width; x++) {

                int sx = area.x + x;
                int sy = area.y + y;

                int a = 0;

                if (sx >= 0 && sy >= 0
                        && sx < bg.getWidth() && sy < bg.getHeight()) {

                    int rgb = bg.getRGB(sx, sy);
                    float s = score.of((rgb >> 16) & 0xFF,
                                       (rgb >> 8) & 0xFF,
                                       rgb & 0xFF);

                    a = (int) (s * 255);
                    if (a > 40) matched++;
                }

                m.setRGB(x, y, (a << 24) | tintRgb);
            }
        }

        if (name != null) {
            System.out.println("[AmbientEffects] " + name
                    + ": " + matched + " pixels matched");
        }

        return m;
    }

    /** Blurs a mask and boosts its alpha so thin lines get a visible halo. */
    private static BufferedImage glowOf(BufferedImage mask, int size, float gain) {

        float[] k = new float[size * size];
        java.util.Arrays.fill(k, 1f / (size * size));

        BufferedImage blurred = new ConvolveOp(
                new Kernel(size, size, k), ConvolveOp.EDGE_NO_OP, null)
                .filter(mask, null);

        return new RescaleOp(
                new float[] { 1f, 1f, 1f, gain },
                new float[] { 0f, 0f, 0f, 0f }, null)
                .filter(blurred, null);
    }

    private static AlphaComposite alpha(float a) {
        return AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, a)));
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}