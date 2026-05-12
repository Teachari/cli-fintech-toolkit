import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class _14_TerminalCLI {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = "root";
    private static final String PASS = "Teja@8004";

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   CORE FINTECH TRANSACTION SYSTEM    ");
        System.out.println("==========================================");

        // 1. Defining incoming transaction records dynamically in memory
        List<_14_Transaction> incomingQueue = new ArrayList<>();
        incomingQueue.add(new _14_Transaction("teja", "CREDIT", 15000.00));
        incomingQueue.add(new _14_Transaction("suri", "DEBIT", -350.00));
        incomingQueue.add(new _14_Transaction("teja", "DEBIT", -1200.00));
        incomingQueue.add(new _14_Transaction("suri", "CREDIT", 4500.00));

        // 2. Process all incoming ledger changes
        processTransactionQueue(incomingQueue);

        // 3. Output updated system matrix summary
        printLiveLedgerMetrics();
    }

    private static void processTransactionQueue(List<_14_Transaction> transactions) {
        String insertSQL = "INSERT INTO transaction_logs (user_tag, transaction_type, amount) VALUES (?, ?, ?)";

        System.out.println(" Initiating backend transaction processor...");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            // Turn off auto-commit to execute batch tracking concurrently
            conn.setAutoCommit(false);

            for (_14_Transaction tx : transactions) {
                ps.setString(1, tx.getUserTag());
                ps.setString(2, tx.getType());
                ps.setDouble(3, tx.getAmount());
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            conn.commit(); // Push atomically to the SQL server instance
            System.out.println("⚡ Live transaction stream processed successfully!");
            System.out.println( results.length + " ledger updates committed securely.");

        } catch (SQLException e) {
            System.err.println(" Database Transaction Error: " + e.getMessage());
        }
    }

    private static void printLiveLedgerMetrics() {
        String metricSQL = "SELECT user_tag, " +
                "COUNT(log_id) as total_tx, " +
                "SUM(amount) as calculated_balance " +
                "FROM transaction_logs GROUP BY user_tag";

        System.out.println("\n Querying Live System Ledger Matrix...");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(metricSQL)) {

            System.out.println("\n--- 🛡 VERIFIED ACCOUNT BALANCE INDEX ---");
            while (rs.next()) {
                System.out.printf(" User: %-6s | Active Trans: %-2d | Total Balance: $%10.2f\n",
                        rs.getString("user_tag"),
                        rs.getInt("total_tx"),
                        rs.getDouble("calculated_balance")
                );
            }
            System.out.println("----------------------------------------");

        } catch (SQLException e) {
            System.err.println(" Error processing data summaries: " + e.getMessage());
        }
    }
}
