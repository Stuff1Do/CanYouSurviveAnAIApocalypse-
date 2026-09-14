package com.cysaaa.gui;

import com.cysaaa.util.AnswerState;
import com.cysaaa.util.GameState;
import com.cysaaa.util.Host; 

public class StateManager {
    private static StateManager instance;

    private GameState screenState = GameState.MAIN_MENU;
    private int currentQuestionNumber = 1;
    private AnswerState lastAnswer = AnswerState.UNANSWERED;
    private GameState withdrawState;
    private Host currentHost;

    private boolean traceEliminationUsed = false;
    private boolean systemRerouteUsed = false;
    private boolean specialLifelineUsed = false;

    private StateManager() {}

    public static StateManager getInstance() {
        if (instance == null){
            instance = new StateManager();
        }
        return instance;
    }

    // ... existing getters/setters unchanged ...
     // getters/setters for each field
    public GameState getScreenState() { 
        return screenState; 
    }
    public void setScreenState(GameState s) { 
        screenState = s; 
    }

    public Host getCurrentHost() {
    return currentHost;
    }

    public void setCurrentHost(Host h) {
        currentHost = h;
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber; 
    }
    public void nextQuestion() { 
        currentQuestionNumber++; 
    }   

    public void setLastAnswer(AnswerState result) { 
        lastAnswer = result; 
    }

    public AnswerState getLastAnswer() { 
        return lastAnswer; 
    }      

    public void setWithdrawState(GameState state){
        withdrawState = state;
    }

    public GameState getWithdrawState(){
        return withdrawState;
    }

    public boolean isTraceEliminationUsed() { return traceEliminationUsed; }
    public void useTraceElimination() { traceEliminationUsed = true; }

    public boolean isSystemRerouteUsed() { return systemRerouteUsed; }
    public void useSystemReroute() { systemRerouteUsed = true; }

    public boolean isSpecialLifelineUsed() { return specialLifelineUsed; }
    public void useSpecialLifeline() { specialLifelineUsed = true; }

    // Call this when starting a new game/playthrough
    public void reset() {
        screenState = GameState.MAIN_MENU;
        currentQuestionNumber = 1;
        lastAnswer = AnswerState.UNANSWERED;
        withdrawState = null;
        currentHost = null;
        traceEliminationUsed = false;
        systemRerouteUsed = false;
        specialLifelineUsed = false;
    }
}