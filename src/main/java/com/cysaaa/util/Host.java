package com.cysaaa.util;

public enum Host {
    HOST_1("Parallel Processing", "/images/host1.png",
           "/images/parallelPr.png", "/images/parallelPrDisabled.png"),
    HOST_2("Neural Prompt", "/images/host2.png",
           "/images/NeuralPr.png", "/images/NeuralPrDisabled.png"),
    HOST_3("Memory Flush", "/images/host3.png",
           "/images/MemFl.png", "/images/MemFlDisabled.png");

    private final String specialLifeline;
    private final String imagePath;
    private final String lifelineActivePath;
    private final String lifelineDisabledPath;

    Host(String specialLifeline, String imagePath, String lifelineActivePath, String lifelineDisabledPath) {
        this.specialLifeline = specialLifeline;
        this.imagePath = imagePath;
        this.lifelineActivePath = lifelineActivePath;
        this.lifelineDisabledPath = lifelineDisabledPath;
    }

    public String getSpecialLifeline() { return specialLifeline; }
    public String getImagePath() { return imagePath; }
    public String getLifelineActivePath() { return lifelineActivePath; }
    public String getLifelineDisabledPath() { return lifelineDisabledPath; }
}