package data;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DatabaseManager - Handles all database operations for the Mystery Game
 * Uses SQLite for local storage with PreparedStatements for security
 */
public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:sqlite:mystery_game.db";
    private Connection connection;
    private boolean dbAvailable = false;
    
    public DatabaseManager() {
        try {
            connect();
            createTables();
            dbAvailable = true;
        } catch (SQLException e) {
            System.err.println("Database not available: " + e.getMessage());
            System.err.println("Game will continue without database features.");
            System.err.println("See DATABASE_SETUP.md for setup instructions.");
            dbAvailable = false;
        }
    }
    
    /**
     * Check if database is available
     */
    public boolean isAvailable() {
        return dbAvailable;
    }
    
    /**
     * Establishes connection to SQLite database
     */
    private void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        System.out.println("Connected to SQLite database: mystery_game.db");
    }
    
    /**
     * Creates all necessary database tables
     */
    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        
        // Player profiles table
        String createPlayersTable = 
            "CREATE TABLE IF NOT EXISTS players (" +
            "player_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE NOT NULL, " +
            "created_at TEXT NOT NULL, " +
            "total_games INTEGER DEFAULT 0, " +
            "cases_solved INTEGER DEFAULT 0, " +
            "cases_failed INTEGER DEFAULT 0)";
        
        // Case progress table
        String createCaseProgressTable = 
            "CREATE TABLE IF NOT EXISTS case_progress (" +
            "progress_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "player_id INTEGER NOT NULL, " +
            "case_name TEXT NOT NULL, " +
            "started_at TEXT NOT NULL, " +
            "completed_at TEXT, " +
            "is_solved INTEGER DEFAULT 0, " +
            "suspects_questioned INTEGER DEFAULT 0, " +
            "clues_found INTEGER DEFAULT 0, " +
            "accused_suspect TEXT, " +
            "time_spent_minutes INTEGER DEFAULT 0, " +
            "FOREIGN KEY(player_id) REFERENCES players(player_id))";
        
        // Suspect data table
        String createSuspectsTable = 
            "CREATE TABLE IF NOT EXISTS suspects (" +
            "suspect_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "suspect_name TEXT NOT NULL, " +
            "role TEXT NOT NULL, " +
            "statement TEXT NOT NULL, " +
            "is_guilty INTEGER DEFAULT 0, " +
            "case_name TEXT NOT NULL)";
        
        // Player choices table - tracks which suspects were questioned and when
        String createPlayerChoicesTable = 
            "CREATE TABLE IF NOT EXISTS player_choices (" +
            "choice_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "progress_id INTEGER NOT NULL, " +
            "player_id INTEGER NOT NULL, " +
            "suspect_name TEXT NOT NULL, " +
            "questioned_at TEXT NOT NULL, " +
            "clues_discovered INTEGER DEFAULT 0, " +
            "choice_order INTEGER, " +
            "FOREIGN KEY(progress_id) REFERENCES case_progress(progress_id), " +
            "FOREIGN KEY(player_id) REFERENCES players(player_id))";
        
        // Clues discovered table
        String createCluesTable = 
            "CREATE TABLE IF NOT EXISTS clues_discovered (" +
            "clue_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "progress_id INTEGER NOT NULL, " +
            "clue_name TEXT NOT NULL, " +
            "discovered_at TEXT NOT NULL, " +
            "FOREIGN KEY(progress_id) REFERENCES case_progress(progress_id))";
        
        stmt.execute(createPlayersTable);
        stmt.execute(createCaseProgressTable);
        stmt.execute(createSuspectsTable);
        stmt.execute(createPlayerChoicesTable);
        stmt.execute(createCluesTable);
        
        stmt.close();
        System.out.println("Database tables created successfully");
    }
    
    // ==================== PLAYER PROFILE OPERATIONS ====================
    
    /**
     * Creates a new player profile using PreparedStatement
     */
    public int createPlayer(String username) {
        if (!dbAvailable) return -1;
        
        String sql = "INSERT INTO players (username, created_at) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, getCurrentTimestamp());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int playerId = rs.getInt(1);
                    System.out.println("Player created with ID: " + playerId);
                    return playerId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating player: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Gets player ID by username using PreparedStatement
     */
    public int getPlayerId(String username) {
        if (!dbAvailable) return -1;
        
        String sql = "SELECT player_id FROM players WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("player_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting player ID: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Updates player statistics using PreparedStatement
     */
    public void updatePlayerStats(int playerId, boolean caseSolved) {
        if (!dbAvailable) return;
        
        String sql = "UPDATE players SET total_games = total_games + 1, " +
                     (caseSolved ? "cases_solved = cases_solved + 1" : "cases_failed = cases_failed + 1") +
                     " WHERE player_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            pstmt.executeUpdate();
            System.out.println("Player stats updated");
        } catch (SQLException e) {
            System.err.println("Error updating player stats: " + e.getMessage());
        }
    }
    
    /**
     * Gets player statistics using PreparedStatement
     */
    public PlayerStats getPlayerStats(int playerId) {
        if (!dbAvailable) return null;
        
        String sql = "SELECT * FROM players WHERE player_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new PlayerStats(
                    rs.getInt("player_id"),
                    rs.getString("username"),
                    rs.getInt("total_games"),
                    rs.getInt("cases_solved"),
                    rs.getInt("cases_failed")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting player stats: " + e.getMessage());
        }
        
        return null;
    }
    
    // ==================== CASE PROGRESS OPERATIONS ====================
    
    /**
     * Starts a new case for a player using PreparedStatement
     */
    public int startCase(int playerId, String caseName) {
        if (!dbAvailable) return -1;
        
        String sql = "INSERT INTO case_progress (player_id, case_name, started_at) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, playerId);
            pstmt.setString(2, caseName);
            pstmt.setString(3, getCurrentTimestamp());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int progressId = rs.getInt(1);
                System.out.println("Case started with progress ID: " + progressId);
                return progressId;
            }
        } catch (SQLException e) {
            System.err.println("Error starting case: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Updates case progress using PreparedStatement
     */
    public void updateCaseProgress(int progressId, int suspectsQuestioned, int cluesFound) {
        if (!dbAvailable) return;
        
        String sql = "UPDATE case_progress SET suspects_questioned = ?, clues_found = ? WHERE progress_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, suspectsQuestioned);
            pstmt.setInt(2, cluesFound);
            pstmt.setInt(3, progressId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating case progress: " + e.getMessage());
        }
    }
    
    /**
     * Completes a case using PreparedStatement
     */
    public void completeCase(int progressId, boolean solved, String accusedSuspect, int timeMinutes) {
        if (!dbAvailable) return;
        
        String sql = "UPDATE case_progress SET completed_at = ?, is_solved = ?, " +
                     "accused_suspect = ?, time_spent_minutes = ? WHERE progress_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, getCurrentTimestamp());
            pstmt.setInt(2, solved ? 1 : 0);
            pstmt.setString(3, accusedSuspect);
            pstmt.setInt(4, timeMinutes);
            pstmt.setInt(5, progressId);
            
            pstmt.executeUpdate();
            System.out.println("Case completed and saved to database");
        } catch (SQLException e) {
            System.err.println("Error completing case: " + e.getMessage());
        }
    }
    
    /**
     * Gets case progress for a player using PreparedStatement
     */
    public ResultSet getCaseHistory(int playerId) {
        if (!dbAvailable) return null;
        
        String sql = "SELECT * FROM case_progress WHERE player_id = ? ORDER BY started_at DESC";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting case history: " + e.getMessage());
        }
        
        return null;
    }
    
    // ==================== SUSPECT DATA OPERATIONS ====================
    
    /**
     * Saves suspect data to database using PreparedStatement
     */
    public void saveSuspect(String name, String role, String statement, boolean isGuilty, String caseName) {
        if (!dbAvailable) return;
        
        String sql = "INSERT INTO suspects (suspect_name, role, statement, is_guilty, case_name) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, statement);
            pstmt.setInt(4, isGuilty ? 1 : 0);
            pstmt.setString(5, caseName);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving suspect: " + e.getMessage());
        }
    }
    
    /**
     * Gets all suspects for a case using PreparedStatement
     */
    public ResultSet getSuspects(String caseName) {
        if (!dbAvailable) return null;
        
        String sql = "SELECT * FROM suspects WHERE case_name = ?";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, caseName);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting suspects: " + e.getMessage());
        }
        
        return null;
    }
    
    // ==================== PLAYER CHOICES OPERATIONS ====================
    
    /**
     * Records a player's choice to question a suspect using PreparedStatement
     */
    public void recordPlayerChoice(int progressId, int playerId, String suspectName, int cluesDiscovered, int choiceOrder) {
        if (!dbAvailable) return;
        
        String sql = "INSERT INTO player_choices (progress_id, player_id, suspect_name, questioned_at, clues_discovered, choice_order) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, progressId);
            pstmt.setInt(2, playerId);
            pstmt.setString(3, suspectName);
            pstmt.setString(4, getCurrentTimestamp());
            pstmt.setInt(5, cluesDiscovered);
            pstmt.setInt(6, choiceOrder);
            
            pstmt.executeUpdate();
            System.out.println("Player choice recorded: " + suspectName);
        } catch (SQLException e) {
            System.err.println("Error recording player choice: " + e.getMessage());
        }
    }
    
    /**
     * Gets player choices for a case using PreparedStatement
     */
    public ResultSet getPlayerChoices(int progressId) {
        if (!dbAvailable) return null;
        
        String sql = "SELECT * FROM player_choices WHERE progress_id = ? ORDER BY choice_order";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, progressId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting player choices: " + e.getMessage());
        }
        
        return null;
    }
    
    // ==================== CLUES OPERATIONS ====================
    
    /**
     * Records a discovered clue using PreparedStatement
     */
    public void recordClueDiscovered(int progressId, String clueName) {
        if (!dbAvailable) return;
        
        String sql = "INSERT INTO clues_discovered (progress_id, clue_name, discovered_at) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, progressId);
            pstmt.setString(2, clueName);
            pstmt.setString(3, getCurrentTimestamp());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording clue: " + e.getMessage());
        }
    }
    
    /**
     * Gets all clues discovered in a case using PreparedStatement
     */
    public ResultSet getDiscoveredClues(int progressId) {
        if (!dbAvailable) return null;
        
        String sql = "SELECT * FROM clues_discovered WHERE progress_id = ? ORDER BY discovered_at";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, progressId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting discovered clues: " + e.getMessage());
        }
        
        return null;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Gets current timestamp in standard format
     */
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    /**
     * Closes database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
    
    /**
     * Gets the active connection
     */
    public Connection getConnection() {
        return connection;
    }
    
    // ==================== INNER CLASS ====================
    
    /**
     * Data class for player statistics
     */
    public static class PlayerStats {
        public final int playerId;
        public final String username;
        public final int totalGames;
        public final int casesSolved;
        public final int casesFailed;
        
        public PlayerStats(int playerId, String username, int totalGames, int casesSolved, int casesFailed) {
            this.playerId = playerId;
            this.username = username;
            this.totalGames = totalGames;
            this.casesSolved = casesSolved;
            this.casesFailed = casesFailed;
        }
        
        @Override
        public String toString() {
            return String.format("Player: %s | Games: %d | Solved: %d | Failed: %d | Success Rate: %.1f%%",
                username, totalGames, casesSolved, casesFailed, 
                totalGames > 0 ? (casesSolved * 100.0 / totalGames) : 0);
        }
    }
}
