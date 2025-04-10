package org.example.stockdashboard.model.repository;

import java.util.Map;

public interface RiskMetricsRepository extends JDBCRepository{
    void saveRiskMetrics(Map<String, Object> indicators) throws Exception;
    Map<String, Object> getLatestRiskMetrics() throws Exception;
}
