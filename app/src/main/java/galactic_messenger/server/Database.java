// Database.java
package galactic_messenger.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:app/database/galactic_messenger.db";

    private Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC Driver not found");
        }
    }

    public Database() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL" +
                            ")");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String registerUser(String username, String password) {
        if (userExists(username)) {
            return "Registration failed: User already exists.";
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return "Registration successful.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Registration failed: SQL error.";
        }
    }

    public boolean userExists(String username) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loginUser(String username, String password) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "Login successful.";
            } else {
                return "Login failed: User not found or incorrect credentials.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Login failed: SQL error.";
        }
    }
}