import java.sql.*;
import java.util.Scanner;

public class GuessingGame {
    public static void main(String[] args) {
        // 1. Connection Setup (Use your specific password)
        String url = "jdbc:mysql://localhost:3306/fintech_db";
        String user = "root";
        String password = "Teja@8004";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Scanner sc = new Scanner(System.in);

            // 2. Initialize Game State
            int secretNum = (int)(Math.random() * 100) + 1;
            int maxAttempts = 5;
            int attemptsUsed = 0;
            boolean hasWon = false;

            System.out.println("--- 🎰 Day 3 Project: Vault Guessing Game ---");
            System.out.print("Enter Player Name: ");
            String playerName = sc.nextLine();

            // 3. The Control Flow Loop (The Core of Day 3)
            for (int i = 1; i <= maxAttempts; i++) {
                attemptsUsed = i;
                System.out.print("\nAttempt " + i + "/" + maxAttempts + ": Guess the number (1-100): ");

                // DAY 3 ARMOR: Input Validation
                if (!sc.hasNextInt()) {
                    System.out.println("⚠️ ERROR: That's not a number! Attempt wasted.");
                    sc.next(); // Clear the buffer
                    continue;
                }

                int guess = sc.nextInt();

                // Branching Logic
                if (guess == secretNum) {
                    System.out.println("🎯 SUCCESS! You cracked the vault code!");
                    hasWon = true;
                    break; // Exit loop early on win
                } else if (guess < secretNum) {
                    System.out.println("Hint: Too Low!");
                } else {
                    System.out.println("Hint: Too High!");
                }
            }

            // 4. Persistence Logic: Save Result to MySQL
            String status = hasWon ? "WIN" : "LOSS";
            if (!hasWon) System.out.println("\n💀 Game Over. The code was: " + secretNum);

            String sql = "INSERT INTO game_scores (player_name, secret_number, attempts_used, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, playerName);
                pstmt.setInt(2, secretNum);
                pstmt.setInt(3, attemptsUsed);
                pstmt.setString(4, status);
                pstmt.executeUpdate();
                System.out.println("💾 Game result synchronized with the Vault.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database Connection Failed!");
            e.printStackTrace();
        }
    }
}