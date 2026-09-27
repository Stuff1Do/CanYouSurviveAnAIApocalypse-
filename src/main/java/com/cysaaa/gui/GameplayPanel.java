package com.cysaaa.gui;

import com.cysaaa.util.Host;
import com.cysaaa.util.GameState;
import com.cysaaa.util.AnswerState;
import com.cysaaa.util.Question;
import com.cysaaa.util.QuestionLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<Question> questions = new ArrayList<>();
    private Question currentQuestion;

    

    public GameplayPanel(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/gameplay.png"); 

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        setLayout(percentLayout);

        // --- Question type banner ---
        typeLabel = new JLabel("TYPE: REMEMBER", SwingConstants.CENTER);
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                syncWithState();
            }
        });
        syncWithState();
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

    private void onLifelineClicked(String lifelineKey, LifelineIconPanel button) {
        if (button.isUsed()) return; // already used, ignore clicks
        button.setUsed(true);
        StateManager state = StateManager.getInstance();
        if ("TRACE_ELIMINATION".equals(lifelineKey)) {
            state.useTraceElimination();
        } else if ("SYSTEM_REROUTE".equals(lifelineKey)) {
            state.useSystemReroute();
            loadNextQuestion();
        } else if ("SPECIAL".equals(lifelineKey)) {
            state.useSpecialLifeline();
            dialogueLabel.setText("<html>Neural Prompt: " + currentQuestion.getHint() + "</html>");
        }
        syncWithState();
        repaint();
    }

    private void onAnswerSelected(int index) {
        if (currentQuestion == null) return;
        StateManager state = StateManager.getInstance();
        int answeredQuestion = state.getCurrentQuestionNumber();
        if (answeredQuestion >= 8 && answeredQuestion < 15) {
            state.updateCheckpoint(answeredQuestion);
        }
        if (!currentQuestion.isCorrect(index)) {
            state.setLastAnswer(AnswerState.WRONG);
            state.setScreenState(GameState.GAME_OVER);
            cardLayout.show(mainPanel, EndingPanel.cardForCheckpoint(state.getHighestCheckpoint()));
            return;
        }

        state.setLastAnswer(AnswerState.CORRECT);
        state.updateCheckpoint(answeredQuestion);
        if (answeredQuestion >= 15) {
            state.setScreenState(GameState.VICTORY);
            cardLayout.show(mainPanel, "ENDING_100");
            return;
        }
        state.nextQuestion();
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

        traceEliminationButton.setUsed(state.isTraceEliminationUsed());
        systemRerouteButton.setUsed(state.isSystemRerouteUsed());
        specialLifelineButton.setUsed(state.isSpecialLifelineUsed());

        if (questions.isEmpty()) {
            questions = QuestionLoader.randomizeQuestions(
                QuestionLoader.loadQuestions("/data/questions.csv"));
        }
        int questionIndex = state.getCurrentQuestionNumber() - 1;
        if (questionIndex >= 0 && questionIndex < questions.size()) {
            currentQuestion = questions.get(questionIndex);
            String dialogue = host == null ? "Prepare to evolve." :
                "Host online. Analyze the next threat.";
            setQuestion(currentQuestion.getType(), currentQuestion.getQuestionText(),
                currentQuestion.getChoices(), dialogue);
        }
    }

    private void loadNextQuestion() {
        if (questions.isEmpty()) return;
        Collections.rotate(questions.subList(StateManager.getInstance().getCurrentQuestionNumber() - 1,
            questions.size()), -1);
        syncWithState();
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
}