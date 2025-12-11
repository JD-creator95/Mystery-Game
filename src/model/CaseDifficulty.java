package model;

public enum CaseDifficulty {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3);
    
    private final String displayName;
    private final int level;
    
    CaseDifficulty(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
