package org.example.stockdashboard.service;

import java.util.Map;

public interface RiskMetricsService {
    Map<String, Object> fetchAndUpdateRiskMetrics() throws Exception;
    Map<String, Object> getRiskMetrics() throws Exception;
}
