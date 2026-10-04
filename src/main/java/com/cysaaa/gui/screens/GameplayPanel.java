package com.cysaaa.gui.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
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
    private JLabel questionTextLabel; // shows question + choices

    // Host speech bubble
    private JLabel dialogueLabel;
    private DialogueUtil dialogue;

    // Host image
    private ImagePanel hostImage;

    // Answer buttons
    private ImagePanel answerA, answerB, answerC, answerD;

    // Answer button image paths, indexed A-D (0-3)
    // TODO: drop the correct/wrong assets into /buttons/ (filenames below are placeholders)
    private static final String[] ANSWER_IMAGE_PATHS = {
        "/buttons/answer_a.png", "/buttons/answer_b.png", "/buttons/answer_c.png", "/buttons/answer_d.png"
    };
    private static final String[] ANSWER_HOVER_IMAGE_PATHS = {
        "/buttons/answer_a_hover.png", "/buttons/answer_b_hover.png", "/buttons/answer_c_hover.png", "/buttons/answer_d_hover.png"
    };
    private static final String[] ANSWER_CORRECT_IMAGE_PATHS = {
        "/buttons/answer_a_correct.png", "/buttons/answer_b_correct.png", "/buttons/answer_c_correct.png", "/buttons/answer_d_correct.png"
    };
    private static final String[] ANSWER_WRONG_IMAGE_PATHS = {
        "/buttons/answer_a_wrong.png", "/buttons/answer_b_wrong.png", "/buttons/answer_c_wrong.png", "/buttons/answer_d_wrong.png"
    };

    // Lifeline buttons
    private LifelineIconPanel traceEliminationButton; // 50:50
    private LifelineIconPanel systemRerouteButton;     // Switch the Question
    private LifelineIconPanel specialLifelineButton;   // host-exclusive

    List<Question> questionList;
    List<Question> randomizedQuestions;
    List<Question> sortedRandomizedQuestions;

    // --- Per-question lifeline effect state ---
    private final List<Integer> eliminatedIndices = new ArrayList<>();  // 50:50 result
    private final List<Integer> parallelSelections = new ArrayList<>(); // Parallel Processing picks
    private boolean parallelProcessingActive = false;

    private final Random random = new Random();
    private boolean answerLock = false;


    public GameplayPanel(JPanel mainPanel, CardLayout cardLayout) {
        super("/backgrounds/gameplay.png");

        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        percentLayout = new PercentLayout();
        setLayout(percentLayout);

        buildUIComponents(); // build everything once, then load questions later

        dialogue = new DialogueUtil(dialogueLabel, 30, 420, "/fonts/Exo2-SemiBold.ttf", 18f);
        dialogue.enableAutoFit(24, 12); // max, min font size

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                
                syncWithState();
                loadCurrentQuestion();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // reset while off-screen so the red/green result and old dialogue don't flash on return
                resetAnswerButtonImages();
                dialogue.clear();
                questionTextLabel.setText(""); // new question is set in componentShown
            }
        });

        
    }

    public void loadCurrentQuestion() {
        answerLock = false;
        int currentQuestionIndex = StateManager.getInstance().getCurrentQuestionNumber();

        if (sortedRandomizedQuestions == null || currentQuestionIndex > sortedRandomizedQuestions.size()) {
            return; // no more questions, game should have already transitioned screens
        }

        Question question = sortedRandomizedQuestions.get(currentQuestionIndex - 1);
        System.out.println("QUESTION #" + currentQuestionIndex + ": ");
        System.out.print("(DEV)Correct Answer: ");
        System.out.println(question.getCorrectIndex() + 1);
        setQuestion(question.getType(), question.getQuestionText(), question.getChoices());

        dialogueOnQuestionLoad();

        resetPerQuestionState();
    }

    public void dialogueOnQuestionLoad(){
        
        Host host = StateManager.getInstance().getCurrentHost();
        StateManager state = StateManager.getInstance();

        if(state.isLifelineUsedState() != null){
            return;
        }

        int currentQuestionIndex = state.getCurrentQuestionNumber();
        if (host != null) {
            //TODO: do more if statements for each question
            //this is also for system reroute so it doesnt get overwritten by loadCurrentQuestion
            //will eventually differentiate playing and game start, just need to make dialogue for both
            if(currentQuestionIndex == 1){
                dialogue.type(host.getLine(GameState.GAME_START));
            }else if(currentQuestionIndex == 8){
                dialogue.type(host.getPostFiftyLine());
            }else if(currentQuestionIndex == 11){
                dialogue.type(host.getPostSeventyFiveLine());
            }else{
                dialogue.type(host.getNextQuestionLine());
            }
            
        }
    }

    // Clears effects that only apply to a single question (50:50 eliminations,
    // in-progress Parallel Processing selections). Does NOT touch usedLifelines —
    // that's tracked for the whole playthrough in StateManager.
    private void resetPerQuestionState() {
        eliminatedIndices.clear();
        parallelSelections.clear();
        parallelProcessingActive = false;
        for (ImagePanel btn : getAnswerButtons()) {
            btn.setVisible(true);
            btn.setSelected(false);
        }
        resetAnswerButtonImages();
    }

    // Restores default look after a correct/wrong reveal
    private void resetAnswerButtonImages() {
        ImagePanel[] buttons = getAnswerButtons();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setImage(ANSWER_IMAGE_PATHS[i]);
            buttons[i].setHoverImage(ANSWER_HOVER_IMAGE_PATHS[i]);
        }
    }

    // Turns the clicked answer button green (correct) or red (wrong).
    // Hover image is cleared so hovering doesn't hide the result.
    private void showAnswerResult(int index, boolean correct) {
        ImagePanel button = getAnswerButtons()[index];
        button.setHoverImage(null);
        button.setImage(correct ? ANSWER_CORRECT_IMAGE_PATHS[index] : ANSWER_WRONG_IMAGE_PATHS[index]);
    }

    private ImagePanel[] getAnswerButtons() {
        return new ImagePanel[] { answerA, answerB, answerC, answerD };
    }

    private Question getCurrentQuestion() {
        int idx = StateManager.getInstance().getCurrentQuestionNumber() - 1;
        return sortedRandomizedQuestions.get(idx);
    }

    private void wireAnswer(ImagePanel button, int index) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onAnswerSelected(index);
            }
        });
    }

    // fixedLifeline: pass the exact Lifeline for baseline buttons, or null for
    // the special button, whose Lifeline is resolved at click-time from the host.
    private void wireLifeline(LifelineIconPanel button, Lifeline fixedLifeline) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Lifeline lifeline = fixedLifeline;
                if (lifeline == null) {
                    Host host = StateManager.getInstance().getCurrentHost();
                    if (host == null) return;
                    lifeline = host.getSpecialLifeline();
                }
                onLifelineClicked(lifeline, button);
            }
        });
    }

    private void onLifelineClicked(Lifeline lifeline, LifelineIconPanel button) {
        if (answerLock) return;
        if (button.isUsed()) return;
        if (StateManager.getInstance().isLifelineUsed(lifeline)) return;

        if (lifeline == Lifeline.PARALLEL_PROCESSING) {
            showParallelProcessingConfirmation(lifeline, button);
            return; // marking used + applying effect happens inside the OK callback
        }
        
        Host host = StateManager.getInstance().getCurrentHost();
        if (host != null && lifeline != Lifeline.NEURAL_PROMPT) {
            dialogue.type(host.getLine(lifeline));
        }
        StateManager.getInstance().useLifeline(lifeline);
        switch (lifeline) {
            case FIFTY_FIFTY -> applyFiftyFifty();
            case SWITCH_QUESTION -> applySwitchQuestion();
            case PARALLEL_PROCESSING -> applyParallelProcessing();
            case MEMORY_FLUSH -> applyMemoryFlush();
            case NEURAL_PROMPT -> applyNeuralPrompt();
        }

        button.setUsed(true);
        

        //after using lifeline, reset lifeline sate
        StateManager.getInstance().resetLifelineUsedState();
    }

    private void applyNeuralPrompt(){
        Question question = getCurrentQuestion();
        dialogue.type(question.getHint());
    }

    // --- Lifeline effects ---

    private void applyFiftyFifty() {
        Question question = getCurrentQuestion();
        int correct = question.getCorrectIndex();

        List<Integer> wrongIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correct) wrongIndices.add(i);
        }
        java.util.Collections.shuffle(wrongIndices, random);

        ImagePanel[] buttons = getAnswerButtons();
        for (int i = 0; i < 2; i++) {
            int eliminate = wrongIndices.get(i);
            eliminatedIndices.add(eliminate);
            buttons[eliminate].setVisible(false);
        }
    }

    private void applySwitchQuestion() {
        Question current = getCurrentQuestion();
        int tier = getTier(current.getType());
        replaceCurrentQuestion(q -> getTier(q.getType()) == tier);
    }

    private void applyMemoryFlush() {
        Question current = getCurrentQuestion();
        replaceCurrentQuestion(q -> q.getType().equals(current.getType()));
    }

    private void replaceCurrentQuestion(Predicate<Question> matcher) {
        
        List<Question> candidates = new ArrayList<>();
        for (Question q : questionList) {
            if (!sortedRandomizedQuestions.contains(q) && matcher.test(q)) {
                candidates.add(q);
            }
        }
        if (candidates.isEmpty()) {
            System.out.println("No replacement question available — pool exhausted for this filter.");
            return;
        }
        Question replacement = candidates.get(random.nextInt(candidates.size()));

        int idx = StateManager.getInstance().getCurrentQuestionNumber() - 1;
        sortedRandomizedQuestions.set(idx, replacement);
        
        loadCurrentQuestion(); // will also reset eliminated/parallel state, which is correct here

        if(StateManager.getInstance().isLifelineUsed(Lifeline.FIFTY_FIFTY)){
            applyFiftyFifty();
        }
    }

    private int getTier(String type) {
        if (type.equals("REMEMBER") || type.equals("UNDERSTAND")) return 1;
        if (type.equals("APPLY") || type.equals("ANALYZE") || type.equals("EVALUATE")) return 2;
        if (type.equals("SYNTHESIS")) return 3;
        System.out.println("TIER ERROR: unknown type " + type);
        return -1;
    }

    private void applyParallelProcessing() {
        parallelProcessingActive = true;
        parallelSelections.clear();
        // TODO: visual "pick one more answer" prompt — no dedicated UI element yet.
        System.out.println("Parallel Processing active — pick two answers.");
    }

    private void showParallelProcessingConfirmation(Lifeline lifeline, LifelineIconPanel button) {
        JRootPane root = SwingUtilities.getRootPane(this);
        JLayeredPane layeredPane = root.getLayeredPane();

        ConfirmationOverlay overlay = new ConfirmationOverlay(
            "/popups/parallelProcessingConfirm.png",
            784, 392,
            250, 300, 200, 80,
            () -> { // onOk
                applyParallelProcessing();
                button.setUsed(true);
                StateManager.getInstance().useLifeline(lifeline);
                if(StateManager.getInstance().getCurrentHost() != null){
                    dialogue.type(StateManager.getInstance().getCurrentHost().getLine(lifeline));
                }
            }
        );

        overlay.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();
    }

    // --- Answer handling ---

    private void onAnswerSelected(int index) {
        if (answerLock) return;
        if (parallelProcessingActive) {
            handleParallelSelection(index);
            return;
        }
        evaluateAnswer(index);
    }

    private void handleParallelSelection(int index) {
        if (parallelSelections.contains(index)) return; // ignore re-click of same choice

        parallelSelections.add(index);
        getAnswerButtons()[index].setSelected(true); // highlight persists after mouse leaves

        if (parallelSelections.size() < 2) {
            System.out.println("First pick recorded: " + index + " — pick one more.");
            return;
        }

        // Both picks in — evaluate. Correct if EITHER pick matches (adjust if you want both-correct).
        Question question = getCurrentQuestion();
        boolean correct = parallelSelections.stream().anyMatch(question::isCorrect);

        parallelProcessingActive = false;
        for (ImagePanel btn : getAnswerButtons()) {
            btn.setSelected(false); // clear highlights before moving to next question/screen
        }
        for (int pick : parallelSelections) {
            showAnswerResult(pick, question.isCorrect(pick));
        }
        parallelSelections.clear();

        resolveAnswer(correct);
    }

    private void evaluateAnswer(int index) {
        Question question = getCurrentQuestion();
        boolean correct = question.isCorrect(index);
        showAnswerResult(index, correct);
        resolveAnswer(correct);
    }

    private void resolveAnswer(boolean correct) {
        answerLock = true; // block further answer/lifeline clicks for every outcome
        if (correct) {
            StateManager.getInstance().setLastAnswer(AnswerState.CORRECT);
            StateManager.getInstance().nextQuestion();

            if (StateManager.getInstance().getCurrentQuestionNumber() > sortedRandomizedQuestions.size()) {
                StateManager.getInstance().setScreenState(GameState.VICTORY);
                dialogue.type(StateManager.getInstance().getCurrentHost().getLine(StateManager.getInstance().getScreenState()));
                runAfterDelay(3000, () -> {
                    cardLayout.show(mainPanel, "VICTORY");
                });
            } else {
                dialogue.type(StateManager.getInstance().getCurrentHost().getLine(StateManager.getInstance().getLastAnswer()));
                runAfterDelay(3000, () -> {
                    progressPanel.syncWithState();
                    cardLayout.show(mainPanel, "PROGRESS");
                });
            }

        } else {
            StateManager.getInstance().setLastAnswer(AnswerState.WRONG);
            String screen = checkCheckpoint();
            showEnding(screen);
        }
    }

    // Call this every time GAMEPLAY is about to be shown
    public void syncWithState() {
        StateManager state = StateManager.getInstance();
        Host host = state.getCurrentHost();

        if (host != null) {
            hostImage.setImage(host.getGameplayImagePath());
            specialLifelineButton.setActiveImage(
                host.getSpecialLifeline().getGameplayActiveIconPath(),
                host.getSpecialLifeline().getGameplayDisabledIconPath(),
                host.getSpecialLifeline().getGameplayHoverIconPath()
            );
        }

        traceEliminationButton.setUsed(state.isLifelineUsed(Lifeline.FIFTY_FIFTY));
        systemRerouteButton.setUsed(state.isLifelineUsed(Lifeline.SWITCH_QUESTION));
        if (host != null) {
            specialLifelineButton.setUsed(state.isLifelineUsed(host.getSpecialLifeline()));
        }
    }

    public String checkCheckpoint(){
        StateManager state = StateManager.getInstance();
        int progressIndex = state.getCurrentQuestionNumber() - 1;

        if (progressIndex < 8) {
            state.setScreenState(GameState.GAME_OVER);
            return "GAME_OVER";
        } else if (progressIndex >= 8 && progressIndex < 11) {
            state.setScreenState(GameState.FIFTY);
            return "CHECKPOINT_1";
        } else if (progressIndex >= 11 && progressIndex < 15) {
            state.setScreenState(GameState.SEVENTY_FIVE);
            return "CHECKPOINT_2";
        } else {
            System.out.println("CHECKPOINT ERROR: something has gone wrong idk, debug idiot");
        }
        return null;
    }

    public void showEnding(String screen){
        Host host = StateManager.getInstance().getCurrentHost();
        if(host != null){
            dialogue.type(host.getLine(StateManager.getInstance().getScreenState()));
        }
        runAfterDelay(3000, () -> cardLayout.show(mainPanel, screen));
    }

    public void loadQuestionList(){
        questionList = QuestionLoader.getQuestions();
    }

    public void randomizeQuestions(List<Question> questions){
        randomizedQuestions = QuestionLoader.randomizeQuestions(questions);
    }

    public void sortQuestions(List<Question> questions){
        sortedRandomizedQuestions = QuestionLoader.sortByType(questions);
    }

    public void startNewGame() {
        StateManager.getInstance().reset();
        loadQuestionList();
        randomizeQuestions(questionList);
        sortQuestions(randomizedQuestions);
        loadCurrentQuestion();
    }

    // hostDialogue param removed — dialogue is now set separately via dialogue.type()
    public void setQuestion(String type, String questionText, String[] choices) {
        typeLabel.setText("TYPE: " + type.toUpperCase());
        // question + choices in one label so the choices always sit right under the question
        StringBuilder sb = new StringBuilder("<html>");
        sb.append(questionText);
        sb.append("<div style='margin-top: 20px'>"); // gap between question and choices
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < choices.length; i++) {
            sb.append(letters[i]).append(". ").append(choices[i]).append("<br>");
        }
        sb.append("</div></html>");
        questionTextLabel.setText(sb.toString());
        fitQuestionFont();
    }

    // Font size range for the question box — text uses the largest size that still fits
    private static final int QUESTION_MAX_FONT = 28;
    private static final int QUESTION_MIN_FONT = 12;

    // Shrinks the question label's font from MAX down until the wrapped HTML fits the label height.
    // (if not laid out yet, componentResized will call this again)
    private void fitQuestionFont() {
        LabelFontFitter.fitToHeight(questionTextLabel, QUESTION_MAX_FONT, QUESTION_MIN_FONT);
    }

    private void runAfterDelay(int delayMillis, Runnable action) {
        Timer timer = new Timer(delayMillis, e -> action.run());
        timer.setRepeats(false);
        timer.start();
    }

    private void buildUIComponents() {
        typeLabel = new JLabel("", SwingConstants.CENTER);
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        percentLayout.addPixel(this, typeLabel, 365, 83, 584, 55);

        questionTextLabel = new JLabel();
        questionTextLabel.setForeground(Color.WHITE);
        questionTextLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        questionTextLabel.setVerticalAlignment(SwingConstants.CENTER); // block sits in the vertical middle of the box
        questionTextLabel.setHorizontalAlignment(SwingConstants.LEFT); // lines stay left-aligned
        percentLayout.addPixel(this, questionTextLabel, 213, 297, 946, 432);
        // label size changes with the window, so refit the font whenever it's resized
        questionTextLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fitQuestionFont();
            }
        });

        // font + typewriter now handled by DialogueUtil, constructed after this method returns
        dialogueLabel = new JLabel();
        dialogueLabel.setForeground(Color.WHITE);
        dialogueLabel.setVerticalAlignment(SwingConstants.CENTER);
        dialogueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // position/size in 1920x1080 design pixels: x, y, width, height
        percentLayout.addPixel(this, dialogueLabel, 1312, 88, 513, 176);

        dialogueLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialogue.skipToEnd();
            }
        });


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

        traceEliminationButton = new LifelineIconPanel(
            "/buttons/TraceElButton.png",
            "/buttons/TraceElButtonDisabled.png",
            "/buttons/TraceElButtonHover.png"
        );
        percentLayout.addPixel(this, traceEliminationButton, 1303, 873, 539, 64);
        wireLifeline(traceEliminationButton, Lifeline.FIFTY_FIFTY);

        systemRerouteButton = new LifelineIconPanel(
            "/buttons/SysReButton.png",
            "/buttons/SysReButtonDisabled.png",
            "/buttons/SysReButtonHover.png"
        );
        percentLayout.addPixel(this, systemRerouteButton, 1303, 957, 539, 64);
        wireLifeline(systemRerouteButton, Lifeline.SWITCH_QUESTION);

        specialLifelineButton = new LifelineIconPanel("/buttons/placeholder.png", "/buttons/placeholder.png");
        percentLayout.addPixel(this, specialLifelineButton, 1303, 790, 539, 64);
        wireLifeline(specialLifelineButton, null);
    }
}