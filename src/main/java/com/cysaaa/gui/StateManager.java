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

    private StateManager() {}

    public static StateManager getInstance() {
        if (instance == null){
            instance = new StateManager();
        }
        return instance;
    }

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

}