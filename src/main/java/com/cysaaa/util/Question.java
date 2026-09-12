package com.cysaaa.util;

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

    public Question(){

    }

    public Question(String type, String questionText, String[] choices, int correctIndex) {
        this.type = type;
        this.questionText = questionText;
        this.choices = choices;
        this.correctIndex = correctIndex;
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