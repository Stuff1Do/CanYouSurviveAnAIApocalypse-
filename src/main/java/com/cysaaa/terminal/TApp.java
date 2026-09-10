package com.cysaaa.terminal;

import java.util.Scanner;

import com.cysaaa.gui.StateManager;
import com.cysaaa.util.AnswerState;
import com.cysaaa.util.GameState;
import com.cysaaa.util.Host; 

public class TApp {
    Scanner scanner = new Scanner(System.in);
    Host selectedHost;

    public void run(){
        System.out.println("========WHO WANTS TO SURVIVE AN AI APOCALYPSE?========");
        System.out.println("1. Play");
        System.out.println("2. Exit");
        System.out.println("Choice: ");
        int choice = scanner.nextInt();

        if(choice == 1){
            hostSelection();
            startGame();
        }else{
            return;
        }
        
    }

    public void hostSelection(){
        StateManager.getInstance().setScreenState(GameState.HOST_SELECT);

        boolean loop = true;
        while(loop){
            System.out.println("Please choose a host.");   
            System.out.println("1. Parallel Processing(two guesses)");
            System.out.println("2. Neural Prompt(vague ai hint)");
            System.out.println("3. Memory Flush(swap question)");
            System.out.println("Choice: ");

            int hostChoice = scanner.nextInt();

            switch (hostChoice) {
                case 1: 
                        StateManager.getInstance().setCurrentHost(Host.HOST_1);
                        loop = false;
                        break;
                case 2: 
                        StateManager.getInstance().setCurrentHost(Host.HOST_2);
                        loop = false;
                        break;
                case 3: 
                        StateManager.getInstance().setCurrentHost(Host.HOST_3);
                        loop = false;
                        break;
                default: System.out.println("Invalid choice.");
            }
        }
        selectedHost = StateManager.getInstance().getCurrentHost();
        System.out.println("Current state: " + selectedHost);
    }

    public void startGame(){
        StateManager.getInstance().setScreenState(GameState.PLAYING);
        StateManager.getInstance().setWithdrawState(GameState.PLAYING);

        //load questions and randomize questions here
        loadQuestions();

        //gameplay loop
        while (true) {
            int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();
            GameState withdrawState = StateManager.getInstance().getWithdrawState();
            GameState screenState = StateManager.getInstance().getScreenState();

            if (currentQuestionIndex > 15 || withdrawState == GameState.WITHDRAW || screenState == GameState.GAME_OVER) {
                break;
            }

            askQuestion();

            // only increment count if the game is still active
            if (StateManager.getInstance().getScreenState() != GameState.GAME_OVER && StateManager.getInstance().getWithdrawState() != GameState.WITHDRAW) {
                StateManager.getInstance().nextQuestion();
            }
        }

        if (StateManager.getInstance().getCurrentQuestionNumber() > 15) { //would be even better to check if question is #15 and check if correct
            StateManager.getInstance().setScreenState(GameState.VICTORY); // but dont have questions yet
        }

        endGame();

    }

    public void askQuestion() {
        
        //  ....show question, read player's choice ...


        //could have the option to withdraw

        int playerChoice = 1; //placeholder
        boolean correct = checkAnswer(playerChoice); // however we determine this

        if (correct) {
            StateManager.getInstance().setLastAnswer(AnswerState.CORRECT);
            //DialogueManager.getInstance().trigger(DialogueEvent.CORRECT_ANSWER);
        } else {
            StateManager.getInstance().setLastAnswer(AnswerState.WRONG);
            StateManager.getInstance().setScreenState(GameState.GAME_OVER);
            //DialogueManager.getInstance().trigger(DialogueEvent.WRONG_ANSWER);
            
        }
    }

    public boolean checkAnswer(int choice){

        return false;
    }

    public void loadQuestions(){
        //placeholder, eventually will be placed in a general class
    }

    public void checkCheckpoint(){
       
        int progress = StateManager.getInstance().getCurrentQuestionNumber();
        if(progress >= 7 && progress < 11){
            System.out.println("You survived as a Cybernetic Core!");
        }else if(progress >= 11 && progress < 15){
            System.out.println("You survived as a Synthetic Lifeform!");
        }else{
            System.out.println("You did not survive an AI Apocalypse.");
        }
        
    }

   

    public void endGame(){
        GameState screenState = StateManager.getInstance().getScreenState();
        GameState withdrawState = StateManager.getInstance().getWithdrawState();

        if(withdrawState == GameState.WITHDRAW){
            checkCheckpoint();
        }


        if(screenState == GameState.VICTORY){
            System.out.println("YOU WON!");
        }else if(screenState == GameState.GAME_OVER){
            checkCheckpoint();
        }
    }



}
