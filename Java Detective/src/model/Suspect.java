package model;

public class Suspect {
    private String name;
    private String alibi;
    private boolean questioned;

    public Suspect(String name, String alibi) {
        this.name = name;
        this.alibi = alibi;
        this.questioned = false;
    }

    public String getName() {
        return name;
    }

    public String getAlibi() {
        return alibi;
    }

    public boolean wasQuestioned() {
        return questioned;
    }

    public void setQuestioned(boolean questioned) {
        this.questioned = questioned;
    }
}
// This class represents a suspect in the game. It contains the suspect's name, alibi, and whether they have been questioned.





