package stocktradingplatform;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private double cashBalance;
    private Map<String, Integer> portfolio; 

    public User(String username, double cashBalance) {
        this.username = username;
        this.cashBalance = cashBalance;
        this.portfolio = new HashMap<>();
    }

    public String getUsername() { return username; }
    public double getCashBalance() { return cashBalance; }
    public void setCashBalance(double cashBalance) { this.cashBalance = cashBalance; }
    
    public Map<String, Integer> getPortfolio() { return portfolio; }
    
    public void updatePortfolioShares(String symbol, int shares) {
        if (shares <= 0) {
            portfolio.remove(symbol);
        } else {
            portfolio.put(symbol, shares);
        }
    }
}
