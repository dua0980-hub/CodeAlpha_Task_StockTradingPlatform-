package stocktradingplatform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/trading_platform?useSSL=false";
    private static final String USER = "root";       
    private static final String PASSWORD = "dua123"; 

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

       public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Automatically creates the authentication framework tables if missing
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username VARCHAR(50) PRIMARY KEY, password VARCHAR(50) NOT NULL, balance DOUBLE NOT NULL)");
            stmt.execute("INSERT IGNORE INTO users (username, password, balance) VALUES ('dua', '123', 10000.0)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS portfolio (symbol VARCHAR(10) PRIMARY KEY, shares INT DEFAULT 0)");
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INT AUTO_INCREMENT PRIMARY KEY, type VARCHAR(10), symbol VARCHAR(10), shares INT, price DOUBLE, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS portfolio_history (id INT AUTO_INCREMENT PRIMARY KEY, total_value DOUBLE, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User loadUser(String username) {
        User user = new User(username, 10000.0);
        try (Connection conn = connect()) {
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM users WHERE username = ?")) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    user = new User(username, rs.getDouble("balance"));
                }
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT symbol, shares FROM portfolio")) {
                while (rs.next()) {
                    user.updatePortfolioShares(rs.getString("symbol"), rs.getInt("shares"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return user;
    }

    public static void saveTrade(String username, String type, String symbol, int tradedShares, int updatedShares, double nextBalance, double sharePrice) {
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET balance = ? WHERE username = ?")) {
                pstmt.setDouble(1, nextBalance);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO transactions (type, symbol, shares, price) VALUES (?, ?, ?, ?)")) {
                pstmt.setString(1, type);
                pstmt.setString(2, symbol);
                pstmt.setInt(3, tradedShares);
                pstmt.setDouble(4, sharePrice);
                pstmt.executeUpdate();
            }

            if (updatedShares > 0) {
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO portfolio (symbol, shares) VALUES (?, ?) ON DUPLICATE KEY UPDATE shares = ?")) {
                    pstmt.setString(1, symbol);
                    pstmt.setInt(2, updatedShares);
                    pstmt.setInt(3, updatedShares);
                    pstmt.executeUpdate();
                }
            } else {
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM portfolio WHERE symbol = ?")) {
                    pstmt.setString(1, symbol);
                    pstmt.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public static void logHistoricalSnapshot(double totalValue) {
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO portfolio_history (total_value) VALUES (?)")) {
            pstmt.setDouble(1, totalValue);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<String> getPerformanceHistory() {
        List<String> list = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT total_value, timestamp FROM portfolio_history ORDER BY timestamp DESC LIMIT 20")) {
            while (rs.next()) {
                list.add(rs.getString("timestamp") + " -> Total Net Worth: $" + String.format("%.2f", rs.getDouble("total_value")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Transaction> getTransactionHistory() {
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM transactions ORDER BY timestamp DESC")) {
            while (rs.next()) {
                list.add(new Transaction(rs.getString("type"), rs.getString("symbol"),
                        rs.getInt("shares"), rs.getDouble("price"), rs.getString("timestamp")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
