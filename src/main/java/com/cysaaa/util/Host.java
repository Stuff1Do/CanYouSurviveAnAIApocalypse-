package com.cysaaa.util;

public enum Host {
    HOST_1("/images/host1.png", "/images/host1_gameplay.png", Lifeline.PARALLEL_PROCESSING),
    HOST_2("/images/host2.png", "/images/host2_gameplay.png", Lifeline.NEURAL_PROMPT),
    HOST_3("/images/host3.png", "/images/host3_gameplay.png", Lifeline.MEMORY_FLUSH);

    private final String imagePath;
    private final String gameplayImagePath;
    private final Lifeline specialLifeline;

    Host(String imagePath, String gameplayImagePath, Lifeline specialLifeline) {
        this.imagePath = imagePath;
        this.gameplayImagePath = gameplayImagePath;
        this.specialLifeline = specialLifeline;
    }

    public String getImagePath() { 
       return imagePath; 
    }

    public String getGameplayImagePath() { 
       return gameplayImagePath; 
    }
    
    public Lifeline getSpecialLifeline() { 
       return specialLifeline; 
    }
}