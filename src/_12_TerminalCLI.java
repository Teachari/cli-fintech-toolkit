
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Scanner;

public class _12_TerminalCLI {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        _12_InterestCalculator calculator = new _12_InterestCalculator();

        System.out.println("==========================================");
        System.out.println("  TIME-BASED INTEREST CALCULATOR (DAY 12)");
        System.out.println("==========================================");

        System.out.print("Enter user tag to calculate (e.g., teja, suri): ");
        String userTag = sc.next().trim().toLowerCase();

        // 1. Using Optional to check the user history safely
        Optional<LocalDate> firstTxDateOpt = calculator.getFirstTransactionDate(userTag);

        if (firstTxDateOpt.isPresent()) {
            LocalDate firstTxDate = firstTxDateOpt.get();
            LocalDate today = LocalDate.now();

            // 2. Modern java.time API: Calculate days since first transaction
            long daysActive = ChronoUnit.DAYS.between(firstTxDate, today);

            System.out.println("\n Transaction History Found!");
            System.out.println("   First Transaction Date : " + firstTxDate);
            System.out.println("   Days Active            : " + daysActive + " days");

            // 3. Run Stored Procedure (0.05% daily rate)
            double dailyRate = 0.0005;
            double interest = calculator.callInterestProcedure(userTag, dailyRate);

            System.out.println("\n Executing Database Stored Procedure...");
            System.out.printf("   Calculated Interest Earned: $%.2f\n", interest);
            System.out.println("==========================================");
        } else {
            // Safety path: No NullPointerException is thrown!
            System.out.println("\n No transaction records found for user: '" + userTag + "'.");
            System.out.println("==========================================");
        }

        sc.close();
    }
}