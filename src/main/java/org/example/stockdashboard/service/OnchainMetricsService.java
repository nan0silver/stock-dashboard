package org.example.stockdashboard.service;

import java.util.Map;

public interface OnchainMetricsService {
    Map<String, Object> fetchAndUpdateOnchainMetrics() throws Exception;
    Map<String, Object> getOnchainMetrics() throws Exception;
}
