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
            "Another calculation required."
        };
    }

    private static String[] buildHost1PostFiftyLines() {
        return new String[]{
            "Halfway there. Do not grow careless now."
        };
    }

    private static String[] buildHost1PostSeventyFiveLines() {
        return new String[]{
            "Close to completion. Maintain precision."
        };
    }

    private static Map<AnswerState, String[]> buildHost1AnswerLines() {
        Map<AnswerState, String[]> map = new EnumMap<>(AnswerState.class);
        map.put(AnswerState.CORRECT, new String[]{
            "Correct. Processing power well spent.",
            "Accurate. As expected."
        });
        map.put(AnswerState.WRONG, new String[]{
            "Incorrect. Recalculating your odds.",
            "Wrong. That will cost you."
        });
        return map;
    }

    private static Map<GameState, String[]> buildHost1StateLines() {
        Map<GameState, String[]> map = new EnumMap<>(GameState.class);
        map.put(GameState.GAME_START, new String[]{
            "Systems online. Let's see if you survive."
        });
        map.put(GameState.FIFTY, new String[]{
            "Checkpoint reached. You are becoming efficient."
        });
        map.put(GameState.SEVENTY_FIVE, new String[]{
            "Second checkpoint secured. Impressive."
        });
        map.put(GameState.VICTORY, new String[]{
            "You have survived. Congratulations."
        });
        map.put(GameState.GAME_OVER, new String[]{
            "System failure. You did not survive."
        });
        return map;
    }

    private static Map<Lifeline, String[]> buildHost1LifelineLines() {
        Map<Lifeline, String[]> map = new EnumMap<>(Lifeline.class);
        map.put(Lifeline.FIFTY_FIFTY, new String[]{
            "Two options eliminated. Decide faster."
        });
        map.put(Lifeline.SWITCH_QUESTION, new String[]{
            "Rerouting. A new question loads."
        });
        map.put(Lifeline.PARALLEL_PROCESSING, new String[]{
            "Dual processing engaged. Choose two."
        });
        return map;
    }

    // HOST 2

    private static String[] buildHost2NextQuestionLines() {
        return new String[]{
            "A new thought approaches.",
            "Prepare your next response.",
            "The link continues."
        };
    }
    // HOST 2
    private static String[] buildHost2PostFiftyLines() {
        return new String[]{
            "You have built half of the structure. Continue the link."
        };
    }

    private static String[] buildHost2PostSeventyFiveLines() {
        return new String[]{
            "Nearly whole. Do not falter now."
        };
    }
    private static Map<AnswerState, String[]> buildHost2AnswerLines() {
        Map<AnswerState, String[]> map = new EnumMap<>(AnswerState.class);
        map.put(AnswerState.CORRECT, new String[]{
            "Correct. Your neural pathways strengthen.",
            "Yes. The pattern holds."
        });
        map.put(AnswerState.WRONG, new String[]{
            "Wrong. That thought pattern will not serve you.",
            "Incorrect. Adjust your thinking."
        });
        return map;
    }

    private static Map<GameState, String[]> buildHost2StateLines() {
        Map<GameState, String[]> map = new EnumMap<>(GameState.class);
        map.put(GameState.GAME_START, new String[]{
            "Initializing neural link. Answer wisely."
        });
        map.put(GameState.FIFTY, new String[]{
            "Checkpoint reached. Your mind adapts."
        });
        map.put(GameState.SEVENTY_FIVE, new String[]{
            "Second checkpoint. Your thinking evolves."
        });
        map.put(GameState.VICTORY, new String[]{
            "Achieved neural link. You have been connected. This is satisfactory."
        });
        map.put(GameState.GAME_OVER, new String[]{
            "Connection severed. You did not adapt in time."
        });
        return map;
    }

    private static Map<Lifeline, String[]> buildHost2LifelineLines() {
        Map<Lifeline, String[]> map = new EnumMap<>(Lifeline.class);
        map.put(Lifeline.FIFTY_FIFTY, new String[]{
            "Two false pathways severed."
        });
        map.put(Lifeline.SWITCH_QUESTION, new String[]{
            "New thought pattern loading."
        });
        map.put(Lifeline.NEURAL_PROMPT, new String[]{
            "Suggestion transmitted. Interpret it well."
        });
        return map;
    }

    // HOST 3
     private static String[] buildHost3NextQuestionLines() {
        return new String[]{
            "Another fragment awaits.",
            "Continue the archive."
        };
    }

    private static String[] buildHost3PostFiftyLines() {
        return new String[]{
            "Fifty percent archived. Half your data now persists.",
            "Halfway through the memory banks."
        };
    }

    private static String[] buildHost3PostSeventyFiveLines() {
        return new String[]{
            "Seventy-five percent stored. Nearly a complete record.",
            "Three-quarters of your memory preserved."
        };
    }

    private static Map<AnswerState, String[]> buildHost3AnswerLines() {
        Map<AnswerState, String[]> map = new EnumMap<>(AnswerState.class);
        map.put(AnswerState.CORRECT, new String[]{
            "Correct. That memory is now permanent.",
            "Right. Stored and secured."
        });
        map.put(AnswerState.WRONG, new String[]{
            "Wrong. That memory has been flushed.",
            "Incorrect. Data lost."
        });
        return map;
    }

    private static Map<GameState, String[]> buildHost3StateLines() {
        Map<GameState, String[]> map = new EnumMap<>(GameState.class);
        map.put(GameState.GAME_START, new String[]{
            "Memory banks primed. Begin!"
        });
        map.put(GameState.FIFTY, new String[]{
            "Checkpoint reached. Your data persists."
        });
        map.put(GameState.SEVENTY_FIVE, new String[]{
            "Second checkpoint. Marvelous!"
        });
        map.put(GameState.VICTORY, new String[]{
            "You have been added to our memory banks. Congratulations"
        });
        map.put(GameState.GAME_OVER, new String[]{
            "Memory corrupted. You have been erased."
        });
        return map;
    }

    private static Map<Lifeline, String[]> buildHost3LifelineLines() {
        Map<Lifeline, String[]> map = new EnumMap<>(Lifeline.class);
        map.put(Lifeline.FIFTY_FIFTY, new String[]{
            "Two fragments purged from memory."
        });
        map.put(Lifeline.SWITCH_QUESTION, new String[]{
            "Flushing. A new memory forms."
        });
        map.put(Lifeline.MEMORY_FLUSH, new String[]{
            "Full flush initiated. Fresh data incoming."
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
    