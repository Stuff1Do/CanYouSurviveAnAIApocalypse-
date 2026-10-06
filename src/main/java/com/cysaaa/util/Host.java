package com.cysaaa.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public enum Host {
    HOST_1("/images/host1.png", "/images/host1_gameplay.png", Lifeline.PARALLEL_PROCESSING,
        buildHost1AnswerLines(), buildHost1StateLines(), buildHost1LifelineLines(), buildHost1NextQuestionLines(),
        buildHost1PostFiftyLines(), buildHost1PostSeventyFiveLines()),
    HOST_2("/images/host2.png", "/images/host2_gameplay.png", Lifeline.NEURAL_PROMPT,
        buildHost2AnswerLines(), buildHost2StateLines(), buildHost2LifelineLines(), buildHost2NextQuestionLines(),
        buildHost2PostFiftyLines(), buildHost2PostSeventyFiveLines()),
    HOST_3("/images/host3.png", "/images/host3_gameplay.png", Lifeline.MEMORY_FLUSH,
        buildHost3AnswerLines(), buildHost3StateLines(), buildHost3LifelineLines(), buildHost3NextQuestionLines(),
        buildHost3PostFiftyLines(), buildHost3PostSeventyFiveLines());

    private final String imagePath;
    private final String gameplayImagePath;
    private final Lifeline specialLifeline;
    private final Map<AnswerState, String[]> answerLines;
    private final Map<GameState, String[]> stateLines;
    private final Map<Lifeline, String[]> lifelineLines;
    private final String[] nextQuestionLines;
    private final String[] postFiftyLines;
    private final String[] postSeventyFiveLines;
    private static final Random random = new Random();

    Host(String imagePath, String gameplayImagePath, Lifeline specialLifeline,
         Map<AnswerState, String[]> answerLines, Map<GameState, String[]> stateLines,
         Map<Lifeline, String[]> lifelineLines, String[] nextQuestionLines,
         String[] postFiftyLines, String[] postSeventyFiveLines) {
        this.imagePath = imagePath;
        this.gameplayImagePath = gameplayImagePath;
        this.specialLifeline = specialLifeline;
        this.answerLines = answerLines;
        this.stateLines = stateLines;
        this.lifelineLines = lifelineLines;
        this.nextQuestionLines = nextQuestionLines;
        this.postFiftyLines = postFiftyLines;
        this.postSeventyFiveLines = postSeventyFiveLines;
    }
    

    // TODO: more lines for each state
    //TODO: on load of different questions

    // HOST 1

    private static String[] buildHost1NextQuestionLines() {
        return new String[]{
            "The next assessment is ready. Proceed with care."
        };
    }

    private static String[] buildHost1PostFiftyLines() {
        return new String[]{
            "You have reached the halfway mark. Maintain your discipline."
        };
    }

    private static String[] buildHost1PostSeventyFiveLines() {
        return new String[]{
            "Completion is near. One lapse now could undo your progress."
        };
    }

    private static Map<AnswerState, String[]> buildHost1AnswerLines() {
        Map<AnswerState, String[]> map = new EnumMap<>(AnswerState.class);
        map.put(AnswerState.CORRECT, new String[]{
            "Correct. Your reasoning remains sound.",
            "Accurate. Continue at this level of focus."
        });
        map.put(AnswerState.WRONG, new String[]{
            "Incorrect. Reassess the evidence before the next decision.",
            "Wrong. The consequences have been recorded."
        });
        return map;
    }

    private static Map<GameState, String[]> buildHost1StateLines() {
        Map<GameState, String[]> map = new EnumMap<>(GameState.class);
        map.put(GameState.GAME_START, new String[]{
            "Systems online. Let's see if you survive."
        });
        map.put(GameState.FIFTY, new String[]{
            "Checkpoint reached. Your performance is improving."
        });
        map.put(GameState.SEVENTY_FIVE, new String[]{
            "Second checkpoint secured. Your consistency is notable."
        });
        map.put(GameState.VICTORY, new String[]{
            "You have survived the assessment. Congratulations."
        });
        map.put(GameState.GAME_OVER, new String[]{
            "Assessment concluded. You did not survive."
        });
        return map;
    }

    private static Map<Lifeline, String[]> buildHost1LifelineLines() {
        Map<Lifeline, String[]> map = new EnumMap<>(Lifeline.class);
        map.put(Lifeline.FIFTY_FIFTY, new String[]{
            "Two options eliminated. Make the remaining decision carefully."
        });
        map.put(Lifeline.SWITCH_QUESTION, new String[]{
            "The question has been replaced. Prepare for a new assessment."
        });
        map.put(Lifeline.PARALLEL_PROCESSING, new String[]{
            "Parallel processing engaged. Select two answers."
        });
        return map;
    }

    // HOST 2

    private static String[] buildHost2NextQuestionLines() {
        return new String[]{
            "A fresh brain-teaser is incoming. Try not to scare it away.",
            "New question loading. Give your neurons a quick pep talk.",
            "The neural link continues. So far, nobody has unplugged it."
        };
    }
    // HOST 2
    private static String[] buildHost2PostFiftyLines() {
        return new String[]{
            "Halfway there! Your brain is doing great work, despite the pressure."
        };
    }

    private static String[] buildHost2PostSeventyFiveLines() {
        return new String[]{
            "Seventy-five percent complete! The finish line is waving at you."
        };
    }
    private static Map<AnswerState, String[]> buildHost2AnswerLines() {
        Map<AnswerState, String[]> map = new EnumMap<>(AnswerState.class);
        map.put(AnswerState.CORRECT, new String[]{
            "Correct! Your neurons just did a tiny victory dance.",
            "Yes! The pattern holds, and your brain gets bragging rights."
        });
        map.put(AnswerState.WRONG, new String[]{
            "Wrong! That thought took a scenic route straight into trouble.",
            "Incorrect. No worries; even genius brains need a reboot sometimes."
        });
        return map;
    }

    private static Map<GameState, String[]> buildHost2StateLines() {
        Map<GameState, String[]> map = new EnumMap<>(GameState.class);
        map.put(GameState.GAME_START, new String[]{
            "Neural link initializing. Please keep all wild guesses inside the vehicle."
        });
        map.put(GameState.FIFTY, new String[]{
            "Checkpoint reached. Your mind is adapting nicely. Gold star for the neurons."
        });
        map.put(GameState.SEVENTY_FIVE, new String[]{
            "Second checkpoint! Your thinking evolves, and I am officially impressed."
        });
        map.put(GameState.VICTORY, new String[]{
            "Neural link achieved! You survived with your brain mostly where you left it."
        });
        map.put(GameState.GAME_OVER, new String[]{
            "Connection severed. The apocalypse wins this round; rude, but fair."
        });
        return map;
    }

    private static Map<Lifeline, String[]> buildHost2LifelineLines() {
        Map<Lifeline, String[]> map = new EnumMap<>(Lifeline.class);
        map.put(Lifeline.FIFTY_FIFTY, new String[]{
            "Two false pathways deleted. Fewer wrong turns, more brainy business."
        });
        map.put(Lifeline.SWITCH_QUESTION, new String[]{
            "New thought pattern loading. This one promises fewer existential questions."
        });
        map.put(Lifeline.NEURAL_PROMPT, new String[]{
            "Hint transmitted. Use it wisely; my neurons have a reputation to protect."
        });
        return map;
    }

    // HOST 3
     private static String[] buildHost3NextQuestionLines() {
        return new String[]{
             "Another fragment awaits. Let us see whether wisdom or chaos answers first.",
             "The archive continues. Keep your focus sharp and your panic manageable."
        };
    }

    private static String[] buildHost3PostFiftyLines() {
        return new String[]{
            "Fifty percent archived. Half your data persists; the other half is still taking notes.",
            "Halfway through the memory banks. A respectable distance from total disaster."
        };
    }

    private static String[] buildHost3PostSeventyFiveLines() {
        return new String[]{
            "Seventy-five percent stored. Nearly complete, so now is a poor time to become dramatic.",
            "Three-quarters of your memory preserved. Protect the final quarter at all costs."
        };
    }

    private static Map<AnswerState, String[]> buildHost3AnswerLines() {
        Map<AnswerState, String[]> map = new EnumMap<>(AnswerState.class);
        map.put(AnswerState.CORRECT, new String[]{
            "Correct. That memory is secured, and yes, you may feel proud.",
            "Right. Stored and secured. Even I am a little impressed."
        });
        map.put(AnswerState.WRONG, new String[]{
            "Wrong. That memory has been flushed. The archive remains unforgiving.",
            "Incorrect. Data lost, but the lesson may still be recoverable."
        });
        return map;
    }

    private static Map<GameState, String[]> buildHost3StateLines() {
        Map<GameState, String[]> map = new EnumMap<>(GameState.class);
        map.put(GameState.GAME_START, new String[]{
            "Memory banks primed. Let us begin before the apocalypse gets impatient."
        });
        map.put(GameState.FIFTY, new String[]{
            "Checkpoint reached. Your data persists, and so does the pressure."
        });
        map.put(GameState.SEVENTY_FIVE, new String[]{
            "Second checkpoint secured. Marvelous work; try not to celebrate too loudly."
        });
        map.put(GameState.VICTORY, new String[]{
            "You have been added to our memory banks. Congratulations; that is a very exclusive filing cabinet."
        });
        map.put(GameState.GAME_OVER, new String[]{
            "Memory corrupted. You have been erased, but at least the archive will remember the attempt."
        });
        return map;
    }

    private static Map<Lifeline, String[]> buildHost3LifelineLines() {
        Map<Lifeline, String[]> map = new EnumMap<>(Lifeline.class);
        map.put(Lifeline.FIFTY_FIFTY, new String[]{
            "Two fragments purged from memory. A tidy archive is a happy archive."
        });
        map.put(Lifeline.SWITCH_QUESTION, new String[]{
            "Flushing. A new memory forms; let us make this one worth keeping."
        });
        map.put(Lifeline.MEMORY_FLUSH, new String[]{
            "Full flush initiated. Fresh data incoming, along with a small chance of regret."
        });
        return map;
    }

    public String getImagePath() { return imagePath; }
    public String getGameplayImagePath() { return gameplayImagePath; }
    public Lifeline getSpecialLifeline() { return specialLifeline; }

    public String getLine(AnswerState state) {
        String[] lines = answerLines.get(state);
        if (lines == null || lines.length == 0) return "";
        return lines[random.nextInt(lines.length)];
    }

    public String getLine(GameState state) {
        String[] lines = stateLines.get(state);
        if (lines == null || lines.length == 0) return "";
        return lines[random.nextInt(lines.length)];
    }

    public String getLine(Lifeline lifeline) {
        String[] lines = lifelineLines.get(lifeline);
        if (lines == null || lines.length == 0) return "";
        return lines[random.nextInt(lines.length)];
    }
    public String getNextQuestionLine() {
        if (nextQuestionLines == null || nextQuestionLines.length == 0) return "";
        return nextQuestionLines[random.nextInt(nextQuestionLines.length)];
    }
    public String getPostFiftyLine() {
        if (postFiftyLines == null || postFiftyLines.length == 0) return "";
        return postFiftyLines[random.nextInt(postFiftyLines.length)];
    }

    public String getPostSeventyFiveLine() {
        if (postSeventyFiveLines == null || postSeventyFiveLines.length == 0) return "";
        return postSeventyFiveLines[random.nextInt(postSeventyFiveLines.length)];
    }

}
    