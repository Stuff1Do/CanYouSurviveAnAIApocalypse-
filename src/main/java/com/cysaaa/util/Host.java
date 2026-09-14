package com.cysaaa.util;

public enum Host {
    HOST_1("/images/host1.png", Lifeline.PARALLEL_PROCESSING),
    HOST_2("/images/host2.png", Lifeline.NEURAL_PROMPT),
    HOST_3("/images/host3.png", Lifeline.MEMORY_FLUSH);

    private final String imagePath;
    private final Lifeline specialLifeline;

    Host(String imagePath, Lifeline specialLifeline) {
        this.imagePath = imagePath;
        this.specialLifeline = specialLifeline;
    }

    public String getImagePath() { 
       return imagePath; 
    }
    
    public Lifeline getSpecialLifeline() { 
       return specialLifeline; 
    }
}