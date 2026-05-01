import java.sql.*;
import java.util.Scanner;

public class _1_Main {
    public static void main(String[] args) {
        // Database credentials - Update with your actual password
        String url = "jdbc:mysql://localhost:3306/fintech_db";
        String user = "root";
        String password = "Teja@8004";

        // Step 1: Open the connection OUTSIDE the loop for efficiency
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Scanner sc = new Scanner(System.in);
            System.out.println("--- 🏦 FinTech CLI Toolkit: Day 3 Service Active ---");

            // Step 2: The Morning "Service Engine" Loop
            while (true) {
                System.out.println("\n[Main Menu] Enter a number or type 'exit' to secure the vault.");
                System.out.print("> ");

                // Check for Sentinel Value (Exit)
                if (sc.hasNext("exit")) {
                    break;
                }

                // Step 3: Armor - Validate first number
                if (!sc.hasNextDouble()) {
                    System.out.println("❌ ERROR: Invalid input. Please enter a numeric value.");
                    sc.next(); // Clear the bad input from the buffer
                    continue;  // Restart loop
                }
                double n1 = sc.nextDouble();

                System.out.print("Enter operation (+, -, *, /): ");
                String op = sc.next();

                System.out.print("Enter second number: ");
                // Armor - Validate second number
                if (!sc.hasNextDouble()) {
                    System.out.println("❌ ERROR: Second input must be a number.");
                    sc.next();
                    continue;
                }
                double n2 = sc.nextDouble();

                // Step 4: Logic Branching (Day 2 Review)
                double res = 0;
                boolean validOp = true;

                if (op.equals("+")) res = n1 + n2;
                else if (op.equals("-")) res = n1 - n2;
                else if (op.equals("*")) res = n1 * n2;
                else if (op.equals("/")) {
                    if (n2 == 0) {
                        System.out.println("❌ ERROR: Cannot divide by zero.");
                        validOp = false;
                    } else {
                        res = n1 / n2;
                    }
                } else {
                    System.out.println("❌ ERROR: Unknown operator '" + op + "'");
                    validOp = false;
                }

                if (validOp) {
                    System.out.println("✅ Result: " + res);

                    // Step 5: SQL Persistence - Push to Vault
                    String sql = "INSERT INTO calc_history (operation, num1, num2, result) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, op);
                        pstmt.setDouble(2, n1);
                        pstmt.setDouble(3, n2);
                        pstmt.setDouble(4, res);
                        pstmt.executeUpdate(); // The "Push"
                        System.out.println("💾 Transaction recorded in vault.");
                    }
                }
            } // End of While Loop

            System.out.println("🔒 Vault locked. Session ended. Goodbye!");
            sc.close();

        } catch (SQLException e) {
            System.out.println("❌ DATABASE ERROR: Check your connection string or password.");
            e.printStackTrace();
        }
    }
}