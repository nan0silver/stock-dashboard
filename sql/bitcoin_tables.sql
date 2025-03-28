CREATE TABLE bitcoin_prices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    price DECIMAL(20, 2) NOT NULL,
    price_change_24h DECIMAL(10, 2),
    timestamp DATETIME NOT NULL
);