import java.sql.*;
import java.util.Scanner;
import java.util.Stack;

public class _15_TransactionEngine {
    //  SECURITY UPGRADE: No more hardcoded passwords!
    // Java will read these values from your computer's system environment variables.
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASS = System.getenv("DB_PASS");

    // Immutable internal representation of a financial ledger adjustment
    static class TransactionNode {
        String userTag;
        String type;
        double amount;

        public TransactionNode(String userTag, String type, double amount) {
            this.userTag = userTag.trim().toLowerCase();
            this.type = type.trim().toUpperCase();
            this.amount = amount;
        }
    }

    private static final Stack<TransactionNode> undoStack = new Stack<>();
    private static final Stack<TransactionNode> redoStack = new Stack<>();

    public static void main(String[] args) {
        // Validation check to stop the program immediately if variables are missing
        if (PASS == null || PASS.trim().isEmpty()) {
            System.err.println(" SECURITY ERROR: System Environment Variable 'DB_PASS' is missing!");
            System.err.println(" Please set up your environment variables in IntelliJ before running.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("==========================================");
        System.out.println("  SECURED TRANSACTION ENGINE (DAY 15)   ");
        System.out.println("==========================================");

        while (true) {
            System.out.println("\n1. Execute New Transaction");
            System.out.println("2. Undo Last Transaction (Stack Pop)");
            System.out.println("3. Redo Last Undone Action");
            System.out.println("4. View Current Database Ledger");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            int choice = sc.nextInt();
            if (choice == 5) break;

            switch (choice) {
                case 1:
                    System.out.print("Enter user tag (e.g., teja): ");
                    String user = sc.next();
                    System.out.print("Enter type (CREDIT/DEBIT): ");
                    String type = sc.next();
                    System.out.print("Enter amount: ");
                    double amt = sc.nextDouble();

                    TransactionNode tx = new TransactionNode(user, type, amt);
                    executeTransactionInDB(tx);
                    break;
                case 2:
                    undoLastTransaction();
                    break;
                case 3:
                    redoLastTransaction();
                    break;
                case 4:
                    printLiveDatabaseLedger();
                    break;
                default:
                    System.out.println("⚠️ Invalid selection.");
            }
        }
        sc.close();
        System.out.println("Goodbye!");
    }

    private static void executeTransactionInDB(TransactionNode tx) {
        String query = "INSERT INTO transaction_logs (user_tag, transaction_type, amount) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, tx.userTag);
                ps.setString(2, tx.type);
                ps.setDouble(3, tx.amount);
                ps.executeUpdate();

                conn.commit();
                undoStack.push(tx);
                redoStack.clear();
                System.out.println(" Transaction successfully pushed to MySQL ledger.");
            } catch (SQLException e) {
                conn.rollback();
                System.err.println(" Atomic processing failed. Rolled back safely.");
            }
        } catch (SQLException e) {
            System.err.println(" Connection layer breakdown: " + e.getMessage());
        }
    }

    private static void undoLastTransaction() {
        if (undoStack.isEmpty()) {
            System.out.println(" Nothing to undo! Stack memory is empty.");
            return;
        }

        TransactionNode lastTx = undoStack.pop();
        String deleteSQL = "DELETE FROM transaction_logs WHERE user_tag = ? AND transaction_type = ? AND amount = ? " +
                "ORDER BY log_id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
                ps.setString(1, lastTx.userTag);
                ps.setString(2, lastTx.type);
                ps.setDouble(3, lastTx.amount);
                ps.executeUpdate();
                conn.commit();

                redoStack.push(lastTx);
                System.out.println(" Undo complete. Removed row from DB.");
            } catch (SQLException e) {
                conn.rollback();
                undoStack.push(lastTx);
                System.err.println(" Undo operation failed at DB layer.");
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static void redoLastTransaction() {
        if (redoStack.isEmpty()) {
            System.out.println("️ Nothing to redo! History buffer tracking is empty.");
            return;
        }

        TransactionNode redoneTx = redoStack.pop();
        String query = "INSERT INTO transaction_logs (user_tag, transaction_type, amount) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, redoneTx.userTag);
                ps.setString(2, redoneTx.type);
                ps.setDouble(3, redoneTx.amount);
                ps.executeUpdate();
                conn.commit();

                undoStack.push(redoneTx);
                System.out.println(" Redo complete. Re-inserted tracking item.");
            } catch (SQLException e) {
                conn.rollback();
                redoStack.push(redoneTx);
                System.err.println(" Redo operation execution failed.");
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static void printLiveDatabaseLedger() {
        String sql = "SELECT * FROM transaction_logs ORDER BY log_id ASC";
        System.out.println("\n--- CURRENT LIVE MYSQL TRANSACTION RECORDS ---");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(" ID: %-3d | User: %-5s | Action: %-6s | Amount: $%7.2f\n",
                        rs.getInt("log_id"),
                        rs.getString("user_tag"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount")
                );
            }
            System.out.println("----------------------------------------------");
        } catch (SQLException e) {
            System.err.println(" Error fetching active metrics: " + e.getMessage());
        }
    }
}
