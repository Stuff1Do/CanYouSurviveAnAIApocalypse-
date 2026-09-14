package com.cysaaa.util;

public enum Lifeline {
    FIFTY_FIFTY("50:50", null, null),
    SWITCH_QUESTION("Switch the Question", null,null),
    PARALLEL_PROCESSING("Parallel Processing", "/images/parallelPr.png", "/images/parallelPrDisabled.png"),
    NEURAL_PROMPT("Neural Prompt", "/images/NeuralPr.png", "/images/NeuralPrDisabled.png"),
    MEMORY_FLUSH("Memory Flush", "/images/MemFl.png", "/images/MemFlDisabled.png");

    private final String displayName;
    private final String activeIconPath;
    private final String disabledIconPath;

    Lifeline(String displayName, String activeIconPath, String disabledIconPath) {
        this.displayName = displayName;
        this.activeIconPath = activeIconPath;
        this.disabledIconPath = disabledIconPath;
    }

    public String getDisplayName() { 
        return displayName; 
    }
    public String getActiveIconPath() { 
        return activeIconPath; 
    }
    public String getDisabledIconPath() { 
        return disabledIconPath; 
    }
}