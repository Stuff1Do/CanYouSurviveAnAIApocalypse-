package com.cysaaa.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class QuestionLoader {

    
    public static List<Question> loadQuestions(String resourcePath) {
        List<Question> questions = new ArrayList<>();

        try (InputStream is = QuestionLoader.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            if (is == null) {
                throw new RuntimeException("Could not find resource: " + resourcePath);
            }

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header row
                }
                if (line.isBlank()) continue;

                String[] fields = line.split("\t"); 

                //if field has less than needed information, skip
                if (fields.length < 7) {
                    System.out.println("Skipping error row: " + line);
                    continue;
                }

                //get question, choices, and correct answer into variables
                String type = fields[0].trim();
                String questionText = fields[1].trim();
                String[] choices = {
                    fields[2].trim(),
                    fields[3].trim(),
                    fields[4].trim(),
                    fields[5].trim()
                };
                String correctAnswerText = fields[6].trim();


                // get correct choice index
                int correctIndex = -1;
                for (int i = 0; i < choices.length; i++) {
                    if (choices[i].equals(correctAnswerText)) {
                        correctIndex = i;
                        break;
                    }
                }

                //if not found, print out warning
                if (correctIndex == -1) {
                    System.out.println("correct answer didn't match any choice for: " + questionText);
                    continue;
                }

                //make new question
                Question question = new Question(type, questionText, choices, correctIndex);
                questions.add(question);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    public static List <Question> randomizeQuestions(List<Question> questions){
        if (questions == null || questions.isEmpty()) {
            return new ArrayList<>();
        }

        Random random = new Random();
        List<Question> randomizedQuestions = new ArrayList<>();
        Set<Integer> indexUsed = new HashSet<>();
        int n = 15;
        int questionIndex;
        for(int i = 0; i < n; i++){
            questionIndex = random.nextInt(questions.size());
            while(indexUsed.contains(questionIndex)){
                questionIndex = random.nextInt(questions.size());
            }
            randomizedQuestions.add(questions.get(questionIndex));
            indexUsed.add(questionIndex);
            
        }
        return randomizedQuestions;

    }
}
