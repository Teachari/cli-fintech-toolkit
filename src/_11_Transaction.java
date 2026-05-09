public class _11_Transaction {
    private String transactionType;
    private double amount;

    public _11_Transaction(String transactionType, double amount) {
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type='" + transactionType + '\'' +
                ", amount=" + amount +
                '}';
    }
}