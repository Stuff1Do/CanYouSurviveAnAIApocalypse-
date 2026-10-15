package com.cysaaa.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.function.DoubleFunction;

/**
 * Random crows that fly across the scene, sometimes landing on perches
 * (rooftop, red sign, robot's head), idling, then taking off again.
 * Coordinates are background-image pixels (1920 x 1080).
 */
public class CrowFlock {

    // true = draws a red dot on every perch so you can line them up.
    private static final boolean DEBUG_PERCHES = false;

    private static final int W = 1920;
    private static final int H = 1080;

    /** Overall speed multiplier: flight, takeoff, landing and wing flaps. */
    private static final double SPEED = 1.6;

    private static final int MAX_CROWS = 5;
    private static final double MIN_SPAWN_GAP = 2.5;   // seconds
    private static final double MAX_SPAWN_GAP = 7.0;
    private static final double LAND_CHANCE = 0.5;     // chance a crow lands
    private static final double SIZE = 1.4;            // overall crow size

    private static final Color BODY     = new Color(12, 8, 22);
    private static final Color FAR_WING = new Color(8, 5, 16);
    private static final Color RIM      = new Color(200, 120, 255, 110);
    private static final Color EYE      = new Color(255, 220, 255);

    private enum State { CROSSING, APPROACH, LANDING, PERCHED, TAKEOFF }

    private static class Perch {
        final double x, y;                          // resting position of the feet
        final DoubleFunction<double[]> follow;      // optional {dx, dy} by time
        final int weight;                           // higher = picked more often
        boolean taken;

        Perch(double x, double y) { this(x, y, 1, null); }

        Perch(double x, double y, int weight, DoubleFunction<double[]> follow) {
            this.x = x;
            this.y = y;
            this.weight = weight;
            this.follow = follow;
        }
    }

    private static class Crow {
        State state;
        double x, y, vx, vy, speed;
        int dir;                         // +1 faces right, -1 faces left
        double scale, seed;
        double phase;                    // wing flap phase
        boolean gliding;
        double glideTimer;
        double stateTime;
        Perch perch;
        double ax, ay;                   // approach point
        double sx, sy, landDur;          // landing start + duration
        double perchTime, nextTurn, nextFlutter, flutter;
    }

    private final ArrayList<Perch> perches = new ArrayList<>();
    private final ArrayList<Crow> crows = new ArrayList<>();
    private final Random random = new Random();

    private double now;
    private double lastT = -1;
    private double spawnTimer = 1.5;
    private boolean firstGroup = true;

    public CrowFlock(CharacterAnimator characters) {

        // Right-hand rooftop ledge
        perches.add(new Perch(1605, 826));
        perches.add(new Perch(1705, 826));
        perches.add(new Perch(1805, 826));

        // Top of the red neon sign
        perches.add(new Perch(1795, 187));

        // Top of the robot cat's head: rides along with the head animation.
        // Weight 3 = picked about 3x as often as a single other spot.
        // (413 appears twice: keep both the same if you move it.)
        perches.add(new Perch(1335, 413, 3, t -> characters.headOffset(t, 413)));

        reset();
    }

    /** Call whenever the menu is shown: restarts the scene with a guaranteed flyover. */
    public void reset() {

        for (Perch p : perches) {
            p.taken = false;
        }

        crows.clear();

        lastT = -1;
        spawnTimer = 1.5;      // first crows appear ~1.5 s after the menu shows
        firstGroup = true;

        // One crow already perched so the scene isn't empty.
        Perch p = freePerch();

        if (p != null) {
            p.taken = true;

            Crow c = newCrow();
            c.state = State.PERCHED;
            c.perch = p;
            c.x = perchX(p);
            c.y = perchY(p);
            c.dir = random.nextBoolean() ? 1 : -1;
            startPerch(c);
            c.stateTime = random.nextDouble() * 2;
            crows.add(c);
        }
    }

    /** g must be scaled so 1 unit = 1 background-image pixel. */
    public void paint(Graphics2D g, double t) {

        now = t;
        double dt = lastT < 0 ? 0 : Math.min(0.1, t - lastT);
        lastT = t;

        update(dt);

        for (Crow c : crows) {
            draw(g, c, t);
        }

        if (DEBUG_PERCHES) {
            g.setColor(Color.RED);
            for (Perch p : perches) {
                g.fillOval((int) perchX(p) - 4, (int) perchY(p) - 4, 8, 8);
            }
        }
    }

    // =========================================================
    // PERCH POSITIONS (some perches move)
    // =========================================================

    private double perchX(Perch p) {
        return p.x + (p.follow == null ? 0 : p.follow.apply(now)[0]);
    }

    private double perchY(Perch p) {
        return p.y + (p.follow == null ? 0 : p.follow.apply(now)[1]);
    }

    // =========================================================
    // BEHAVIOUR
    // =========================================================

    private Crow newCrow() {
        Crow c = new Crow();
        c.scale = SIZE * (0.85 + random.nextDouble() * 0.35);
        c.seed = random.nextDouble() * 100;
        c.phase = random.nextDouble() * Math.PI * 2;
        c.glideTimer = 0.5 + random.nextDouble();
        return c;
    }

    private Perch freePerch() {

        int total = 0;
        for (Perch p : perches) {
            if (!p.taken) total += p.weight;
        }

        if (total == 0) return null;

        int pick = random.nextInt(total);

        for (Perch p : perches) {
            if (p.taken) continue;
            pick -= p.weight;
            if (pick < 0) return p;
        }

        return null;
    }

    private void spawnGroup() {

        boolean guaranteed = firstGroup;   // the first group always flies across
        firstGroup = false;

        int n = guaranteed
                ? 2
                : 1 + (random.nextDouble() < 0.35 ? 1 : 0)
                    + (random.nextDouble() < 0.15 ? 1 : 0);

        boolean fromLeft = random.nextBoolean();

        for (int i = 0; i < n && crows.size() < MAX_CROWS; i++) {

            Crow c = newCrow();

            c.dir = fromLeft ? 1 : -1;
            c.x = fromLeft ? -60 - i * 90 : W + 60 + i * 90;
            c.y = 60 + random.nextDouble() * 380;
            c.speed = (300 + random.nextDouble() * 160) * SPEED;

            Perch p = (!guaranteed && random.nextDouble() < LAND_CHANCE)
                    ? freePerch() : null;

            if (p != null) {
                p.taken = true;
                c.perch = p;
                c.state = State.APPROACH;
                c.dir = perchX(p) >= c.x ? 1 : -1;
                c.ax = perchX(p) - c.dir * 220;
                c.ay = Math.max(30, perchY(p) - 120);
            } else {
                c.state = State.CROSSING;
                c.vx = c.dir * c.speed;
                c.vy = (-20 + random.nextDouble() * 60) * SPEED;
            }

            crows.add(c);
        }
    }

    private void update(double dt) {

        spawnTimer -= dt;
        if (spawnTimer <= 0) {
            if (crows.size() < MAX_CROWS) {
                spawnGroup();
            }
            spawnTimer = MIN_SPAWN_GAP
                    + random.nextDouble() * (MAX_SPAWN_GAP - MIN_SPAWN_GAP);
        }

        Iterator<Crow> it = crows.iterator();

        while (it.hasNext()) {

            Crow c = it.next();
            c.stateTime += dt;

            switch (c.state) {

                case CROSSING:
                    updateCrossing(c, dt);
                    if ((c.vx > 0 && c.x > W + 150)
                            || (c.vx < 0 && c.x < -150)
                            || c.y < -150 || c.y > H + 150) {
                        it.remove();
                    }
                    break;

                case APPROACH:
                    updateApproach(c, dt);
                    break;

                case LANDING:
                    updateLanding(c, dt);
                    break;

                case PERCHED:
                    updatePerched(c, dt);
                    break;

                case TAKEOFF:
                    updateTakeoff(c, dt);
                    break;
            }
        }
    }

    private void updateCrossing(Crow c, double dt) {

        c.glideTimer -= dt;
        if (c.glideTimer <= 0) {
            c.gliding = !c.gliding;
            c.glideTimer = c.gliding
                    ? 0.5 + random.nextDouble() * 0.7
                    : 1.0 + random.nextDouble() * 1.4;
        }

        if (!c.gliding) {
            c.phase += 22 * SPEED * dt;
        }

        c.x += c.vx * dt;
        c.y += (c.vy + Math.cos(c.stateTime * 2.2 * SPEED + c.seed) * 28) * dt;
    }

    private void updateApproach(Crow c, double dt) {

        c.gliding = false;
        c.phase += 22 * SPEED * dt;

        double dx = c.ax - c.x;
        double dy = c.ay - c.y;
        double dist = Math.hypot(dx, dy);

        if (dist < 14) {
            c.state = State.LANDING;
            c.stateTime = 0;
            c.sx = c.x;
            c.sy = c.y;
            c.landDur = (1.1 + random.nextDouble() * 0.4) / SPEED;
            c.dir = perchX(c.perch) >= c.sx ? 1 : -1;
            return;
        }

        double step = Math.min(c.speed * dt, dist);
        c.x += dx / dist * step;
        c.y += dy / dist * step;
        c.dir = dx >= 0 ? 1 : -1;
    }

    private void updateLanding(Crow c, double dt) {

        double u = Math.min(1, c.stateTime / c.landDur);
        double e = 1 - (1 - u) * (1 - u);          // ease out

        // Aim at the perch's CURRENT position (the robot's head moves)
        c.x = c.sx + (perchX(c.perch) - c.sx) * e;
        c.y = c.sy + (perchY(c.perch) - c.sy) * e;

        c.gliding = false;
        c.phase += (28 - 8 * u) * SPEED * dt;      // flaring flaps

        if (u >= 1) {
            c.state = State.PERCHED;
            c.stateTime = 0;
            startPerch(c);
        }
    }

    private void startPerch(Crow c) {
        c.perchTime = 4 + random.nextDouble() * 9;
        c.nextTurn = 2 + random.nextDouble() * 4;
        c.nextFlutter = 1.5 + random.nextDouble() * 4;
        c.flutter = 0;
    }

    private void updatePerched(Crow c, double dt) {

        // Ride along with the perch (the robot's head bobs and sways)
        c.x = perchX(c.perch);
        c.y = perchY(c.perch);

        c.nextTurn -= dt;
        if (c.nextTurn <= 0) {
            c.dir = -c.dir;
            c.nextTurn = 2 + random.nextDouble() * 5;
        }

        c.nextFlutter -= dt;
        if (c.nextFlutter <= 0) {
            c.flutter = 0.5;
            c.nextFlutter = 3 + random.nextDouble() * 6;
        }

        if (c.flutter > 0) {
            c.flutter -= dt;
        }

        if (c.stateTime >= c.perchTime) {
            c.perch.taken = false;
            c.perch = null;
            c.state = State.TAKEOFF;
            c.stateTime = 0;
        }
    }

    private void updateTakeoff(Crow c, double dt) {

        c.gliding = false;
        c.phase += 28 * SPEED * dt;

        double k = Math.min(1, c.stateTime / (0.9 / SPEED));
        c.vx = c.dir * (40 + 170 * k) * SPEED;
        c.vy = (-110 - 40 * k) * SPEED;

        c.x += c.vx * dt;
        c.y += c.vy * dt;

        if (c.stateTime > 1.1 / SPEED) {
            c.state = State.CROSSING;
            c.vx = c.dir * 200 * SPEED;
            c.vy = -40 * SPEED;
            c.glideTimer = 0.6;
        }
    }

    // =========================================================
    // DRAWING (procedural crow silhouette)
    // =========================================================

    private double wingAngle(Crow c, double phase) {
        return c.gliding ? 0.25 : Math.sin(phase) * 0.85 + 0.2;
    }

    private Path2D wing(double a, double sx, double sy, double len) {

        double tipX = sx - 6 - 7 * Math.cos(a);
        double tipY = sy - len * Math.sin(a);

        Path2D p = new Path2D.Double();
        p.moveTo(sx + 5, sy);
        p.quadTo((sx + tipX) / 2 + 6, (sy + tipY) / 2, tipX, tipY);
        p.quadTo((sx + tipX) / 2 - 8, (sy + tipY) / 2 + 3, sx - 10, sy + 1);
        p.closePath();
        return p;
    }

    private void draw(Graphics2D g0, Crow c, double t) {

        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        boolean perched = c.state == State.PERCHED;

        // While perched, (x, y) is the feet; lift the body above them.
        g.translate(c.x, c.y - (perched ? 10 * c.scale : 0));
        g.scale(c.dir * c.scale, c.scale);

        if (perched) {
            g.setColor(BODY);
            g.setStroke(new BasicStroke(1.5f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(-1, 4, -1, 10);
            g.drawLine(3, 4, 3, 10);
            g.rotate(-0.45);          // upright perching posture
        } else {
            // Far wing, drawn behind the body and slightly out of phase
            g.setColor(FAR_WING);
            g.fill(wing(wingAngle(c, c.phase - 0.6), 1, -3.5, 20));
        }

        // Tail
        Path2D tail = new Path2D.Double();
        tail.moveTo(-9, -2);
        tail.lineTo(-21, -4);
        tail.lineTo(-22, 1);
        tail.lineTo(-9, 3);
        tail.closePath();

        Ellipse2D body = new Ellipse2D.Double(-11, -4.5, 22, 9);

        g.setColor(BODY);
        g.fill(tail);
        g.fill(body);

        // Head, with a slow nod while perched
        double nod = perched
                ? Math.max(0, Math.sin(t * 1.7 + c.seed)) * 1.6
                : 0;

        Graphics2D hg = (Graphics2D) g.create();
        hg.translate(0, nod);

        Ellipse2D head = new Ellipse2D.Double(6, -7, 9, 9);

        Path2D beak = new Path2D.Double();
        beak.moveTo(14, -4.5);
        beak.lineTo(21, -2.8);
        beak.lineTo(14, -1);
        beak.closePath();

        hg.setColor(BODY);
        hg.fill(head);
        hg.fill(beak);

        hg.setColor(EYE);
        hg.fill(new Ellipse2D.Double(11.5, -4.4, 1.7, 1.7));
        hg.dispose();

        // Faint rim light so the silhouette reads against the dark city
        g.setColor(RIM);
        g.setStroke(new BasicStroke(0.6f));
        g.draw(body);

        // Near wing
        if (!perched) {

            Path2D w = wing(wingAngle(c, c.phase), 3, -3, 22);

            g.setColor(BODY);
            g.fill(w);
            g.setColor(RIM);
            g.draw(w);

        } else {

            // Folded wing, with an occasional ruffle
            Graphics2D wg = (Graphics2D) g.create();

            if (c.flutter > 0) {
                wg.rotate(-0.35 * Math.abs(Math.sin(c.flutter * 22)), 4, -3);
            }

            Path2D folded = new Path2D.Double();
            folded.moveTo(4, -3.5);
            folded.quadTo(-6, -5.5, -17, 0);
            folded.quadTo(-8, 3, 2, 2);
            folded.closePath();

            wg.setColor(BODY);
            wg.fill(folded);
            wg.setColor(RIM);
            wg.draw(folded);
            wg.dispose();
        }

        g.dispose();
    }
}