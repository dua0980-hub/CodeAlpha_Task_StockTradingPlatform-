package stocktradingplatform;

public class Transaction {
    private String type; 
    private String stockSymbol;
    private int shares;
    private double pricePerShare;
    private String timestamp;

    public Transaction(String type, String stockSymbol, int shares, double pricePerShare, String timestamp) {
        this.type = type;
        this.stockSymbol = stockSymbol;
        this.shares = shares;
        this.pricePerShare = pricePerShare;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public String getStockSymbol() { return stockSymbol; }
    public int getShares() { return shares; }
    public double getPricePerShare() { return pricePerShare; }
    public String getTimestamp() { return timestamp; }
}
