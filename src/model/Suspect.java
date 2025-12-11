package model;

import java.util.ArrayList;
import java.util.List;

public class Suspect {
    private String name;
    private String role;
    private String statement;
    private List<Clue> clues;
    private boolean isGuilty;
    
    public Suspect(String name, String role, String statement) {
        this.name = name;
        this.role = role;
        this.statement = statement;
        this.clues = new ArrayList<>();
        this.isGuilty = false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatement() {
        return statement;
    }
    
    public void setStatement(String statement) {
        this.statement = statement;
    }
    
    public List<Clue> getClues() {
        return clues;
    }
    
    public void setClue(Clue clue) {
        if (clue != null && !this.clues.contains(clue)) {
            this.clues.add(clue);
        }
    }
    
    public Clue getClue() {
        // Return first clue for backward compatibility
        return clues.isEmpty() ? null : clues.get(0);
    }
    
    public boolean isGuilty() {
        return isGuilty;
    }
    
    public void setGuilty(boolean guilty) {
        isGuilty = guilty;
    }
}
