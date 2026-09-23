package com.cysaaa.util;

import java.util.Set;

import com.cysaaa.util.AnswerState;
import com.cysaaa.util.GameState;
import com.cysaaa.util.Host;
import com.cysaaa.util.Lifeline; 
import java.util.EnumSet;

public class StateManager {
    private static StateManager instance;

    private final Set<Lifeline> usedLifelines = EnumSet.noneOf(Lifeline.class);

    private GameState screenState = GameState.MAIN_MENU;
    private int currentQuestionNumber = 1;
    private AnswerState lastAnswer = AnswerState.UNANSWERED;
    private GameState withdrawState;
    private Host currentHost;

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


    public boolean isLifelineUsed(Lifeline lifeline) {
        return usedLifelines.contains(lifeline);
    }

    public void useLifeline(Lifeline lifeline) {
        usedLifelines.add(lifeline);
    }

    public Set<Lifeline> getAvailableLifelines() {
        Set<Lifeline> available = EnumSet.of(Lifeline.FIFTY_FIFTY, Lifeline.SWITCH_QUESTION);
        if (currentHost != null) {
            available.add(currentHost.getSpecialLifeline());
        }
        available.removeAll(usedLifelines);
        return available;
    }
    // Call this when starting a new game/playthrough
    public void reset() {
        screenState = GameState.MAIN_MENU;
        currentQuestionNumber = 1;
        lastAnswer = AnswerState.UNANSWERED;
        withdrawState = null;
        currentHost = null;
        usedLifelines.clear();
    }
}