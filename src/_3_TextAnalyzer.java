import java.sql.*;
import java.util.Scanner;

public class _3_TextAnalyzer {

    // 1. DATABASE CREDENTIALS (Update these to match your workbench)
    static final String DB_URL = "jdbc:mysql://localhost:3306/fintech_db";
    static final String USER = "root";
    static final String PASS = "Teja@8004";

    // 2. CONNECTION FUNCTION (Afternoon Task)
    public static Connection connectDB() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // 3. INTENT ANALYSIS FUNCTION (Morning Task)
    public static String getDetectedIntent(String userInput) {
        if (userInput == null) return "unknown";

        String[] words = userInput.toLowerCase().split(" ");
        String[] keywords = {"deposit", "withdraw", "balance", "transfer"};
        String detectedIntent = "unknown";

        for (String word : words) {
            for (String key : keywords) {
                if (word.equals(key)) {
                    detectedIntent = key;
                }
            }
        }
        return detectedIntent;
    }

    // 4. DATABASE LOGGING FUNCTION (Evening Task)
    // This is what prevents the "NULL" columns you see in image_027252.png
    public static void logToDatabase(String rawText, String intent) {
        String sql = "INSERT INTO analysis_logs (raw_text, detected_intent) VALUES (?, ?)";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rawText);
            pstmt.setString(2, intent);
            pstmt.executeUpdate();

            System.out.println("✅ Database Updated: Sentence and Intent saved.");

        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }

    // 5. CLEANUP FUNCTION (Afternoon Task)
    public static void cleanupLogs() {
        String sql = "DELETE FROM analysis_logs WHERE detected_intent = 'unknown'";

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("🧹 Cleanup: Removed " + rowsDeleted + " unknown entries.");

        } catch (SQLException e) {
            System.out.println("❌ Cleanup Failed.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Day 4 Final: CLI Fintech Toolkit ===");
        System.out.println("1. Analyze Text & Save to DB");
        System.out.println("2. Run DB Cleanup (DELETE)");
        System.out.print("Choose: ");

        int choice = sc.nextInt();
        sc.nextLine(); // Clear the buffer

        if (choice == 1) {
            System.out.print("Enter your request: ");
            String input = sc.nextLine();

            // STEP A: Analyze (Morning)
            String action = getDetectedIntent(input);
            System.out.println("Detected: " + action);

            // STEP B: Log (Evening) - This fills the rows in your SQL table
            logToDatabase(input, action);

        } else if (choice == 2) {
            cleanupLogs();
        }

        sc.close();
    }
}