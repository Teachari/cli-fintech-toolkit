import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class _13_BulkImporter {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = "root";
    private static final String PASS = "Teja@8004";

    public void importCSV(String csvFilePath) {
        // SQL query to insert logs
        String insertSQL = "INSERT INTO transaction_logs (user_tag, transaction_type, amount) VALUES (?, ?, ?)";

        System.out.println("📂 Opening file reader for: " + csvFilePath);

        // Use try-with-resources to automatically close file readers and database connections
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
             Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            // Turn off auto-commit to execute this as one single high-speed database transaction
            conn.setAutoCommit(false);

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                // Skip empty lines or header lines if present
                if (line.trim().isEmpty() || line.startsWith("UserTag")) {
                    continue;
                }

                // Parse CSV columns by split-comma delimiter
                String[] data = line.split(",");
                if (data.length == 3) {
                    String userTag = data[0].trim().toLowerCase();
                    String type = data[1].trim().toUpperCase();
                    double amount = Double.parseDouble(data[2].trim());

                    // Bind fields to our SQL statement
                    ps.setString(1, userTag);
                    ps.setString(2, type);
                    ps.setDouble(3, amount);

                    // Add parameters to memory batch
                    ps.addBatch();
                    count++;
                }
            }

            // Execute all buffered insert operations at once
            if (count > 0) {
                ps.executeBatch();
                conn.commit(); // Commit the transaction to save data
                System.out.println(" High-speed batch processing complete!");
                System.out.println(" " + count + " records successfully imported into 'transaction_logs'!");
            } else {
                System.out.println(" No valid records found in file.");
            }

        } catch (IOException e) {
            System.err.println(" File IO Error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println(" SQL Database Error: " + e.getMessage());
        }
    }
}