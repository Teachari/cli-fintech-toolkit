// Implementing Comparable allows PriorityQueue to sort these objects automatically
public class _16_PaymentTask implements Comparable<_16_PaymentTask> {
    private final String userTag;
    private final double amount;
    private final int priorityLevel; // 3 = High/Premium, 2 = Medium, 1 = Low/Standard

    public _16_PaymentTask(String userTag, double amount, int priorityLevel) {
        this.userTag = userTag.trim().toLowerCase();
        this.amount = amount;
        this.priorityLevel = priorityLevel;
    }

    public String getUserTag() { return userTag; }
    public double getAmount() { return amount; }
    public int getPriorityLevel() { return priorityLevel; }

    @Override
    public int compareTo(_16_PaymentTask other) {
        // Sort in descending order (highest priority value processed first)
        return Integer.compare(other.priorityLevel, this.priorityLevel);
    }
}