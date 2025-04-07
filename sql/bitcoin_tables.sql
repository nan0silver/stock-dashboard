CREATE TABLE bitcoin_prices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    price DECIMAL(20, 2) NOT NULL,
    price_change_24h DECIMAL(10, 2),
    timestamp DATETIME NOT NULL
);

CREATE TABLE bitcoin_news (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              title VARCHAR(255) NOT NULL,
                              url VARCHAR(512) NOT NULL,
                              source VARCHAR(100),
                              published_at DATETIME NOT NULL,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE bitcoin_news ADD COLUMN sentiment VARCHAR(20);
