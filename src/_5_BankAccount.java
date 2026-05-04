public class _5_BankAccount {
    // 1. Data Hiding (Private Variables)
    private int accountId;
    private String accountHolder;
    private double balance;

    public _5_BankAccount(int accountId, String accountHolder, double initialBalance) {
        this.accountId = accountId;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    // 2. Safe Access (Getters)
    public int getAccountId() { return accountId; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }

    // 3. Controlled Behavior (Setter Logic)
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println(" Transaction Successful: +$" + amount);
        } else {
            System.out.println(" Error: Deposit must be positive.");
        }
    }

    public void displayProfile() {
        System.out.println("\n---  SECURE ACCOUNT PROFILE ---");
        System.out.println("Account ID : " + accountId);
        System.out.println("Owner      : " + accountHolder);
        System.out.println("Net Worth  : $" + String.format("%.2f", balance));
    }
}