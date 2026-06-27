package stocktradingplatform;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainFrame extends JFrame {
    private Map<String, Stock> marketStocks = new HashMap<>();
    private User currentUser;
    
    private JLabel balanceLabel, netWorthLabel;
    private JTable marketTable, historyTable;
    private DefaultTableModel marketModel, historyModel;
    private JList<String> performanceList;
    private DefaultListModel<String> performanceListModel;

    // Custom Modern Colors (Pro Dark Terminal Theme)
    private static final Color BG_COLOR = new Color(28, 30, 38);
    private static final Color PANEL_COLOR = new Color(37, 40, 54);
    private static final Color ACCENT_GREEN = new Color(46, 204, 113);
    private final Color ACCENT_RED = new Color(231, 76, 60);
    private static final Color TEXT_LIGHT = new Color(241, 242, 246);
    private static final Color TEXT_MUTED = new Color(164, 176, 190);

    public MainFrame(String authenticatedUser) {
        DatabaseHelper.initializeDatabase();
        currentUser = DatabaseHelper.loadUser(authenticatedUser);
        initializeMockMarketData();

        setTitle("QUANTUM | Modern Trading Terminal - Welcome " + authenticatedUser);
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        // Header Panel Layout
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(53, 59, 81)),
            new EmptyBorder(15, 25, 15, 25)
        ));

        balanceLabel = new JLabel("Available Liquidity: $" + String.format("%.2f", currentUser.getCashBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(ACCENT_GREEN);

        netWorthLabel = new JLabel("Net Portfolio Value: $0.00", JLabel.RIGHT);
        netWorthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        netWorthLabel.setForeground(TEXT_LIGHT);

        headerPanel.add(balanceLabel);
        headerPanel.add(netWorthLabel);
        add(headerPanel, BorderLayout.NORTH);

        // UI Manager Tab Styling
        UIManager.put("TabbedPane.background", BG_COLOR);
        UIManager.put("TabbedPane.foreground", TEXT_LIGHT);
        UIManager.put("TabbedPane.selected", PANEL_COLOR);
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 13));

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainTabs.addTab("  Market Terminal  ", createMarketPanel());
        mainTabs.addTab("  Performance Analytics  ", createPerformancePanel());
        mainTabs.addTab("  Transaction Ledger  ", createHistoryPanel());
        add(mainTabs, BorderLayout.CENTER);

        syncAccountValuation();
        initiateMarketSimulator();
    }

    private void initializeMockMarketData() {
        marketStocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 180.00));
        marketStocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 175.50));
        marketStocks.put("NVDA", new Stock("NVDA", "NVIDIA Corp.", 850.25));
        marketStocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 420.10));
    }

    private JPanel createMarketPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel searchLabel = new JLabel("Search Ticker: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchLabel.setForeground(TEXT_MUTED);
        
        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(PANEL_COLOR);
        searchField.setForeground(TEXT_LIGHT);
        searchField.setCaretColor(TEXT_LIGHT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(53, 59, 81), 1),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] header = {"Ticker", "Company Name", "Market Price", "Holding Volume"};
        marketModel = new DefaultTableModel(header, 0);
        marketTable = createStyledTable(marketModel);
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filterTable() {
                String query = searchField.getText().trim().toUpperCase();
                javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(marketModel);
                marketTable.setRowSorter(sorter);
                if (query.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter(query, 0));
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        JScrollPane scrollPane = new JScrollPane(marketTable);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(53, 59, 81), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionControls = new JPanel();
        actionControls.setBackground(PANEL_COLOR);
        actionControls.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel qtyLabel = new JLabel("Order Quantity: ");
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyLabel.setForeground(TEXT_LIGHT);

        JTextField volumeInput = new JTextField(6);
        volumeInput.setFont(new Font("Segoe UI", Font.BOLD, 14));
        volumeInput.setBackground(BG_COLOR);
        volumeInput.setForeground(TEXT_LIGHT);
        volumeInput.setCaretColor(TEXT_LIGHT);
        volumeInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(53, 59, 81), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JButton triggerBuy = createStyledButton("BUY ASSET", ACCENT_GREEN);
        JButton triggerSell = createStyledButton("SELL ASSET", ACCENT_RED);

        actionControls.add(qtyLabel);
        actionControls.add(volumeInput);
        actionControls.add(Box.createHorizontalStrut(15));
        actionControls.add(triggerBuy);
        actionControls.add(Box.createHorizontalStrut(10));
        actionControls.add(triggerSell);
        panel.add(actionControls, BorderLayout.SOUTH);

        triggerBuy.addActionListener(e -> completeAssetExchange("BUY", volumeInput.getText()));
        triggerSell.addActionListener(e -> completeAssetExchange("SELL", volumeInput.getText()));

        return panel;
    }

    private JPanel createPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Account Net Worth History Snapshot Log:", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_MUTED);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        performanceListModel = new DefaultListModel<>();
        performanceList = new JList<>(performanceListModel);
        performanceList.setFont(new Font("Consolas", Font.PLAIN, 14));
        performanceList.setBackground(PANEL_COLOR);
        performanceList.setForeground(TEXT_LIGHT);
        performanceList.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(performanceList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(53, 59, 81), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        renderPerformanceView();
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel exportControlStrip = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportControlStrip.setBackground(BG_COLOR);
        exportControlStrip.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton exportCsvBtn = createStyledButton("EXPORT TO CSV", new Color(52, 152, 219));
        exportCsvBtn.setPreferredSize(new Dimension(150, 30));
        exportControlStrip.add(exportCsvBtn);
        panel.add(exportControlStrip, BorderLayout.NORTH);

        String[] header = {"Action", "Asset Ticker", "Traded Units", "Execution Value", "Timestamp"};
        historyModel = new DefaultTableModel(header, 0);
        historyTable = createStyledTable(historyModel);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(53, 59, 81), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        exportCsvBtn.addActionListener(e -> triggerCsvExportProcessor());
        
        renderHistoryView();
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        table.setRowHeight(35);
        table.setBackground(PANEL_COLOR);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(new Color(53, 59, 81));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(53, 59, 81));
        table.setSelectionForeground(TEXT_LIGHT);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(BG_COLOR);
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(53, 59, 81)));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        return table;
    }

    private static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 35));
        return btn;
    }

    private void renderMarketView() {
        marketModel.setRowCount(0);
        for (Stock s : marketStocks.values()) {
            int currentInventory = currentUser.getPortfolio().getOrDefault(s.getSymbol(), 0);
            marketModel.addRow(new Object[]{
                s.getSymbol(), 
                s.getName(), 
                String.format("$%.2f", s.getCurrentPrice()), 
                currentInventory
            });
        }
    }

    private void renderHistoryView() {
        historyModel.setRowCount(0);
        for (Transaction t : DatabaseHelper.getTransactionHistory()) {
            historyModel.addRow(new Object[]{
                t.getType(), 
                t.getStockSymbol(), 
                t.getShares(), 
                String.format("$%.2f", t.getPricePerShare()), 
                t.getTimestamp()
            });
        }
    }

    private void renderPerformanceView() {
        performanceListModel.clear();
        for (String logEntry : DatabaseHelper.getPerformanceHistory()) {
            performanceListModel.addElement(logEntry);
        }
    }

    private void completeAssetExchange(String actionType, String orderVolumeRaw) {
        int selectedIndex = marketTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Operation Interrupted: Choose an asset from the table.");
            return;
        }
        try {
            int specifiedVolume = Integer.parseInt(orderVolumeRaw);
            if (specifiedVolume <= 0) throw new NumberFormatException();

            int realModelIndex = marketTable.convertRowIndexToModel(selectedIndex);
            String token = (String) marketModel.getValueAt(realModelIndex, 0);
            
            Stock stockReference = marketStocks.get(token);
            double transactionalWeight = stockReference.getCurrentPrice() * specifiedVolume;
            
            int activeInventory = currentUser.getPortfolio().getOrDefault(token, 0);
            double currentLiquidity = currentUser.getCashBalance();

            if (actionType.equals("BUY")) {
                if (currentLiquidity >= transactionalWeight) {
                    double balancePostTrade = currentLiquidity - transactionalWeight;
                    int unitsPostTrade = activeInventory + specifiedVolume;
                    
                    DatabaseHelper.saveTrade(
                        currentUser.getUsername(), 
                        "BUY", 
                        token, 
                        specifiedVolume, 
                        unitsPostTrade, 
                        balancePostTrade, 
                        stockReference.getCurrentPrice()
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "Order Aborted: Insufficient cash balance.");
                    return;
                }
            } else {
                if (activeInventory >= specifiedVolume) {
                    double balancePostTrade = currentLiquidity + transactionalWeight;
                    int unitsPostTrade = activeInventory - specifiedVolume;
                    
                    DatabaseHelper.saveTrade(
                        currentUser.getUsername(), 
                        "SELL", 
                        token, 
                        specifiedVolume, 
                        unitsPostTrade, 
                        balancePostTrade, 
                        stockReference.getCurrentPrice()
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "Order Aborted: Not enough shares owned.");
                    return;
                }
            }

            currentUser = DatabaseHelper.loadUser(currentUser.getUsername()); 
            balanceLabel.setText("Available Liquidity: $" + String.format("%.2f", currentUser.getCashBalance()));
            renderMarketView();
            renderHistoryView();
            
            double netWorth = calculateCurrentNetWorth();
            DatabaseHelper.logHistoricalSnapshot(netWorth);
            renderPerformanceView();
            
            netWorthLabel.setText("Net Portfolio Value: $" + String.format("%.2f", netWorth));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity: Enter a positive integer value.");
        }
    }

    private double calculateCurrentNetWorth() {
        double inventoryMarketValue = 0;
        for (Map.Entry<String, Integer> trackingRecord : currentUser.getPortfolio().entrySet()) {
            Stock currentStockDetails = marketStocks.get(trackingRecord.getKey());
            if (currentStockDetails != null) {
                inventoryMarketValue += trackingRecord.getValue() * currentStockDetails.getCurrentPrice();
            }
        }
        return currentUser.getCashBalance() + inventoryMarketValue;
    }

    private void syncAccountValuation() {
        double comprehensiveNetWorth = calculateCurrentNetWorth();
        netWorthLabel.setText("Net Portfolio Value: $" + String.format("%.2f", comprehensiveNetWorth));
    }

    private void initiateMarketSimulator() {
        Timer automatedPulse = new Timer(4000, e -> {
            Random assetVarianceGenerator = new Random();
            for (Stock targetStock : marketStocks.values()) {
                double scaleFactor = 0.94 + (0.12 * assetVarianceGenerator.nextDouble()); 
                targetStock.setCurrentPrice(targetStock.getCurrentPrice() * scaleFactor);
            }
            renderMarketView();
            syncAccountValuation();
        });
        automatedPulse.start();
    }

    private void triggerCsvExportProcessor() {
        java.util.List<Transaction> records = DatabaseHelper.getTransactionHistory();
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Export Halted: The ledger database is currently empty.");
            return;
        }

        JFileChooser folderBrowser = new JFileChooser();
        folderBrowser.setDialogTitle("Select Export Save Destination");
        folderBrowser.setSelectedFile(new java.io.File("Trade_Ledger_Audit.csv"));
        
        int interactionOutcome = folderBrowser.showSaveDialog(this);
        if (interactionOutcome == JFileChooser.APPROVE_OPTION) {
            java.io.File destinationPath = folderBrowser.getSelectedFile();
            
            try (java.io.FileWriter streamWriter = new java.io.FileWriter(destinationPath)) {
                streamWriter.write("Action,Asset Ticker,Traded Units,Strike Unit Price,Execution Timestamp\n");
                
                for (Transaction item : records) {
                    streamWriter.write(String.format("%s,%s,%d,%.2f,%s\n",
                        item.getType(), 
                        item.getStockSymbol(), 
                        item.getShares(), 
                        item.getPricePerShare(), 
                        item.getTimestamp()
                    ));
                }
                JOptionPane.showMessageDialog(this, "Success: Transaction ledger exported securely!\nPath: " + destinationPath.getAbsolutePath());
            } catch (java.io.IOException errorEx) {
                JOptionPane.showMessageDialog(this, "File System Error: Failed to compile output file data.");
                errorEx.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame loginWindow = new JFrame("QUANTUM | Access Authentication");
            loginWindow.setSize(380, 240);
            loginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginWindow.setLocationRelativeTo(null);
            loginWindow.getContentPane().setBackground(BG_COLOR);
            loginWindow.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel userLbl = new JLabel("Username:");
            userLbl.setForeground(TEXT_LIGHT);
            gbc.gridx = 0; gbc.gridy = 0;
            loginWindow.add(userLbl, gbc);

            JTextField userTxt = new JTextField(15);
            userTxt.setBackground(PANEL_COLOR);
            userTxt.setForeground(TEXT_LIGHT);
            userTxt.setCaretColor(TEXT_LIGHT);
            gbc.gridx = 1; gbc.gridy = 0;
            loginWindow.add(userTxt, gbc);

            JLabel passLbl = new JLabel("Password:");
            passLbl.setForeground(TEXT_LIGHT);
            gbc.gridx = 0; gbc.gridy = 1;
            loginWindow.add(passLbl, gbc);

            JPasswordField passTxt = new JPasswordField(15);
            passTxt.setBackground(PANEL_COLOR);
            passTxt.setForeground(TEXT_LIGHT);
            passTxt.setCaretColor(TEXT_LIGHT);
            gbc.gridx = 1; gbc.gridy = 1;
            loginWindow.add(passTxt, gbc);

            JButton loginBtn = createStyledButton("SECURE LOGIN", ACCENT_GREEN);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            gbc.insets = new Insets(15, 8, 8, 8);
            loginWindow.add(loginBtn, gbc);

            loginBtn.addActionListener(e -> {
                String inputUser = userTxt.getText().trim();
                String inputPass = new String(passTxt.getPassword());

                if (DatabaseHelper.validateLogin(inputUser, inputPass)) {
                    loginWindow.dispose(); 
                    new MainFrame(inputUser).setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(loginWindow, "Access Denied: Invalid Credentials.", "Auth Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            loginWindow.setVisible(true);
        });
    }
}
