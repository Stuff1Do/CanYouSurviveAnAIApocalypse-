package com.cysaaa.gui.components;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WindEffect
 *
 * Drifting wind streaks with a gusting strength. Call update() each frame
 * from a Timer, then paint(g2) from a panel's paintComponent.
 */
public class WindEffect {

    private static class Particle {
        float x, y;
        float speed;   // per-particle speed (fake depth: slow = far, fast = near)
        float length;  // streak length
        float drift;   // slight vertical wobble
        int alpha;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final int count;
    private int width, height;
    private float time = 0f;
    private float gust = 1f; // current wind strength multiplier

    public WindEffect(int count) {
        this.count = count;
    }

    /** Call whenever the panel size is known/changes. */
    public void setSize(int w, int h) {
        if (w == width && h == height) return;
        width = w;
        height = h;
        particles.clear();
        for (int i = 0; i < count; i++) {
            Particle p = new Particle();
            respawn(p, true);
            particles.add(p);
        }
    }

    private void respawn(Particle p, boolean anywhere) {
        p.x = anywhere ? random.nextFloat() * width : -random.nextFloat() * 200;
        p.y = random.nextFloat() * height;
        p.speed = 2f + random.nextFloat() * 6f;
        p.length = 30 + random.nextFloat() * 90;
        p.drift = (random.nextFloat() - 0.5f) * 0.4f;
        p.alpha = 25 + random.nextInt(60);
    }

    public void update() {
        time += 0.02f;
        // two overlapping sine waves = irregular gusts, never fully still
        gust = 1.0f + 0.6f * (float) Math.sin(time) + 0.3f * (float) Math.sin(time * 2.3f);
        gust = Math.max(0.2f, gust);

        for (Particle p : particles) {
            p.x += p.speed * gust;
            p.y += p.drift + (float) Math.sin(time * 3 + p.x * 0.01f) * 0.4f;
            if (p.x - p.length > width) respawn(p, false);
        }
    }

    public void paint(Graphics2D g) {
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Particle p : particles) {
            float len = p.length * gust; // streaks stretch in strong gusts
            g.setColor(new Color(225, 190, 255, p.alpha)); // soft lavender to match your palette
            g.drawLine((int) (p.x - len), (int) p.y, (int) p.x, (int) (p.y + p.drift * 10));
        }
    }
}