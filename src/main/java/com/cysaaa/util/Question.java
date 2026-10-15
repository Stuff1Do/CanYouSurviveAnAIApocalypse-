package com.cysaaa.util;
import java.util.Random;

/**
 * Question
 *
 * question with type, problem, and choices  
 */
public class Question {

    private  String type;         
    private  String questionText;
    private  String[] choices;     // exactly 4
    private  int correctIndex;      
    private  String hint;

    public Question(){

    }

    public Question(String type, String questionText, String[] choices, int correctIndex, String hint) {
        this.type = type;
        this.questionText = questionText;
        this.choices = choices;
        this.correctIndex = correctIndex;
        this.hint = hint;
    }

    

    public String getType() {
        return type;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getChoices() {
        return choices;
    }

    public String getCorrectAnswerText() {
        return choices[correctIndex];
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public boolean isCorrect(int chosenIndex) {
        return chosenIndex == correctIndex;
    }

    public String getHint(){
        return hint;
    }

    public void shuffleChoices(Random random) {

        for (int i = choices.length - 1; i > 0; i--) {

            int j = random.nextInt(i + 1);   

            String temp = choices[i];
            choices[i] = choices[j];
            choices[j] = temp;

            if (correctIndex == i) {
                correctIndex = j;
            } else if (correctIndex == j) {
                correctIndex = i;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(type).append("] ").append(questionText).append("\n");
        char label = 'A';
        for (String choice : choices) {
            sb.append(label++).append(". ").append(choice).append("\n");
        }
        return sb.toString();
    }
}