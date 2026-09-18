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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TApp {
    Scanner scanner = new Scanner(System.in);
    Host selectedHost;


    List<Question> questionList;
    List<Question> randomizedQuestions;
    Set<Integer> eliminatedChoices = new HashSet<>();
    int eliminatedForQuestionIndex = -1;
    boolean twoGuesses = false;
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
        questionList = QuestionLoader.loadQuestions("/data/questions.csv");
        randomizedQuestions = QuestionLoader.randomizeQuestions(questionList);

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
            boolean successful = useLifeline(question, currentQuestionIndex);
            if(!successful){
                    System.out.println("WARNING: Lifeline already used OR Invalid Input!");
            }
            
            askQuestion(randomizedQuestions);
            return;
        }

        
        //TODO: make into new function
        boolean correct = checkAnswer(question, choice); 

        if(twoGuesses && !correct){
            twoGuesses = false;
            System.out.println("Parallel Processing is active! You can guess again.");
            eliminatedChoices.add(Integer.parseInt(choice.trim()) - 1);
            askQuestion(randomizedQuestions);
            return;
        }

        if (correct) {
            StateManager.getInstance().setLastAnswer(AnswerState.CORRECT);
            System.out.println("Correct Answer!");
            System.out.println();
            //DialogueManager.getInstance().trigger(DialogueEvent.CORRECT_ANSWER);
        } else {
            StateManager.getInstance().setLastAnswer(AnswerState.WRONG);
            System.out.println("Wrong answer.");
            StateManager.getInstance().setScreenState(GameState.GAME_OVER);
            //DialogueManager.getInstance().trigger(DialogueEvent.WRONG_ANSWER);
            
        }

    }

    
    public boolean checkAnswer(Question question, String choice) {
        try {
            int choiceIndex = Integer.parseInt(choice.trim()) - 1;
            boolean result = question.isCorrect(choiceIndex);

            return result;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, counted as wrong.");
            return false;
        }
    }

    public void printQuestionMenu(Question question, int currentQuestionIndex){

         if (eliminatedForQuestionIndex != currentQuestionIndex) {
            eliminatedChoices.clear();
            eliminatedForQuestionIndex = currentQuestionIndex;
        }

        System.out.println("Question #"+currentQuestionIndex+":");
        
        System.out.println("[" + question.getType() + "] " + question.getQuestionText());

        String[] choices = question.getChoices();
        for (int i = 0; i < choices.length; i++) {
            char label = (char) ('A' + i);
            if (eliminatedChoices.contains(i)) {
                System.out.println(label + ". [ELIMINATED]");
            } else {
                System.out.println(label + ". " + choices[i]);
            }
        }


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

    public boolean useLifeline(Question question, int currentQuestionIndex){
        System.out.println("Enter #number of lineline: ");
        System.out.println("Choice: ");
        int lifeline = Integer.parseInt(scanner.nextLine().trim());
        if(lifeline == 1){
            boolean used = StateManager.getInstance().isLifelineUsed(Lifeline.FIFTY_FIFTY);
            if(!used){
                StateManager.getInstance().useLifeline(Lifeline.FIFTY_FIFTY);
                System.out.println("Used lifeline 1"); //placeholder
                //function for lifeline 1
                lifeline1(question);
            }else{
                return false;
            }  
        }else if(lifeline == 2){
            boolean used = StateManager.getInstance().isLifelineUsed(Lifeline.SWITCH_QUESTION);
            if(!used){
                StateManager.getInstance().useLifeline(Lifeline.SWITCH_QUESTION);
                System.out.println("Used lifeline 2"); //placeholder
                
                lifeline2(currentQuestionIndex);
            }else{
                return false;
            }
            
        }else if(lifeline == 3){
            Host currentHost = StateManager.getInstance().getCurrentHost();
            boolean used = StateManager.getInstance().isLifelineUsed(currentHost.getSpecialLifeline());
            if(!used){
                StateManager.getInstance().useLifeline(currentHost.getSpecialLifeline());
                System.out.println("Used special lifeline."); //placeholder
                //function for lifeline 3
                specialLifeline(question);
            }else{
                return false;
            }
        }else{
            return false;
        }
        return true;

    }

    public void lifeline1(Question question){
        List<Integer> wrongIndices = new ArrayList<>();
        int correctIndex = question.getCorrectIndex();
        for(int i = 0; i < 4; i++){
            if(i != correctIndex){
                wrongIndices.add(i);
            }
        }

        Collections.shuffle(wrongIndices);
        eliminatedChoices.add(wrongIndices.get(0));
        eliminatedChoices.add(wrongIndices.get(1));
        System.out.println("Two wrong answers eliminated!");
    }
    public void lifeline2(int currentQuestionIndex){
        Question current = randomizedQuestions.get(currentQuestionIndex - 1);
        String category = current.getType();

        //make sure its a diff category
        List<Question> sameCategoryUnused = new ArrayList<>();
        for (Question q : questionList) {
            if (!q.getType().equals(category) && !randomizedQuestions.contains(q)) {
                sameCategoryUnused.add(q);
            }
        }

        //randomize picking a question with diff category
        Random random = new Random();
        int indexMax = sameCategoryUnused.size();
        Question replacement = sameCategoryUnused.get(random.nextInt(indexMax));
        randomizedQuestions.set(currentQuestionIndex - 1, replacement);

        // clear any eliminations, since this is now a different question
        eliminatedChoices.clear();

        System.out.println("Question swapped!");
    }
    public void specialLifeline(Question question){

        Lifeline lifeline = selectedHost.getSpecialLifeline();
        String lifelineName = lifeline.getDisplayName();
        System.out.println("Used "+lifelineName);
        if(lifelineName == "Memory Flush"){
            memoryFlush();
        }else if(lifelineName == "Neural Prompt"){
            neuralPrompt(question);
        }else if(lifelineName == "Parallel Processing"){
            parallelProcessing();
        }
    }

    public void memoryFlush(){
        int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();
        Question current = randomizedQuestions.get(currentQuestionIndex - 1);
        String category = current.getType();
        List<Question> sameCategoryUsed = new ArrayList<>();
        for (Question q : questionList) {
            if (q.getType().equals(category) && !randomizedQuestions.contains(q)) {
                sameCategoryUsed.add(q);
            }
        }

        Random random = new Random();
        int indexMax = sameCategoryUsed.size();
        Question replacement = sameCategoryUsed.get(random.nextInt(indexMax));

        randomizedQuestions.set(currentQuestionIndex -1, replacement);

        eliminatedChoices.clear();

        System.out.println("Question swapped!");
    }

    public void neuralPrompt(Question question){
        String hint = question.getHint();

        System.out.println("Neural Prompt: "+hint);
    }

    public void parallelProcessing(){
        System.out.println("Activated Parallel Processing!");
        twoGuesses = true;
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
