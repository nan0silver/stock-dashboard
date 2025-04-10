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

-- technical_indicators 테이블
CREATE TABLE technical_indicators (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      rsi DOUBLE,
                                      macd VARCHAR(20),
                                      bollinger_bands VARCHAR(50),
                                      moving_average_200d VARCHAR(20),
                                      volatility_30d DOUBLE,
                                      timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- onchain_metrics 테이블
CREATE TABLE onchain_metrics (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 active_wallets INT,
                                 avg_transaction_fee DOUBLE,
                                 mining_difficulty VARCHAR(20),
                                 hash_rate VARCHAR(50),
                                 timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- risk_metrics 테이블
CREATE TABLE risk_metrics (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              fear_greed_index INT,
                              market_sentiment VARCHAR(50),
                              volatility_30d DOUBLE,
                              price_change_risk VARCHAR(20),
                              market_health VARCHAR(20),
                              timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);