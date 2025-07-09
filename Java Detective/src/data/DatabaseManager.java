package data;

import model.Difficulty;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/JavaDetective";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Ju@nrick1995";

    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Connected to MySQL Database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int createPlayer(String username, Difficulty difficulty) {
        String sql = "INSERT INTO players (username, difficulty) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, difficulty.name());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int playerId = rs.getInt(1);
                System.out.println("Player created with ID: " + playerId);
                return playerId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateQuestionedStatus(int playerId, String suspectName, boolean questioned) {
        String checkSql = "SELECT * FROM suspect_status WHERE player_id = ? AND suspect_name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, playerId);
            checkStmt.setString(2, suspectName);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE suspect_status SET questioned = ? WHERE player_id = ? AND suspect_name = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setBoolean(1, questioned);
                    updateStmt.setInt(2, playerId);
                    updateStmt.setString(3, suspectName);
                    updateStmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO suspect_status (player_id, suspect_name, questioned) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, playerId);
                    insertStmt.setString(2, suspectName);
                    insertStmt.setBoolean(3, questioned);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean wasSuspectQuestioned(int playerId, String suspectName) {
        String sql = "SELECT questioned FROM suspect_status WHERE player_id = ? AND suspect_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            stmt.setString(2, suspectName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("questioned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAccusation(int playerId, String suspectName, boolean correct) {
    String sql = "INSERT INTO accusations (player_id, suspect_name, correct) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, playerId);
        stmt.setString(2, suspectName);
        stmt.setBoolean(3, correct);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void updateLastCasePlayed(int playerId, String caseName) {
    String sql = "UPDATE players SET last_case_played = ? WHERE id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, caseName);
        stmt.setInt(2, playerId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
// This class manages the database connection and operations.
// It allows creating players, updating suspect questioning status, checking if a suspect was questioned.

