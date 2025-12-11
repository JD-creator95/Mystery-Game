package game;

import gui.GameWindow;
import model.*;
import data.GameData;
import data.InvestigationLogger;
import data.DatabaseManager;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GameController {
    private GameWindow window;
    private GameState gameState;
    private GameData gameData;
    private InvestigationLogger logger;
    private DatabaseManager dbManager;
    private int playerId;
    private int progressId;
    private long gameStartTime;
    private int choiceCounter;
    
    public GameController() {
        this.window = new GameWindow();
        this.gameData = new GameData();
        this.gameState = new GameState();
        this.logger = new InvestigationLogger();
        this.dbManager = new DatabaseManager();
        this.choiceCounter = 0;
        
        // Get or create player profile
        initializePlayer();
    }
    
    private void initializePlayer() {
        String username = JOptionPane.showInputDialog(window, 
            "Enter your detective name:", 
            "Player Profile", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (username == null || username.trim().isEmpty()) {
            username = "Detective";
        }
        
        playerId = dbManager.getPlayerId(username);
        if (playerId == -1) {
            playerId = dbManager.createPlayer(username);
        }
        
        // Show player stats
        DatabaseManager.PlayerStats stats = dbManager.getPlayerStats(playerId);
        if (stats != null && stats.totalGames > 0) {
            JOptionPane.showMessageDialog(window,
                "Welcome back, " + stats.username + "!\n\n" + stats.toString(),
                "Player Statistics",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void startGame() {
        window.setVisible(true);
        
        // Initialize player profile after window is visible
        if (dbManager.isAvailable()) {
            initializePlayer();
        }
        
        showIntroduction();
    }
    
    private void selectDifficulty() {
        CaseDifficulty[] difficulties = CaseDifficulty.values();
        CaseDifficulty selected = (CaseDifficulty) JOptionPane.showInputDialog(
            window,
            "Select case difficulty:",
            "Difficulty Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            difficulties,
            CaseDifficulty.MEDIUM
        );
        
        if (selected != null) {
            gameData.setDifficulty(selected);
            gameState.setDifficulty(selected);
        }
    }
    
    private void showIntroduction() {
        selectDifficulty();
        
        String intro = gameData.getCrimeStory();
        logger.logGameStart();
        
        // Start tracking this case in database (if available)
        if (dbManager.isAvailable()) {
            progressId = dbManager.startCase(playerId, "The Stolen Azure Heart Sapphire");
            gameStartTime = System.currentTimeMillis();
            
            // Save suspects to database
            for (Suspect suspect : gameData.getSuspects()) {
                dbManager.saveSuspect(
                    suspect.getName(),
                    suspect.getRole(),
                    suspect.getStatement(),
                    suspect.isGuilty(),
                    "The Stolen Azure Heart Sapphire"
                );
            }
        }
        
        window.displayText(intro);
        window.clearButtons();
        window.addChoiceButton("Begin Investigation", this::showMainMenu);
    }
    
    private void showMainMenu() {
        StringBuilder text = new StringBuilder();
        text.append("What would you like to do?\n\n");
        text.append("Clues found: ").append(gameState.getClues().size()).append("\n");
        text.append("Suspects questioned: ").append(gameState.getQuestionedSuspects().size()).append("\n");
        
        window.displayText(text.toString());
        window.clearButtons();
        
        window.addChoiceButton("Question a Suspect", this::showSuspects);
        window.addChoiceButton("Review Clues", this::showClues);
        window.addChoiceButton("Make an Accusation", this::makeAccusation);
        window.addChoiceButton("Save Investigation Log", this::saveInvestigationLog);
    }
    
    private void showSuspects() {
        List<Suspect> suspects = gameData.getSuspects();
        window.displayText("Choose a suspect to question:");
        window.clearButtons();
        
        for (Suspect suspect : suspects) {
            window.addChoiceButton(suspect.getName() + " - " + suspect.getRole(), 
                () -> questionSuspect(suspect));
        }
        
        window.addChoiceButton("Back to Menu", this::showMainMenu);
    }
    
    private void questionSuspect(Suspect suspect) {
        gameState.addQuestionedSuspect(suspect);
        logger.logSuspectQuestioned(suspect.getName(), suspect.getRole());
        choiceCounter++;
        
        window.displayText("You question " + suspect.getName() + ":\n\n" + 
                          suspect.getStatement());
        
        // Check if this reveals any clues
        List<Clue> revealedClues = suspect.getClues();
        int cluesDiscovered = 0;
        if (!revealedClues.isEmpty()) {
            StringBuilder clueText = new StringBuilder();
            for (Clue clue : revealedClues) {
                if (!gameState.hasClue(clue)) {
                    gameState.addClue(clue);
                    logger.logClueDiscovered(clue.getName(), clue.getDescription());
                    clueText.append("\n[New Clue Found: ").append(clue.getName()).append("]");
                    cluesDiscovered++;
                    
                    // Record clue in database (if available)
                    if (dbManager.isAvailable()) {
                        dbManager.recordClueDiscovered(progressId, clue.getName());
                    }
                }
            }
            if (clueText.length() > 0) {
                window.appendText(clueText.toString());
            }
        }
        
        // Record player choice in database (if available)
        if (dbManager.isAvailable()) {
            dbManager.recordPlayerChoice(progressId, playerId, suspect.getName(), cluesDiscovered, choiceCounter);
            
            // Update case progress
            dbManager.updateCaseProgress(progressId, gameState.getQuestionedSuspects().size(), gameState.getClues().size());
        }
        
        window.clearButtons();
        window.addChoiceButton("Continue", this::showMainMenu);
    }
    
    private void showClues() {
        List<Clue> clues = gameState.getClues();
        logger.logCluesReviewed(clues.size());
        
        StringBuilder text = new StringBuilder("Clues you've gathered:\n\n");
        
        if (clues.isEmpty()) {
            text.append("No clues found yet. Question suspects to find clues!");
        } else {
            for (Clue clue : clues) {
                text.append("• ").append(clue.getName()).append("\n");
                text.append("  ").append(clue.getDescription()).append("\n\n");
            }
        }
        
        window.displayText(text.toString());
        window.clearButtons();
        window.addChoiceButton("Back to Menu", this::showMainMenu);
    }
    
    private void makeAccusation() {
        List<Suspect> suspects = gameData.getSuspects();
        window.displayText("Who do you think is the culprit?");
        window.clearButtons();
        
        for (Suspect suspect : suspects) {
            window.addChoiceButton("Accuse " + suspect.getName(), 
                () -> checkAccusation(suspect));
        }
        
        window.addChoiceButton("Back to Menu", this::showMainMenu);
    }
    
    private void checkAccusation(Suspect suspect) {
        boolean correct = suspect.isGuilty();
        
        logger.logAccusation(suspect.getName(), correct);
        
        // Create case summary
        List<String> clueNames = new ArrayList<>();
        for (Clue clue : gameState.getClues()) {
            clueNames.add(clue.getName());
        }
        List<String> suspectNames = new ArrayList<>();
        for (Suspect s : gameState.getQuestionedSuspects()) {
            suspectNames.add(s.getName());
        }
        logger.logCaseSummary(clueNames, suspectNames, correct);
        
        // Calculate time spent
        int timeMinutes = (int) ((System.currentTimeMillis() - gameStartTime) / 60000);
        
        // Save case completion to database (if available)
        if (dbManager.isAvailable()) {
            dbManager.completeCase(progressId, correct, suspect.getName(), timeMinutes);
            dbManager.updatePlayerStats(playerId, correct);
        }
        
        if (correct) {
            window.displayText("Congratulations! You solved the mystery!\n\n" +
                              suspect.getName() + " was indeed the culprit.\n\n" +
                              "The evidence all pointed to them. Well done, Detective!");
        } else {
            window.displayText("Wrong accusation!\n\n" +
                              suspect.getName() + " was not the culprit.\n\n" +
                              "You need more evidence. The case remains unsolved.");
        }
        
        window.clearButtons();
        window.addChoiceButton("Save Final Report", this::saveInvestigationLog);
        window.addChoiceButton("Play Again", this::resetGame);
        window.addChoiceButton("Exit", () -> System.exit(0));
    }
    
    private void saveInvestigationLog() {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = "investigation_log_" + timestamp + ".txt";
        
        boolean success = logger.saveToFile(fileName);
        
        if (success) {
            JOptionPane.showMessageDialog(window, 
                "Investigation log saved to: " + fileName,
                "Log Saved",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(window,
                "Failed to save investigation log.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetGame() {
        gameState = new GameState();
        logger = new InvestigationLogger();
        choiceCounter = 0;
        showIntroduction();
    }
}
