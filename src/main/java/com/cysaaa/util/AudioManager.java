package com.cysaaa.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public final class AudioManager {

    private static Clip backgroundClip;
    private static String backgroundFileName = "";

    private AudioManager() {
    }

    public static void playEffect(String fileName) {
        play(fileName, false);
    }

    public static void playBackground(String fileName) {
        if (fileName.equals(backgroundFileName) && backgroundClip != null && backgroundClip.isRunning()) {
            return;
        }
        stopBackground();
        play(fileName, true);
    }

    public static void stopBackground() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
        backgroundFileName = "";
    }

    private static void play(String fileName, boolean loop) {
        try {
            URL resource = AudioManager.class.getResource("/audio/" + fileName);
            if (resource == null) {
                throw new IllegalArgumentException("Audio resource not found: " + fileName);
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.setLoopPoints(0, -1);
            clip.start();

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundClip = clip;
                backgroundFileName = fileName;
            }
        } catch (Exception exception) {
            System.err.println("Could not play audio '" + fileName + "': " + exception.getMessage());
        }
    }
}
