import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class _11_TerminalCLI {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = "root";
    private static final String PASS = "Teja@8004";

    public static void main(String[] args) {
        List<_11_Transaction> dbTransactions = new ArrayList<>();

        System.out.println(" Establishing JDBC connection to fintech_db...");

        // Securely connecting and querying from transaction_logs
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             // Synchronized with your MySQL column: 'transaction_type'
             ResultSet rs = stmt.executeQuery("SELECT transaction_type, amount FROM transaction_logs")) {

            while (rs.next()) {
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");

                // Map database record to our Java object & store in ArrayList collection
                dbTransactions.add(new _11_Transaction(type, amount));
            }

            // Pipe our populated collection directly into the Stream-Based Analytics Engine
            _11_StreamAnalytics.runAnalytics(dbTransactions);

        } catch (SQLException e) {
            System.err.println(" Database Sync Error: " + e.getMessage());
        }
    }
}