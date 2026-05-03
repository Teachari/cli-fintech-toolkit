import java.sql.*;
import java.util.Scanner;

public class _4_LoanCalculator {

    // Database Credentials
    static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    static final String USER = "root";
    static final String PASS = "Teja@8004"; // Change to your actual password

    // 1. METHOD: High-Precision EMI Calculation
    public static double calculateEMI(double p, double r, int t) {
        double monthlyRate = r / 12 / 100;
        int months = t * 12;
        return (p * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);
    }

    // 2. RECURSION: Monthly Amortization Schedule
    public static void printAndCalcSchedule(int month, double balance, double annualRate, double emi, int totalMonths) {
        if (month > totalMonths || balance <= 0) {
            System.out.println("-------------------------------------------------------------");
            return;
        }

        double monthlyInterest = balance * (annualRate / 12 / 100);
        double principalPaid = emi - monthlyInterest;
        double newBalance = Math.max(0, balance - principalPaid);

        System.out.printf("| %-4d | $%10.2f | $%10.2f | $%10.2f | $%10.2f |\n",
                month, balance, monthlyInterest, principalPaid, newBalance);

        printAndCalcSchedule(month + 1, newBalance, annualRate, emi, totalMonths);
    }

    // 3. JDBC: Save Loan to the linked 'loans' table
    public static void saveLoanToDatabase(int userId, double p, double r, double emi, double total) {
        String sql = "INSERT INTO loans (user_id, principal_amount, annual_rate, monthly_emi, total_repayment) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDouble(2, p);
            pstmt.setDouble(3, r);
            pstmt.setDouble(4, emi);
            pstmt.setDouble(5, total);

            pstmt.executeUpdate();
            System.out.println("\n Success: Loan record synchronized with User ID: " + userId);

        } catch (SQLException e) {
            System.out.println("\n❌ DB Error (Ensure 'accounts' table has ID " + userId + "): " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===  DAY 5: SYNCHRONIZED LOAN ENGINE ===");

        // Step A: Link to User
        System.out.print("Enter existing User ID from 'accounts' table: ");
        int userId = sc.nextInt();

        // Step B: Collect Loan Data
        System.out.print("Enter Loan Amount: ");
        double principal = sc.nextDouble();
        System.out.print("Enter Annual Interest Rate (%): ");
        double rate = sc.nextDouble();
        System.out.print("Enter Duration (Years): ");
        int years = sc.nextInt();

        // Step C: Execute Logic
        double emi = calculateEMI(principal, rate, years);
        int totalMonths = years * 12;
        double totalRepayment = emi * totalMonths;

        System.out.println("\n---  AMORTIZATION SCHEDULE ---");
        System.out.println("-------------------------------------------------------------");
        System.out.println("| Month|   Balance  |  Interest  |  Principal | New Balance |");
        System.out.println("-------------------------------------------------------------");

        printAndCalcSchedule(1, principal, rate, emi, totalMonths);

        // Step D: Database Synchronization
        System.out.println("\nFixed Monthly Payment (EMI): $" + String.format("%.2f", emi));
        System.out.print("Do you want to save this loan to the database? (yes/no): ");
        String save = sc.next();

        if (save.equalsIgnoreCase("yes")) {
            saveLoanToDatabase(userId, principal, rate, emi, totalRepayment);
        }

        System.out.println("Session Complete.");
        sc.close();
    }
}