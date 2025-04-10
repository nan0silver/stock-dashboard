package org.example.stockdashboard.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.stockdashboard.util.DotenvMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig implements DotenvMixin {

    @Bean
    public DataSource dataSource() {
        try {
            // 드라이버 명시적 등록
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL"));
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASSWORD"));

            // 성능 최적화를 위한 설정
            config.setMaximumPoolSize(20);             // 최대 연결 수 증가
            config.setMinimumIdle(10);                 // 최소 유휴 연결 수 증가
            config.setConnectionTimeout(10000);        // 연결 타임아웃 10초로 감소
            config.setIdleTimeout(60000);              // 유휴 연결 제거 시간 60초로 감소
            config.setMaxLifetime(1800000);            // 최대 수명 30분 유지
            config.setLeakDetectionThreshold(60000);   // 연결 누수 감지 시간 60초

            // 성능 향상을 위한 추가 설정
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");// 30초

            return new HikariDataSource(config);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }
}