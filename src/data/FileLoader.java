package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {
    
    /**
     * Loads the crime story from case1.txt
     */
    public static String loadCrimeStory(String filePath) {
        StringBuilder story = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                story.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error loading crime story: " + e.getMessage());
            // Return default story if file not found
            return getDefaultCrimeStory();
        }
        
        return story.toString();
    }
    
    /**
     * Parses clues from clues.txt file
     */
    public static List<ClueData> loadClues(String filePath) {
        List<ClueData> clues = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentClueName = null;
            String currentLocation = null;
            StringBuilder currentDescription = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Detect clue headers (e.g., "CLUE #1: TORN RED FABRIC")
                if (line.startsWith("CLUE #") && line.contains(":")) {
                    // Save previous clue if exists
                    if (currentClueName != null) {
                        clues.add(new ClueData(currentClueName, currentDescription.toString().trim(), currentLocation));
                    }
                    
                    // Start new clue
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        currentClueName = parts[1].trim();
                        currentDescription = new StringBuilder();
                        currentLocation = "";
                    }
                } else if (line.startsWith("Location:")) {
                    currentLocation = line.substring("Location:".length()).trim();
                } else if (line.startsWith("Description:")) {
                    currentDescription.append(line.substring("Description:".length()).trim());
                } else if (!line.isEmpty() && !line.equals("=".repeat(line.length())) && 
                          !line.startsWith("CLUES DISCOVERED") && !line.startsWith("Significance:") &&
                          !line.startsWith("IMPORTANT NOTES") && !line.startsWith("---")) {
                    // Continue building description
                    if (currentDescription.length() > 0 && !line.startsWith("•")) {
                        currentDescription.append(" ");
                    }
                    currentDescription.append(line);
                }
            }
            
            // Add the last clue
            if (currentClueName != null) {
                clues.add(new ClueData(currentClueName, currentDescription.toString().trim(), currentLocation));
            }
            
        } catch (IOException e) {
            System.err.println("Error loading clues: " + e.getMessage());
            return getDefaultClues();
        }
        
        return clues;
    }
    
    /**
     * Parses suspects from suspects.txt file
     */
    public static List<SuspectData> loadSuspects(String filePath) {
        List<SuspectData> suspects = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentName = null;
            String currentRole = null;
            StringBuilder currentStatement = new StringBuilder();
            boolean inStatement = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Detect suspect headers (e.g., "SUSPECT #1: DR. ELEANOR HAYES")
                if (line.startsWith("SUSPECT #") && line.contains(":")) {
                    // Save previous suspect if exists
                    if (currentName != null && currentStatement.length() > 0) {
                        suspects.add(new SuspectData(currentName, currentRole, currentStatement.toString().trim()));
                    }
                    
                    // Start new suspect
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        currentName = parts[1].trim();
                        currentRole = "";
                        currentStatement = new StringBuilder();
                        inStatement = false;
                    }
                } else if (line.startsWith("Occupation:")) {
                    currentRole = line.substring("Occupation:".length()).trim();
                } else if (line.startsWith("OFFICIAL STATEMENT:")) {
                    inStatement = true;
                } else if (line.startsWith("MOTIVE:") || line.startsWith("MEANS:") || 
                          line.startsWith("ALIBI:") || line.startsWith("THE TRUTH")) {
                    inStatement = false;
                } else if (inStatement && !line.isEmpty() && 
                          !line.equals("=".repeat(line.length())) &&
                          !line.startsWith("Age:") && !line.startsWith("Appearance:") &&
                          !line.startsWith("BACKGROUND:")) {
                    // Build statement
                    if (currentStatement.length() > 0) {
                        currentStatement.append("\n\n");
                    }
                    currentStatement.append(line);
                }
            }
            
            // Add the last suspect
            if (currentName != null && currentStatement.length() > 0) {
                suspects.add(new SuspectData(currentName, currentRole, currentStatement.toString().trim()));
            }
            
        } catch (IOException e) {
            System.err.println("Error loading suspects: " + e.getMessage());
            return getDefaultSuspects();
        }
        
        return suspects;
    }
    
    // Default data if files are not found
    private static String getDefaultCrimeStory() {
        return "THE MYSTERY OF THE STOLEN SAPPHIRE\n\n" +
               "A valuable sapphire has been stolen from the Metropolitan Museum. " +
               "As the detective, you must question suspects and gather clues to solve the case.";
    }
    
    private static List<ClueData> getDefaultClues() {
        List<ClueData> clues = new ArrayList<>();
        clues.add(new ClueData("Torn Fabric", "A piece of red fabric found at the scene.", "Back door"));
        clues.add(new ClueData("Footprints", "Size 10 muddy footprints.", "Hallway"));
        return clues;
    }
    
    private static List<SuspectData> getDefaultSuspects() {
        List<SuspectData> suspects = new ArrayList<>();
        suspects.add(new SuspectData("Dr. Hayes", "Curator", "I was home all night."));
        suspects.add(new SuspectData("Marcus Cole", "Security Guard", "I saw nothing suspicious."));
        suspects.add(new SuspectData("Sarah Mitchell", "Assistant Curator", "I left at 6 PM."));
        return suspects;
    }
    
    // Inner classes to hold parsed data
    public static class ClueData {
        public final String name;
        public final String description;
        public final String location;
        
        public ClueData(String name, String description, String location) {
            this.name = name;
            this.description = description;
            this.location = location;
        }
    }
    
    public static class SuspectData {
        public final String name;
        public final String role;
        public final String statement;
        
        public SuspectData(String name, String role, String statement) {
            this.name = name;
            this.role = role;
            this.statement = statement;
        }
    }
}
