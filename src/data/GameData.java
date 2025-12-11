package data;

import model.Suspect;
import model.Clue;
import model.CaseDifficulty;
import java.util.ArrayList;
import java.util.List;

public class GameData {
    private List<Suspect> suspects;
    private List<Clue> allClues;
    private String culpritName;
    private String crimeStory;
    private CaseDifficulty difficulty;
    
    public GameData() {
        this.difficulty = CaseDifficulty.MEDIUM;
        loadFromFiles();
    }
    
    private void loadFromFiles() {
        // Load crime story from file using FileLoader
        crimeStory = FileLoader.loadCrimeStory("data/case1.txt");
        
        // Load clues from file using FileLoader
        List<FileLoader.ClueData> clueDataList = FileLoader.loadClues("data/clues.txt");
        allClues = new ArrayList<>();
        for (FileLoader.ClueData clueData : clueDataList) {
            Clue clue = new Clue(clueData.name, clueData.description, clueData.location);
            allClues.add(clue);
        }
        
        // Load suspects from file using FileLoader
        List<FileLoader.SuspectData> suspectDataList = FileLoader.loadSuspects("data/suspects.txt");
        suspects = new ArrayList<>();
        
        for (FileLoader.SuspectData suspectData : suspectDataList) {
            Suspect suspect = new Suspect(suspectData.name, suspectData.role, suspectData.statement);
            
            // Assign clues to suspects based on names
            if (suspectData.name.contains("Eleanor Hayes") || suspectData.name.contains("ELEANOR HAYES")) {
                // Dr. Hayes gets red fabric and alibi photo clues
                for (Clue clue : allClues) {
                    String clueName = clue.getName().toUpperCase();
                    if (clueName.contains("RED") && clueName.contains("FABRIC") || 
                        clueName.contains("ALIBI") || clueName.contains("PHOTOGRAPH")) {
                        suspect.setClue(clue);
                    }
                }
            } else if (suspectData.name.contains("Marcus Cole") || suspectData.name.contains("MARCUS COLE")) {
                // Marcus gets footprints and security footage clues
                for (Clue clue : allClues) {
                    String clueName = clue.getName().toUpperCase();
                    if (clueName.contains("FOOTPRINT") || clueName.contains("MUDDY") ||
                        clueName.contains("SECURITY") && clueName.contains("FOOTAGE")) {
                        suspect.setClue(clue);
                    }
                }
            } else if (suspectData.name.contains("Sarah Mitchell") || suspectData.name.contains("SARAH MITCHELL")) {
                // Sarah gets key card and financial records clues
                for (Clue clue : allClues) {
                    String clueName = clue.getName().toUpperCase();
                    if (clueName.contains("KEY") && clueName.contains("CARD") || 
                        clueName.contains("FINANCIAL") || clueName.contains("STOLEN KEY")) {
                        suspect.setClue(clue);
                    }
                }
                suspect.setGuilty(true);
            }
            
            suspects.add(suspect);
        }
        
        culpritName = "Sarah Mitchell";
    }
    
    public List<Suspect> getSuspects() {
        return suspects;
    }
    
    public List<Clue> getAllClues() {
        return allClues;
    }
    
    public String getCulpritName() {
        return culpritName;
    }
    
    public CaseDifficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(CaseDifficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getCrimeStory() {
        return crimeStory;
    }
}
