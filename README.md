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

✨ Features

- 🌙 Modern Dark Theme User Interface
- 📊 Interactive GUI using Java Swing
- 💹 Buy and Sell Stocks
- 💼 Portfolio Management
- 💰 Account Balance Tracking
- ⚡ Real-time Transaction Updates
- 🖱️ User-Friendly Navigation
- 🛡️ Input Validation and Error Handling

🛠️ Technologies Used

- Java
- Java Swing
- Object-Oriented Programming (OOP)
- Event-Driven Programming

📂 Project Structure

StockTradingPlatform/
│── src/
│── assets/
│── README.md

🚀 Getting Started

1. Clone the repository.
2. Open the project in your preferred Java IDE (IntelliJ IDEA, Eclipse, or NetBeans).
3. Build and run the project.
4. Start exploring the Stock Trading Platform.

🎯 Learning Outcomes

This project enhanced my understanding of:

- Java GUI Development
- Object-Oriented Programming
- Event Handling
- Data Management
- Desktop Application Development

👩‍💻 Author

Dua Burfat 
IT Student At University Of Sindh

Java Developer 

📄 License

This project is developed for educational and learning purposes.
LinkedIn :https://www.linkedin.com/in/dua-burfat-769578390/⁠
