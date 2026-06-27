USE `trading_platform`
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    PASSWORD VARCHAR(50) NOT NULL,
    balance DOUBLE NOT NULL
);

INSERT IGNORE INTO users (username, PASSWORD, balance) 
VALUES ('dua', '123', 10000.0);
