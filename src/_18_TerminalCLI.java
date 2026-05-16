import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class _18_TerminalCLI {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASS = System.getenv("DB_PASS");

    public static void main(String[] args) {
        if (PASS == null || PASS.trim().isEmpty()) {
            System.err.println(" SECURITY ERROR: Environment variable 'DB_PASS' is missing!");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("==========================================");
        System.out.println("  SEARCH & SORT REPORT ENGINE (DAY 18)  ");
        System.out.println("==========================================");

        // 1. Prompt user for full-text search keyword
        System.out.print("Enter search keyword (e.g., office, food, payment): ");
        String keyword = sc.next().trim();

        // 2. Fetch matched rows using MySQL Full-Text Search
        List<_18_ReportTransaction> records = fetchSearchResults(keyword);

        if (records.isEmpty()) {
            System.out.println(" No transactions matched that keyword.");
            sc.close();
            return;
        }

        // 3. Prompt user for memory sorting preference
        System.out.println("\nSelect Sorting Rule for Report:");
        System.out.println("1. Sort by Amount (Highest to Lowest)");
        System.out.println("2. Sort by User Tag (Alphabetical)");
        System.out.print("Choice: ");
        int choice = sc.nextInt();

        // 4. Run Java sorting algorithms based on preference
        if (choice == 1) {
            // Sort descending by amount
            records.sort(Comparator.comparingDouble(_18_ReportTransaction::getAmount).reversed());
            System.out.println("\n --- REPORT SORTED BY AMOUNT (DESCENDING) ---");
        } else {
            // Sort ascending by user alphabetical tag
            records.sort(Comparator.comparing(_18_ReportTransaction::getUserTag));
            System.out.println("\n --- REPORT SORTED BY USERNAME (A-Z) ---");
        }

        // 5. Output sorted data report
        for (_18_ReportTransaction tx : records) {
            System.out.println(tx);
        }
        System.out.println("------------------------------------------------");
        sc.close();
    }

    private static List<_18_ReportTransaction> fetchSearchResults(String keyword) {
        List<_18_ReportTransaction> list = new ArrayList<>();

        // Modernized SQL query using MATCH() AGAINST() Full-Text Syntax
        String sql = "SELECT log_id, user_tag, amount, description FROM transaction_logs " +
                "WHERE MATCH(description) AGAINST(? IN NATURAL LANGUAGE MODE)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new _18_ReportTransaction(
                            rs.getInt("log_id"),
                            rs.getString("user_tag"),
                            rs.getDouble("amount"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println(" Database extraction error: " + e.getMessage());
        }
        return list;
    }
}