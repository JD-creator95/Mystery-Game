package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<Clue> clues;
    private List<Suspect> questionedSuspects;
    private int currentScene;
    private CaseDifficulty difficulty;
    
    public GameState() {
        this.clues = new ArrayList<>();
        this.questionedSuspects = new ArrayList<>();
        this.currentScene = 0;
        this.difficulty = CaseDifficulty.MEDIUM;
    }
    
    public List<Clue> getClues() {
        return clues;
    }
    
    public void addClue(Clue clue) {
        if (!clues.contains(clue)) {
            clues.add(clue);
        }
    }
    
    public boolean hasClue(Clue clue) {
        return clues.contains(clue);
    }
    
    public List<Suspect> getQuestionedSuspects() {
        return questionedSuspects;
    }
    
    public void addQuestionedSuspect(Suspect suspect) {
        if (!questionedSuspects.contains(suspect)) {
            questionedSuspects.add(suspect);
        }
    }
    
    public boolean hasQuestionedSuspect(Suspect suspect) {
        return questionedSuspects.contains(suspect);
    }
    
    public int getCurrentScene() {
        return currentScene;
    }
    
    public void setCurrentScene(int scene) {
        this.currentScene = scene;
    }
    
    public CaseDifficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(CaseDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
