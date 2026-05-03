import java.util.Scanner;

public class LoanCalculator {

    // 1. METHOD: The EMI Formula
    // Purpose: Uses standard methods to return a specific decimal value.
    public static double calculateEMI(double principal, double annualRate, int years) {
        // Convert annual rate to monthly decimal (e.g., 5% -> 0.05 / 12)
        double monthlyRate = annualRate / 12 / 100;
        // Total number of payment months
        int totalMonths = years * 12;

        // Formula: [P x r x (1+r)^n] / [(1+r)^n - 1]
        double numerator = principal * monthlyRate * Math.pow(1 + monthlyRate, totalMonths);
        double denominator = Math.pow(1 + monthlyRate, totalMonths) - 1;

        return numerator / denominator;
    }

    // 2. RECURSION: Total Interest Accumulator
    // Purpose: Demonstrates recursion by breaking down interest month by month.
    public static double calculateTotalInterest(double balance, double annualRate, int monthsRemaining) {
        // Base Case: When time runs out, recursion stops
        if (monthsRemaining == 0) {
            return 0;
        }

        // Calculate interest for the current month
        double monthlyInterest = balance * (annualRate / 12 / 100);

        // Recursive Step: Add this month's interest to the interest of the remaining months
        return monthlyInterest + calculateTotalInterest(balance, annualRate, monthsRemaining - 1);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=======================================");
        System.out.println("    FINTECH TOOLKIT: LOAN ENGINE    ");
        System.out.println("=======================================");

        // Step 1: User Input with Precision
        System.out.print("Enter Loan Amount (Principal): ");
        double p = sc.nextDouble();

        System.out.print("Enter Annual Interest Rate (%): ");
        double r = sc.nextDouble();

        System.out.print("Enter Loan Duration (Years): ");
        int t = sc.nextInt();

        // Step 2: Processing using Day 5 Morning Skills
        double emi = calculateEMI(p, r, t);
        double totalInterest = calculateTotalInterest(p, r, t * 12);
        double totalRepayment = p + totalInterest;

        // Step 3: High-Precision CLI Output
        System.out.println("\n---  TRANSACTION SUMMARY ---");
        System.out.printf("Monthly Installment (EMI): $%.2f\n", emi);
        System.out.printf("Total Interest Charged:    $%.2f\n", totalInterest);
        System.out.printf("Total Amount to Repay:     $%.2f\n", totalRepayment);
        System.out.println("------------------------------");

        System.out.println("\n Precision logic completed successfully.");

        sc.close();
    }
}