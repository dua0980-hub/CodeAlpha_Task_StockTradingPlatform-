# Modern Real-Time Stock Trading Platform

![Java](https://shields.io)
![MySQL](https://shields.io)
![NetBeans](https://shields.io)

An enterprise-grade, high-performance **Real-Time Stock Trading Simulator** developed using **Java Swing (GUI)** and fully backed by a **MySQL Database Architecture**. This platform simulates a volatile live stock market ecosystem, allowing users to practice portfolio management, execution order routing, and asset timeline tracking without financial risk.

---

## 🎨 Premium Dark UI Design Overview

- **Pro-Trading Console Theme:** Engineered using a sleek, low-fatigue dark material palette (`#1C1E26`).
- **Interactive Multi-Tab Layout:** Clean navigation tabs dividing the workspace into *Market Terminal*, *Performance Analytics*, and *Transaction Ledgers*.
- **Visual Alert Indicators:** Action items, funds, and button profiles color-coded with semantic alerts (Crimson Red for selling, Emerald Green for buying).

---

## ⚡ Core Architecture & Functional Capabilities

- **🔄 Automated Volatility Engine:** Background execution handler loops shifting global share values randomly within a +/-6% index spread every 4 seconds.
- **🛡️ Atomic Transaction Safeguards:** Automated data validation routines block fraudulent trades like account overdrafts or short-selling nonexistent stock volumes.
- **🔍 No-Overhead Real-Time Row Filtering:** RegEx pattern-matching sorters filtering tabular records seamlessly directly inside the UI thread without adding connection overhead to the DB.
- **📊 Chronological Net Worth Auditing:** Captures user wealth snapshots over time to chart net portfolio valuations within analytical timelines.
- **📥 Java File I/O CSV Pipeline:** Direct-stream file extraction utilities exporting data records directly into a native `.csv` document readable in Microsoft Excel.

---

## 📂 Object-Oriented Directory Architecture

The system splits system responsibilities cleanly following proper OOP design pattern components:

```text
StockTradingPlatform/
│
├── src/stocktradingplatform/
│   ├── MainFrame.java          # Core Application Entry Window & GUI Renderer
│   ├── DatabaseHelper.java      # MySQL JDBC Driver Transaction Connection Handler
│   ├── User.java                # OOP Representation of Account Liquid Resource Profiles
│   ├── Stock.java               # OOP Domain Model Tracking Real-Time Volatile Pricing Indexes
│   └── Transaction.java         # Immutable Domain Model Preserving Historical Execution Data
│
└── Libraries/
    └── mysql-connector-java-5.1.49.jar # Required Communication Database Driver
```

---

## ⚙️ Quick Installation Setup Guide

### 1. Initialize the SQLyog Database Schema
Connect to your local MySQL instance using your client manager and execute this blueprint schema initialization script:

```sql
CREATE DATABASE IF NOT EXISTS trading_platform;
USE trading_platform;

CREATE TABLE IF NOT EXISTS user_balance (
    id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    balance DOUBLE NOT NULL
);
INSERT IGNORE INTO user_balance (id, username, balance) VALUES (1, 'DefaultUser', 10000.0);

CREATE TABLE IF NOT EXISTS portfolio (
    symbol VARCHAR(10) PRIMARY KEY,
    shares INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    shares INT NOT NULL,
    price DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS portfolio_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_value DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Configure Local Connection Credentials
Open `DatabaseHelper.java` inside your NetBeans project and map your local system password properties:
```java
private static final String URL = "jdbc:mysql://localhost:3306/trading_platform?useSSL=false";
private static final String USER = "root";       
private static final String PASSWORD = "dua123"; 
```

### 3. Build & Deploy Inside NetBeans
1. Clone or copy these source repositories into a clean NetBeans workspace.
2. Right-click your project root **Libraries** folder tree icon -> **Add JAR/Folder**.
3. Select your local path directory pointing directly to `mysql-connector-java-5.1.49.jar`.
4. Right-click on **`MainFrame.java`** and click **Run File** (`Shift + F6`) to deploy the live application window instance.

