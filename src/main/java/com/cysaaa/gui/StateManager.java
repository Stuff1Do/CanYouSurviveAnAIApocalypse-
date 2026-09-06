package com.cysaaa.gui;

import com.cysaaa.util.GameState;

public class StateManager {
    private static StateManager instance;

    private GameState currentState;

    private StateManager() {
        currentState = GameState.MAIN_MENU; // default state on launch
    }

    public static StateManager getInstance() {
        if (instance == null) {
            instance = new StateManager();
        }
        return instance;
    }
}
