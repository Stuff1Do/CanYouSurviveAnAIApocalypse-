package com.cysaaa.terminal;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import com.cysaaa.gui.StateManager;
import com.cysaaa.util.AnswerState;
import com.cysaaa.util.GameState;
import com.cysaaa.util.Host;
import com.cysaaa.util.Lifeline;
import com.cysaaa.util.Question;
import com.cysaaa.util.QuestionLoader; 

public class TApp {
    Scanner scanner = new Scanner(System.in);
    Host selectedHost;

    public void run(){
        System.out.println("========WHO WANTS TO SURVIVE AN AI APOCALYPSE?========");
        System.out.println("1. Play");
        System.out.println("2. Exit");
        System.out.println("Choice: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

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

            int hostChoice = Integer.parseInt(scanner.nextLine().trim());
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
        List<Question> questionList = QuestionLoader.loadQuestions("/data/questions.csv");
        List<Question> randomizedQuestions = QuestionLoader.randomizeQuestions(questionList);

        //gameplay loop
        while (true) {
            int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();
            GameState withdrawState = StateManager.getInstance().getWithdrawState();
            GameState screenState = StateManager.getInstance().getScreenState();

            if (currentQuestionIndex > randomizedQuestions.size() || withdrawState == GameState.WITHDRAW || screenState == GameState.GAME_OVER) {
                break;
            }

            askQuestion(randomizedQuestions);

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

    public void askQuestion(List<Question> randomizedQuestions) {
        int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();
        //  ....show question, read player's choice ...
        Question question = randomizedQuestions.get(currentQuestionIndex - 1);
        printQuestionMenu(question, currentQuestionIndex);
        String choice = scanner.nextLine();
        if(choice.equals("random")){
            int i = 0;
            Question q;
            while(i < randomizedQuestions.size()){
                System.out.println("(NUMBER "+(i+1)+")");
                q = randomizedQuestions.get(i);
                System.out.println(q);
                i++;
            }
            askQuestion(randomizedQuestions);
            return;
        }

        if(choice.equals("withdraw")){
            StateManager.getInstance().setWithdrawState(GameState.WITHDRAW);
            return;
        }

        if(choice.equals("lifeline")){
           boolean successful = useLifeline();
           if(!successful){
                System.out.println("WARNING: Lifeline already used OR Invalid Input!");
           }
           askQuestion(randomizedQuestions);
           return;
        }

        
        //TODO: make into new function
        boolean correct = checkAnswer(question, choice); 

        if (correct) {
            StateManager.getInstance().setLastAnswer(AnswerState.CORRECT);
            System.out.println("Correct Answer!");
            //DialogueManager.getInstance().trigger(DialogueEvent.CORRECT_ANSWER);
        } else {
            StateManager.getInstance().setLastAnswer(AnswerState.WRONG);
            System.out.println("Wrong answer.");
            StateManager.getInstance().setScreenState(GameState.GAME_OVER);
            //DialogueManager.getInstance().trigger(DialogueEvent.WRONG_ANSWER);
            
        }

    }

    public void printQuestionMenu(Question question, int currentQuestionIndex){
        System.out.println("Question #"+currentQuestionIndex+":");
        System.out.println(question);
        System.out.println("Enter ('withdraw') to withdraw from the game.");//could have the option to withdraw
        System.out.println("(DEV) enter 'random' to show all randomized questions");
        System.out.println();
        System.out.println("Lifelines: ");
        Set<Lifeline> lifelines = StateManager.getInstance().getAvailableLifelines();
        if(lifelines.isEmpty()){
            System.out.println("No available lifelines.");
        }else{
            for(Lifeline lifeline : lifelines){
                System.out.print("#");
                System.out.println(lifeline.getDisplayName());
                
            }
        }
        
        System.out.println();
        System.out.println("Enter 'lifeline' to use lifeline: ");
        System.out.println("Choice(1-4): ");
    }

    public boolean useLifeline(){
        System.out.println("Enter #number of lineline: ");
        System.out.println("Choice: ");
        int lifeline = Integer.parseInt(scanner.nextLine().trim());
        if(lifeline == 1){
            boolean used = StateManager.getInstance().isLifelineUsed(Lifeline.FIFTY_FIFTY);
            if(!used){
                 StateManager.getInstance().useLifeline(Lifeline.FIFTY_FIFTY);
                 System.out.println("Used lifeline 1"); //placeholder
                 //function for lifeline 1
            }else{
                return false;
            }  
        }else if(lifeline == 2){
             boolean used = StateManager.getInstance().isLifelineUsed(Lifeline.SWITCH_QUESTION);
            if(!used){
                 StateManager.getInstance().useLifeline(Lifeline.SWITCH_QUESTION);
                 System.out.println("Used lifeline 2"); //placeholder
                 //function for lifeline 1
            }else{
                return false;
            }
            
        }else if(lifeline == 3){
            Host currentHost = StateManager.getInstance().getCurrentHost();
            boolean used = StateManager.getInstance().isLifelineUsed(currentHost.getSpecialLifeline());
            if(!used){
                 StateManager.getInstance().useLifeline(currentHost.getSpecialLifeline());
                 System.out.println("Used special lifeline."); //placeholder
                 //function for lifeline 1
            }else{
                return false;
            }
        }else{
            return false;
        }
        return true;

    }

    public void lifeline1(){

    }
    public void lifeline2(){

    }
    public void specialLifelin(){

    }

    public boolean checkAnswer(Question question, String choice) {
        try {
            int choiceIndex = Integer.parseInt(choice.trim()) - 1;
            return question.isCorrect(choiceIndex);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, counted as wrong.");
            return false;
        }
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
