import java.util.Scanner;
import java.sql.*;

public class _6_BankingCLI {

    // Database Credentials
    static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    static final String USER = "root";
    static final String PASS = "Teja@8004"; // Update this!

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===  OOPS & DB RELATIONSHIPS ===");
        System.out.print("Enter Account ID (use 101): ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Holder Name: ");
        String name = sc.nextLine();

        System.out.print("Current Bank Balance: ");
        double bal = sc.nextDouble();

        // 1. Create the Java Object (OOPS)
        _5_BankAccount account = new _5_BankAccount(id, name, bal);

        boolean running = true;
        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View Profile");
            System.out.println("2. Deposit Money");
            System.out.println("3. View Linked Loans (DB Sync)");
            System.out.println("4. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    account.displayProfile();
                    break;
                case 2:
                    System.out.print("Amount: ");
                    account.deposit(sc.nextDouble());
                    break;
                case 3:
                    // 2. Use Relationship to fetch data
                    fetchLinkedLoans(account.getAccountId());
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid Option.");
            }
        }
        System.out.println("Session Closed.");
        sc.close();
    }

    public static void fetchLinkedLoans(int userId) {
        String sql = "SELECT loan_id, principal_amount, monthly_emi FROM loans WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n---  LOANS LINKED TO ID: " + userId + " ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("Loan ID: %d | Principal: $%.2f | EMI: $%.2f\n",
                        rs.getInt("loan_id"), rs.getDouble("principal_amount"), rs.getDouble("monthly_emi"));
            }
            if (!found) System.out.println("No loans found for this account.");

        } catch (SQLException e) {
            System.out.println(" Database Error: " + e.getMessage());
        }
    }
}