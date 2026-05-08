import java.sql.*;
public class _10_FintechManager {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = "root";
    private static final String PASS = "Teja@8004";
    public void fetchHistory(String username) {
        // Instantiate our generic helper specifying String type elements
        _10_TransactionHistory<String> history = new _10_TransactionHistory<>();
        // SQL Query to pull logs sorted by entry date
        String query = "SELECT transaction_type, amount, created_at FROM transaction_logs WHERE user_tag = ? ORDER BY created_at ASC";
        // Day 9 Robust Error Handling: try-with-resources auto-closes database connections
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasData = false;

                while (rs.next()) {
                    hasData = true;
                    String type = rs.getString("transaction_type");
                    double amount = rs.getDouble("amount");
                    Timestamp timestamp = rs.getTimestamp("created_at");

                    // Format the row into a clean ledger string
                    String logLine = String.format("[%s] %-6s : $%8.2f", timestamp, type, amount);

                    // Add directly into our dynamic Generic Collection
                    history.addLog(logLine);
                }

                if (!hasData) {
                    System.out.println(" No transactions recorded for user: '" + username + "'");
                } else {
                    // Display our populated Generic Array
                    history.displayLogs();
                }
            }

        } catch (SQLException e) {
            System.err.println(" Database Synchronization Error: " + e.getMessage());
        }
    }
}