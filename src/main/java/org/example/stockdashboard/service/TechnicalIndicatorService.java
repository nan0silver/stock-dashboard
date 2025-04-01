package org.example.stockdashboard.service;

import java.util.Map;

public interface TechnicalIndicatorService {
    Map<String, Object> getTechnicalIndicators() throws Exception;
    Map<String, Object> getOnchainMetrics() throws Exception;
    Map<String, Object> getRiskMetrics() throws Exception;
}
