import java.sql.*;
import java.util.PriorityQueue;

public class _16_TerminalCLI {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASS = System.getenv("DB_PASS"); // Secured environment parameter

    public static void main(String[] args) {
        // Quick fail-safe if environment variables are not loaded in IntelliJ
        if (PASS == null || PASS.trim().isEmpty()) {
            System.err.println(" SECURITY ERROR: Environment variable 'DB_PASS' is missing!");
            System.err.println(" Please map DB_PASS in your IntelliJ Run Configurations.");
            return;
        }

        System.out.println("==========================================");
        System.out.println("   PRIORITY PAYMENT PROCESSOR (DAY 16) ");
        System.out.println("==========================================");

        // 1. Initializing our DSA PriorityQueue
        PriorityQueue<_16_PaymentTask> paymentQueue = new PriorityQueue<>();

        // Adding transfers in random chronological order
        System.out.println(" Receiving incoming payments into queue buffer...");
        paymentQueue.add(new _16_PaymentTask("suri", 150.00, 1));   // Standard transfer
        paymentQueue.add(new _16_PaymentTask("teja", 25000.00, 3)); // Premium VIP transfer
        paymentQueue.add(new _16_PaymentTask("suri", 400.00, 2));   // Medium business transfer
        paymentQueue.add(new _16_PaymentTask("teja", 50.00, 1));    // Standard transfer

        // 2. Process elements sequentially from highest priority down to lowest
        processPriorityPayments(paymentQueue);

        // 3. Output database records to verify performance
        verifyDatabaseLedger();
    }

    private static void processPriorityPayments(PriorityQueue<_16_PaymentTask> queue) {
        String insertSQL = "INSERT INTO transaction_logs (user_tag, transaction_type, amount) VALUES (?, 'CREDIT', ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // 🛡 MYSQL ISOLATION UPGRADE: Set to highest level to ensure total concurrency safety
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false); // Begin managed atomic block

            System.out.println("\n Processing payments via highest ISOLATION LEVEL (SERIALIZABLE)...");

            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                while (!queue.isEmpty()) {
                    // .poll() pulls the element with the highest priority out first!
                    _16_PaymentTask currentTask = queue.poll();

                    System.out.printf("  Executing: [User: %-5s | Priority Rank: %d | Amount: $%.2f]\n",
                            currentTask.getUserTag(), currentTask.getPriorityLevel(), currentTask.getAmount());

                    ps.setString(1, currentTask.getUserTag());
                    ps.setDouble(2, currentTask.getAmount());
                    ps.executeUpdate();
                }

                conn.commit(); // Safely save prioritized ledger states to disk
                System.out.println(" All priority queue executions committed securely.");

            } catch (SQLException e) {
                conn.rollback();
                System.err.println(" Core processing failed. Queue changes rolled back.");
            }
        } catch (SQLException e) {
            System.err.println(" DB Connection failure: " + e.getMessage());
        }
    }

    private static void verifyDatabaseLedger() {
        String sql = "SELECT * FROM transaction_logs ORDER BY log_id ASC";
        System.out.println("\n--- LIVE MYSQL DATABASE STORAGE TRAIL ---");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(" Insertion ID: %-3d | Account Owner: %-5s | Value: $%.2f\n",
                        rs.getInt("log_id"),
                        rs.getString("user_tag"),
                        rs.getDouble("amount")
                );
            }
            System.out.println("-----------------------------------------");
        } catch (SQLException e) {
            System.err.println("Error fetching active ledger tracking states: " + e.getMessage());
        }
    }
}