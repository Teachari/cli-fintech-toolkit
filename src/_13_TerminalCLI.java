import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.sql.*;

public class _13_TerminalCLI {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = "root";
    private static final String PASS = "Teja@8004";

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    BULK TRANSACTION IMPORTER (DAY 13)  ");
        System.out.println("==========================================");

        // This is your CSV data loaded directly inside the Java code! No external files needed.
        String csvData = "teja,CREDIT,15000.00\n" +
                "suri,DEBIT,-350.00\n" +
                "teja,DEBIT,-1200.00\n" +
                "suri,CREDIT,4500.00\n";

        importBulkData(csvData);
        verifyAuditLogs();
    }

    private static void importBulkData(String rawData) {
        String insertSQL = "INSERT INTO transaction_logs (user_tag, transaction_type, amount) VALUES (?, ?, ?)";

        System.out.println(" Parsing in-memory bulk data...");

        // We use StringReader instead of FileReader for a clean, zero-file approach!
        try (BufferedReader br = new BufferedReader(new StringReader(rawData));
             Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            // Turn off auto-commit for optimal high-speed batch execution
            conn.setAutoCommit(false);

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length == 3) {
                    String userTag = data[0].trim().toLowerCase();
                    String type = data[1].trim().toUpperCase();
                    double amount = Double.parseDouble(data[2].trim());

                    ps.setString(1, userTag);
                    ps.setString(2, type);
                    ps.setDouble(3, amount);

                    ps.addBatch(); // Store in batch memory
                    count++;
                }
            }

            if (count > 0) {
                ps.executeBatch();
                conn.commit(); // Commit all records at once
                System.out.println("⚡ High-speed batch processing complete!");
                System.out.println(" " + count + " records successfully imported into 'transaction_logs'!");
            } else {
                System.out.println(" No valid records found.");
            }

        } catch (IOException e) {
            System.err.println(" String Reader Error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println(" SQL Database Error: " + e.getMessage());
        }
    }

    private static void verifyAuditLogs() {
        String query = "SELECT * FROM transaction_audit_logs ORDER BY performed_at DESC LIMIT 4";

        System.out.println("\n Querying Secure Database Audit Trail...");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n---  AUTOMATIC DATABASE AUDIT SHIELD ---");
            while (rs.next()) {
                System.out.printf(" Audit ID: %-3d | Action: %-11s | User: %-5s | Amount: $%9.2f | Date: %s\n",
                        rs.getInt("audit_id"),
                        rs.getString("action_performed"),
                        rs.getString("user_tag"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("performed_at")
                );
            }
            System.out.println("------------------------------------------");

        } catch (SQLException e) {
            System.err.println(" Audit read error: " + e.getMessage());
        }
    }
}