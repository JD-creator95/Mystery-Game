package data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DatabaseTest - Demonstrates and tests all database functionality
 */
public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=== MYSTERY GAME DATABASE TEST ===\n");
        
        DatabaseManager db = new DatabaseManager();
        
        try {
            // Test 1: Create players
            System.out.println("TEST 1: Creating Player Profiles");
            System.out.println("---------------------------------");
            int player1 = db.createPlayer("Detective Holmes");
            int player2 = db.createPlayer("Detective Poirot");
            System.out.println("Created 2 players\n");
            
            // Test 2: Start cases
            System.out.println("TEST 2: Starting Cases");
            System.out.println("----------------------");
            int case1 = db.startCase(player1, "The Stolen Sapphire");
            int case2 = db.startCase(player2, "The Stolen Sapphire");
            System.out.println("Started 2 cases\n");
            
            // Test 3: Save suspect data
            System.out.println("TEST 3: Saving Suspect Data");
            System.out.println("---------------------------");
            db.saveSuspect("Dr. Eleanor Hayes", "Museum Curator", "I was home all night...", false, "The Stolen Sapphire");
            db.saveSuspect("Marcus Cole", "Security Guard", "I was on duty...", false, "The Stolen Sapphire");
            db.saveSuspect("Sarah Mitchell", "Assistant Curator", "I left at 6 PM...", true, "The Stolen Sapphire");
            System.out.println("Saved 3 suspects\n");
            
            // Test 4: Record player choices
            System.out.println("TEST 4: Recording Player Choices");
            System.out.println("--------------------------------");
            db.recordPlayerChoice(case1, player1, "Dr. Eleanor Hayes", 2, 1);
            db.recordPlayerChoice(case1, player1, "Marcus Cole", 2, 2);
            db.recordPlayerChoice(case1, player1, "Sarah Mitchell", 2, 3);
            System.out.println("Recorded 3 player choices for Player 1\n");
            
            // Test 5: Record discovered clues
            System.out.println("TEST 5: Recording Discovered Clues");
            System.out.println("----------------------------------");
            db.recordClueDiscovered(case1, "Torn Red Fabric");
            db.recordClueDiscovered(case1, "Muddy Footprints");
            db.recordClueDiscovered(case1, "Security Footage Gap");
            db.recordClueDiscovered(case1, "Stolen Key Card");
            System.out.println("Recorded 4 clues\n");
            
            // Test 6: Update case progress
            System.out.println("TEST 6: Updating Case Progress");
            System.out.println("------------------------------");
            db.updateCaseProgress(case1, 3, 4);
            db.updateCaseProgress(case2, 2, 3);
            System.out.println("Updated progress for 2 cases\n");
            
            // Test 7: Complete cases
            System.out.println("TEST 7: Completing Cases");
            System.out.println("------------------------");
            db.completeCase(case1, true, "Sarah Mitchell", 15);
            db.completeCase(case2, false, "Dr. Eleanor Hayes", 12);
            System.out.println("Completed 2 cases\n");
            
            // Test 8: Update player statistics
            System.out.println("TEST 8: Updating Player Statistics");
            System.out.println("----------------------------------");
            db.updatePlayerStats(player1, true);
            db.updatePlayerStats(player2, false);
            System.out.println("Updated stats for 2 players\n");
            
            // Test 9: Retrieve player statistics
            System.out.println("TEST 9: Retrieving Player Statistics");
            System.out.println("------------------------------------");
            DatabaseManager.PlayerStats stats1 = db.getPlayerStats(player1);
            DatabaseManager.PlayerStats stats2 = db.getPlayerStats(player2);
            
            if (stats1 != null) {
                System.out.println(stats1);
            }
            if (stats2 != null) {
                System.out.println(stats2);
            }
            System.out.println();
            
            // Test 10: Retrieve case history
            System.out.println("TEST 10: Retrieving Case History");
            System.out.println("--------------------------------");
            ResultSet caseHistory = db.getCaseHistory(player1);
            if (caseHistory != null) {
                while (caseHistory.next()) {
                    System.out.println("Case: " + caseHistory.getString("case_name"));
                    System.out.println("  Started: " + caseHistory.getString("started_at"));
                    System.out.println("  Completed: " + caseHistory.getString("completed_at"));
                    System.out.println("  Solved: " + (caseHistory.getInt("is_solved") == 1 ? "Yes" : "No"));
                    System.out.println("  Suspects Questioned: " + caseHistory.getInt("suspects_questioned"));
                    System.out.println("  Clues Found: " + caseHistory.getInt("clues_found"));
                    System.out.println("  Accused: " + caseHistory.getString("accused_suspect"));
                    System.out.println("  Time: " + caseHistory.getInt("time_spent_minutes") + " minutes");
                }
                caseHistory.close();
            }
            System.out.println();
            
            // Test 11: Retrieve player choices
            System.out.println("TEST 11: Retrieving Player Choices");
            System.out.println("----------------------------------");
            ResultSet choices = db.getPlayerChoices(case1);
            if (choices != null) {
                while (choices.next()) {
                    System.out.println("Choice " + choices.getInt("choice_order") + ": " + 
                                     choices.getString("suspect_name") + 
                                     " (Clues: " + choices.getInt("clues_discovered") + ")");
                }
                choices.close();
            }
            System.out.println();
            
            // Test 12: Retrieve discovered clues
            System.out.println("TEST 12: Retrieving Discovered Clues");
            System.out.println("------------------------------------");
            ResultSet clues = db.getDiscoveredClues(case1);
            if (clues != null) {
                while (clues.next()) {
                    System.out.println("Clue: " + clues.getString("clue_name") + 
                                     " (Found: " + clues.getString("discovered_at") + ")");
                }
                clues.close();
            }
            System.out.println();
            
            // Test 13: Retrieve suspects
            System.out.println("TEST 13: Retrieving Suspects");
            System.out.println("----------------------------");
            ResultSet suspects = db.getSuspects("The Stolen Sapphire");
            if (suspects != null) {
                while (suspects.next()) {
                    System.out.println("Suspect: " + suspects.getString("suspect_name") + 
                                     " (" + suspects.getString("role") + ")");
                    System.out.println("  Guilty: " + (suspects.getInt("is_guilty") == 1 ? "Yes" : "No"));
                }
                suspects.close();
            }
            System.out.println();
            
            System.out.println("=== ALL TESTS COMPLETED SUCCESSFULLY ===");
            
        } catch (SQLException e) {
            System.err.println("Test error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
