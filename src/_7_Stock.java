public class _7_Stock extends _7_Asset {
    private int shares;

    public _7_Stock(String name, double price, int shares) {
        super(name, price); // Passes name/price to Parent constructor
        this.shares = shares;
    }

    @Override
    public double calculateValue() {
        return marketPrice * shares; // Specific logic for stocks
    }
}