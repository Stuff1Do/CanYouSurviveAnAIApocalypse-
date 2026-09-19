package com.cysaaa.util;

public enum Lifeline {
    FIFTY_FIFTY("50:50", null, null, null, null),
    SWITCH_QUESTION("Switch the Question", null, null, null, null),
    PARALLEL_PROCESSING("Parallel Processing", "/images/parallelPr.png", "/images/parallelPrDisabled.png",
        "/buttons/parallelPrButton.png", "/buttons/parallelPrButtonDisabled.png"),
    NEURAL_PROMPT("Neural Prompt", "/images/NeuralPr.png", "/images/NeuralPrDisabled.png",
        "/buttons/NeuralPrButton.png", "/buttons/NeuralPrButtonDisabled.png"),
    MEMORY_FLUSH("Memory Flush", "/images/MemFl.png", "/images/MemFlDisabled.png",
        "/buttons/MemFlButton.png", "/buttons/MemFlButtonDisabled.png");

    private final String displayName;
    private final String activeIconPath;
    private final String disabledIconPath;
    private final String gameplayActiveIconPath;
    private final String gameplayDisabledIconPath;

    Lifeline(String displayName, String activeIconPath, String disabledIconPath,
             String gameplayActiveIconPath, String gameplayDisabledIconPath) {
        this.displayName = displayName;
        this.activeIconPath = activeIconPath;
        this.disabledIconPath = disabledIconPath;
        this.gameplayActiveIconPath = gameplayActiveIconPath;
        this.gameplayDisabledIconPath = gameplayDisabledIconPath;
    }

    public String getDisplayName() { return displayName; }
    public String getActiveIconPath() { return activeIconPath; }
    public String getDisabledIconPath() { return disabledIconPath; }
    public String getGameplayActiveIconPath() { return gameplayActiveIconPath; }
    public String getGameplayDisabledIconPath() { return gameplayDisabledIconPath; }
}