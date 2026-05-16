public class _18_ReportTransaction {
    private final int id;
    private final String userTag;
    private final double amount;
    private final String description;

    public _18_ReportTransaction(int id, String userTag, double amount, String description) {
        this.id = id;
        this.userTag = userTag;
        this.amount = amount;
        this.description = description;
    }

    public int getId() { return id; }
    public String getUserTag() { return userTag; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format(" ID: %-3d | User: %-5s | Amount: $%9.2f | Note: %s",
                id, userTag, amount, description);
    }
}