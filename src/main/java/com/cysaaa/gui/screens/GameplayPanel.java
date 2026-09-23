package com.cysaaa.gui.screens;

import com.cysaaa.util.Host;
import com.cysaaa.util.Lifeline;
import com.cysaaa.util.Question;
import com.cysaaa.util.StateManager;
import com.cysaaa.util.GameState;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import com.cysaaa.gui.components.*;
import com.cysaaa.util.*;


public class GameplayPanel extends BackgroundPanel {

    private PercentLayout percentLayout;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private ProgressPanel progressPanel;
    public void setProgressPanel(ProgressPanel progressPanel) {
        this.progressPanel = progressPanel;
    }

    // Question type banner
    private JLabel typeLabel;

    // Question box
    private JLabel questionTextLabel;
    private JLabel choicesLabel;

    // Host speech bubble
    private JLabel dialogueLabel;

    // Host image
    private ImagePanel hostImage;

    // Answer buttons
    private ImagePanel answerA, answerB, answerC, answerD;

    // Lifeline buttons
    private LifelineIconPanel traceEliminationButton;
    private LifelineIconPanel systemRerouteButton;
    private LifelineIconPanel specialLifelineButton;

    List<Question> questionList;
    List<Question> randomizedQuestions;

    

    public GameplayPanel(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/gameplay.png"); 

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        setLayout(percentLayout);      


        //load questions & randomize
        loadQuestionList();
        randomizeQuestions(questionList);

        buildUIComponents(); //build everything once, then load questions later

        //on load 
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                syncWithState();
                loadCurrentQuestion();
            }
        });

        
    }

    public void loadCurrentQuestion() {
        int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();

        if (randomizedQuestions == null || currentQuestionIndex > randomizedQuestions.size()) {
            return; // no more questions, game should have already transitioned screens
        }

        Question question = randomizedQuestions.get(currentQuestionIndex - 1);
        setQuestion(question.getType(), question.getQuestionText(), question.getChoices(), "");
    }

    private void wireAnswer(ImagePanel button, int index) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onAnswerSelected(index);
            }
        });
    }

    private void wireLifeline(LifelineIconPanel button, String lifelineKey) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onLifelineClicked(lifelineKey, button);
            }
        });
    }

    // TODO: hook up real lifeline effects (eliminate choices, swap question, host effect)
    private void onLifelineClicked(String lifelineKey, LifelineIconPanel button) {
        if (button.isUsed()) return; // already used, ignore clicks
        System.out.println("Lifeline used: " + lifelineKey);
        button.setUsed(true);
        // e.g. StateManager.getInstance().markLifelineUsed(lifelineKey);
    }

    // TODO: hook up to Question/scoring logic once that exists
    private void onAnswerSelected(int index) {
        int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();
        Question question = randomizedQuestions.get(currentQuestionIndex - 1);

        boolean correct = question.isCorrect(index);

        if (correct) {
            StateManager.getInstance().setLastAnswer(AnswerState.CORRECT);
            StateManager.getInstance().nextQuestion();
        } else {
            StateManager.getInstance().setLastAnswer(AnswerState.WRONG);
            StateManager.getInstance().setScreenState(GameState.GAME_OVER);
        }

        progressPanel.syncWithState();
        cardLayout.show(mainPanel, "PROGRESS");
    }

    // Call this every time GAMEPLAY is about to be shown
    public void syncWithState() {
        StateManager state = StateManager.getInstance();
        Host host = state.getCurrentHost();

        if (host != null) {
            hostImage.setImage(host.getGameplayImagePath());
            specialLifelineButton.setActiveImage(
                host.getSpecialLifeline().getGameplayActiveIconPath(),
                host.getSpecialLifeline().getGameplayDisabledIconPath()
            );
        }

         
        traceEliminationButton.setUsed(state.isLifelineUsed(Lifeline.FIFTY_FIFTY));
        systemRerouteButton.setUsed(state.isLifelineUsed(Lifeline.SWITCH_QUESTION));
        specialLifelineButton.setUsed(state.isLifelineUsed(host.getSpecialLifeline()));
        
    }   

    public void startGame(){
        loadQuestionList();
        randomizeQuestions(questionList);


        while(true){
            int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();
            GameState withdrawState = StateManager.getInstance().getWithdrawState();
            GameState screenState = StateManager.getInstance().getScreenState();

            if (currentQuestionIndex > randomizedQuestions.size() || withdrawState == GameState.WITHDRAW || screenState == GameState.GAME_OVER) {
                break;
            }

            askQuestion(randomizedQuestions.get(currentQuestionIndex - 1));

            // only increment count if the game is still active
            if (StateManager.getInstance().getScreenState() != GameState.GAME_OVER && StateManager.getInstance().getWithdrawState() != GameState.WITHDRAW) {
                StateManager.getInstance().nextQuestion();
            }
        }


       


    }

    public void askQuestion(Question question){


        String questionType = question.getType();
        typeLabel = new JLabel(questionType, SwingConstants.CENTER);
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        percentLayout.addPixel(this, typeLabel, 173, 97, 634, 86);

        // --- Question box ---
        questionTextLabel = new JLabel("<html>Question text goes here.</html>");
        questionTextLabel.setForeground(Color.WHITE);
        questionTextLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        questionTextLabel.setVerticalAlignment(SwingConstants.TOP);
        percentLayout.addPixel(this, questionTextLabel, 213, 273, 976, 150);

        choicesLabel = new JLabel("<html>A. ...<br>B. ...<br>C. ...<br>D. ...</html>");
        choicesLabel.setForeground(Color.WHITE);
        choicesLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        choicesLabel.setVerticalAlignment(SwingConstants.TOP);
        percentLayout.addPixel(this, choicesLabel, 213, 450, 976, 160);

        // --- Host speech bubble ---
        // TODO: Change dialogue label text depending on the host label.
        dialogueLabel = new JLabel("<html>Host dialogue goes here.</html>");
        dialogueLabel.setForeground(Color.WHITE);
        dialogueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dialogueLabel.setVerticalAlignment(SwingConstants.TOP);
        percentLayout.addPixel(this, dialogueLabel, 1332, 119, 495, 237);

        // --- Host image (gameplay-specific per-host asset) ---
        hostImage = new ImagePanel();
        percentLayout.addPixel(this, hostImage, 1351, 260, 436, 507);

        // --- Answer buttons ---
        answerA = new ImagePanel("/buttons/answer_a.png", "/buttons/answer_a_hover.png");
        percentLayout.addPixel(this, answerA, 194, 873, 474, 60);
        wireAnswer(answerA, 0);

        answerB = new ImagePanel("/buttons/answer_b.png", "/buttons/answer_b_hover.png");
        percentLayout.addPixel(this, answerB, 647, 873, 474, 60);
        wireAnswer(answerB, 1);

        answerC = new ImagePanel("/buttons/answer_c.png", "/buttons/answer_c_hover.png");
        percentLayout.addPixel(this, answerC, 194, 963, 474, 60);
        wireAnswer(answerC, 2);

        answerD = new ImagePanel("/buttons/answer_d.png", "/buttons/answer_d_hover.png");
        percentLayout.addPixel(this, answerD, 647, 963, 474, 60);
        wireAnswer(answerD, 3);

        // --- Lifeline buttons ---
        traceEliminationButton = new LifelineIconPanel(
            "/buttons/TraceElButton.png",
            "/buttons/TraceElButtonDisabled.png"
        );
        percentLayout.addPixel(this, traceEliminationButton, 1303, 873, 539, 64);
        wireLifeline(traceEliminationButton, "TRACE_ELIMINATION");

        systemRerouteButton = new LifelineIconPanel(
            "/buttons/SysReButton.png",
            "/buttons/SysReButtonDisabled.png"
        );
        percentLayout.addPixel(this, systemRerouteButton, 1303, 957, 539, 64);
        wireLifeline(systemRerouteButton, "SYSTEM_REROUTE");

        // Special (host-specific)
        specialLifelineButton = new LifelineIconPanel(
            "/buttons/placeholder.png",
            "/buttons/placeholder.png"
        );
        percentLayout.addPixel(this, specialLifelineButton, 1303, 790, 539, 64);
        wireLifeline(specialLifelineButton, "SPECIAL");
    }


    public void loadQuestionList(){
        questionList = QuestionLoader.loadQuestions("/data/questions.csv");
        
    }

    public void randomizeQuestions(List<Question> questions){
        randomizedQuestions = QuestionLoader.randomizeQuestions(questionList);
    }

    
    // Call this once question data exists, to populate the screen
    public void setQuestion(String type, String questionText, String[] choices, String hostDialogue) {
        typeLabel.setText("TYPE: " + type.toUpperCase());
        questionTextLabel.setText("<html>" + questionText + "</html>");

        StringBuilder sb = new StringBuilder("<html>");
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < choices.length; i++) {
            sb.append(letters[i]).append(". ").append(choices[i]).append("<br>");
        }
        sb.append("</html>");
        choicesLabel.setText(sb.toString());

        dialogueLabel.setText("<html>" + hostDialogue + "</html>");
    }

    
    private void buildUIComponents() {
        typeLabel = new JLabel("", SwingConstants.CENTER);
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        percentLayout.addPixel(this, typeLabel, 173, 97, 634, 86);

        questionTextLabel = new JLabel();
        questionTextLabel.setForeground(Color.WHITE);
        questionTextLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        questionTextLabel.setVerticalAlignment(SwingConstants.TOP);
        percentLayout.addPixel(this, questionTextLabel, 213, 273, 976, 150);

        choicesLabel = new JLabel();
        choicesLabel.setForeground(Color.WHITE);
        choicesLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        choicesLabel.setVerticalAlignment(SwingConstants.TOP);
        percentLayout.addPixel(this, choicesLabel, 213, 450, 976, 160);

        dialogueLabel = new JLabel();
        dialogueLabel.setForeground(Color.WHITE);
        dialogueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dialogueLabel.setVerticalAlignment(SwingConstants.TOP);
        percentLayout.addPixel(this, dialogueLabel, 1332, 119, 495, 237);

        hostImage = new ImagePanel();
        percentLayout.addPixel(this, hostImage, 1351, 260, 436, 507);

        answerA = new ImagePanel("/buttons/answer_a.png", "/buttons/answer_a_hover.png");
        percentLayout.addPixel(this, answerA, 194, 873, 474, 60);
        wireAnswer(answerA, 0);

        answerB = new ImagePanel("/buttons/answer_b.png", "/buttons/answer_b_hover.png");
        percentLayout.addPixel(this, answerB, 647, 873, 474, 60);
        wireAnswer(answerB, 1);

        answerC = new ImagePanel("/buttons/answer_c.png", "/buttons/answer_c_hover.png");
        percentLayout.addPixel(this, answerC, 194, 963, 474, 60);
        wireAnswer(answerC, 2);

        answerD = new ImagePanel("/buttons/answer_d.png", "/buttons/answer_d_hover.png");
        percentLayout.addPixel(this, answerD, 647, 963, 474, 60);
        wireAnswer(answerD, 3);

        traceEliminationButton = new LifelineIconPanel("/buttons/TraceElButton.png", "/buttons/TraceElButtonDisabled.png");
        percentLayout.addPixel(this, traceEliminationButton, 1303, 873, 539, 64);
        wireLifeline(traceEliminationButton, "TRACE_ELIMINATION");

        systemRerouteButton = new LifelineIconPanel("/buttons/SysReButton.png", "/buttons/SysReButtonDisabled.png");
        percentLayout.addPixel(this, systemRerouteButton, 1303, 957, 539, 64);
        wireLifeline(systemRerouteButton, "SYSTEM_REROUTE");

        specialLifelineButton = new LifelineIconPanel("/buttons/placeholder.png", "/buttons/placeholder.png");
        percentLayout.addPixel(this, specialLifelineButton, 1303, 790, 539, 64);
        wireLifeline(specialLifelineButton, "SPECIAL");
    }
}