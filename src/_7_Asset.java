public class _7_Asset {
    protected String symbol;
    protected double marketPrice;

    public _7_Asset(String symbol, double marketPrice) {
        this.symbol = symbol;
        this.marketPrice = marketPrice;
    }

    // This method will be overridden by children (Polymorphism)
    public double calculateValue() {
        return marketPrice;
    }

    public void display() {
        System.out.printf("Asset: %-10s | Price: $%.2f | Total: $%.2f%n",
                symbol, marketPrice, calculateValue());
    }
}