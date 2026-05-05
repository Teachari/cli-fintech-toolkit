import java.sql.*;
public class _7_InvestmentCLI {
    static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    static final String USER = "root";
    static final String PASS = "Teja@8004"; // Your verified password
    public static void main(String[] args) {
        System.out.println("=== DAY 7: INVESTMENT PORTFOLIO CLI ===\n");
        // 1. JAVA OOPS SECTION (Inheritance & Polymorphism)
        System.out.println("--- Current Live Assets (Local Objects) ---");
        _7_Asset[] localPortfolio = {
                new _7_Stock("Google", 175.00, 10),
                new _7_Asset("Silver Bar", 30.00) // Generic Asset
        };
        for (_7_Asset a : localPortfolio) {
            a.display(); // Polymorphism calls the correct calculateValue
        }
        // 2. DATABASE ANALYTICS SECTION (SQL Aggregates)
        fetchDatabaseSummary();
    }
    public static void fetchDatabaseSummary() {
        // SQL using SUM, AVG, and COUNT[cite: 1]
        String sql = "SELECT COUNT(*) as total_assets, " +
                "SUM(market_price * quantity) as portfolio_value, " +
                "AVG(market_price) as avg_price FROM investments";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("\n---  PORTFOLIO ANALYTICS (FROM SQL) ---");
                System.out.printf("Total Assets Tracked : %d%n", rs.getInt("total_assets"));
                System.out.printf("Total Net Worth      : $%.2f%n", rs.getDouble("portfolio_value"));
                System.out.printf("Average Asset Price  : $%.2f%n", rs.getDouble("avg_price"));
                System.out.println("==========================================");
            }
        } catch (SQLException e) {
            System.out.println(" Database Error: " + e.getMessage());
        }
    }
}