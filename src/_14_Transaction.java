public class _14_Transaction {
    private final String userTag;
    private final String type;
    private final double amount;

    public _14_Transaction(String userTag, String type, double amount) {
        this.userTag = userTag != null ? userTag.trim().toLowerCase() : "";
        this.type = type != null ? type.trim().toUpperCase() : "";
        this.amount = amount;
    }

    public String getUserTag() {
        return userTag;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}