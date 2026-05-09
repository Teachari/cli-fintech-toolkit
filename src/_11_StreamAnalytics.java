import java.util.List;
import java.util.stream.Collectors;

public class _11_StreamAnalytics {

    public static void runAnalytics(List<_11_Transaction> transactions) {
        System.out.println("\n==================================================");
        System.out.println("  ⚡ REAL-TIME ANALYTICS ENGINE (JAVA 8 STREAMS)   ");
        System.out.println("==================================================");

        // 1. FILTER USING LAMBDA: Get a list of all DEBIT amounts
        List<Double> debitsOnly = transactions.stream()
                .filter(t -> t.getTransactionType().equalsIgnoreCase("DEBIT")) // Lambda
                .map(_11_Transaction::getAmount)                             // Method Reference
                .collect(Collectors.toList());

        System.out.println("  Filtered Debit Transactions : " + debitsOnly);

        // 2. REDUCTION USING STREAM: Find the largest single transaction (absolute volume)
        double maxTransaction = transactions.stream()
                .mapToDouble(t -> Math.abs(t.getAmount()))
                .max()
                .orElse(0.0);

        System.out.println("  Peak Transaction Volume     : $" + maxTransaction);

        // 3. SUM USING STREAM: Sum up all CREDIT transactions
        double totalCredits = transactions.stream()
                .filter(t -> t.getTransactionType().equalsIgnoreCase("CREDIT"))
                .mapToDouble(_11_Transaction::getAmount)
                .sum();

        System.out.println("  Total Deposited Credits     : $" + totalCredits);
        System.out.println("==================================================\n");
    }
}