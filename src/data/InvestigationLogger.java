package data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InvestigationLogger {
    private StringBuilder log;
    
    public InvestigationLogger() {
        this.log = new StringBuilder();
        initializeLog();
    }
    
    private void initializeLog() {
        log.append("========================================\n");
        log.append("INVESTIGATION LOG\n");
        log.append("Case: The Stolen Azure Heart Sapphire\n");
        log.append("Date: ").append(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date())).append("\n");
        log.append("========================================\n\n");
    }
    
    public void logGameStart() {
        log.append("Investigation commenced.\n");
        log.append("Reviewing crime scene details...\n\n");
    }
    
    public void logSuspectQuestioned(String suspectName, String role) {
        log.append("--- Questioned: ").append(suspectName).append(" (").append(role).append(") ---\n");
        log.append("Time: ").append(new SimpleDateFormat("HH:mm:ss").format(new Date())).append("\n\n");
    }
    
    public void logClueDiscovered(String clueName, String description) {
        log.append("🔍 CLUE DISCOVERED: ").append(clueName).append("\n");
        log.append("   ").append(description).append("\n\n");
    }
    
    public void logCluesReviewed(int clueCount) {
        log.append("Reviewed evidence. Total clues gathered: ").append(clueCount).append("\n\n");
    }
    
    public void logAccusation(String accusedName, boolean correct) {
        log.append("========================================\n");
        log.append("ACCUSATION MADE\n");
        log.append("========================================\n");
        log.append("Accused: ").append(accusedName).append("\n");
        log.append("Result: ").append(correct ? "CORRECT - Case Solved!" : "INCORRECT - Investigation continues").append("\n");
        log.append("Time: ").append(new SimpleDateFormat("HH:mm:ss").format(new Date())).append("\n\n");
    }
    
    public void logCaseSummary(List<String> cluesFound, List<String> suspectsQuestioned, boolean solved) {
        log.append("========================================\n");
        log.append("CASE SUMMARY\n");
        log.append("========================================\n");
        log.append("Status: ").append(solved ? "SOLVED" : "UNSOLVED").append("\n");
        log.append("Clues Found: ").append(cluesFound.size()).append("\n");
        for (String clue : cluesFound) {
            log.append("  • ").append(clue).append("\n");
        }
        log.append("\nSuspects Questioned: ").append(suspectsQuestioned.size()).append("\n");
        for (String suspect : suspectsQuestioned) {
            log.append("  • ").append(suspect).append("\n");
        }
        log.append("\n");
    }
    
    public void logCustomNote(String note) {
        log.append("NOTE: ").append(note).append("\n\n");
    }
    
    public String getLog() {
        return log.toString();
    }
    
    public boolean saveToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(log.toString());
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving investigation log: " + e.getMessage());
            return false;
        }
    }
    
    public boolean saveToFileWithTimestamp() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "investigation_log_" + timestamp + ".txt";
        return saveToFile(fileName);
    }
}
