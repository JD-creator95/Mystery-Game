package data;

import model.Clue;
import model.Suspect;

import java.io.*;
import java.util.*;

public class DataLoader {
    public static List<Clue> loadClues(String filename) {
        List<Clue> clues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                clues.add(new Clue(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clues;
    }

    public static List<Suspect> loadSuspects(String filename) {
        List<Suspect> suspects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    suspects.add(new Suspect(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suspects;
    }

    public static String loadStory(String filename) {
        StringBuilder story = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                story.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return story.toString();
    }
}
// This class is responsible for loading clues, suspects, and the story from files.
// It reads the data from text files and creates Clue and Suspect objects.
