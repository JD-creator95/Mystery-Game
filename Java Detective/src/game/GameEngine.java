package game;

import data.DataLoader;
import data.DatabaseManager;
import model.Clue;
import model.Difficulty;
import model.Suspect;

import java.util.*;

public class GameEngine {
    private List<Clue> clues;
    private List<Suspect> suspects;
    private String story;
    private final String correctSuspect = "Bob"; // Hardcoded for now
    private DatabaseManager db;
    private int playerId;

public GameEngine(String username, Difficulty difficulty) {
    db = new DatabaseManager();
    playerId = db.createPlayer(username, difficulty);
    clues = DataLoader.loadClues("resources/clues.txt");
    suspects = DataLoader.loadSuspects("resources/suspects.txt");
    story = DataLoader.loadStory("resources/case1.txt");
    }
    

    public String getStory() {
        return story;
    }

    public String getAllClues() {
        StringBuilder sb = new StringBuilder("Clues:\n");
        for (Clue clue : clues) {
            sb.append("- ").append(clue.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public String questionSuspects() {
        StringBuilder sb = new StringBuilder("Suspect Statements:\n");
        for (Suspect suspect : suspects) {
            boolean previously = db.wasSuspectQuestioned(playerId, suspect.getName());
            if (!previously) {
                db.updateQuestionedStatus(playerId, suspect.getName(), true);
                sb.append(suspect.getName()).append(": ").append(suspect.getAlibi()).append("\n");
            } else {
                sb.append(suspect.getName()).append(": Already questioned.\n");
            }
        }
        return sb.toString();
    }

public String makeAccusation(String name) {
    if (name == null || name.isBlank()) return "You didn't accuse anyone.";
    boolean isCorrect = name.equalsIgnoreCase(correctSuspect);
    db.saveAccusation(playerId, name, isCorrect);
    db.updateLastCasePlayed(playerId, "Case #1");

    return isCorrect
            ? "Correct! " + name + " was the murderer. Case closed!"
            : "Wrong! " + name + " was innocent. Better luck next time.";
    }
}



