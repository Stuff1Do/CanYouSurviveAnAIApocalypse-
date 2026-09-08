package com.cysaaa.util;

public enum Host {
    HOST_1("Overclock", "/hosts/host1.png", "/lifelines/overclock.png"),
    HOST_2("Neural Prompt", "/hosts/host2.png", "/lifelines/neural_prompt.png"),
    HOST_3("Memory Flush", "/hosts/host3.png", "/lifelines/memory_flush.png");

    private final String specialLifeline;
    private final String imagePath;
    private final String lifelineImagePath;

    Host(String specialLifeline, String imagePath, String lifelineImagePath) {
        this.specialLifeline = specialLifeline;
        this.imagePath = imagePath;
        this.lifelineImagePath = lifelineImagePath;
    }

    public String getSpecialLifeline() {
        return specialLifeline;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getLifelineImagePath() {
        return lifelineImagePath;
    }
}