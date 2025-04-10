package org.example.stockdashboard.model.repository;

import java.util.Map;

public interface OnchainMetricsRepository extends JDBCRepository{
    void saveOnchainMetrics(Map<String, Object> metrics) throws Exception;
    Map<String, Object> getLatestOnchainMetrics() throws Exception;
}
