package org.example.stockdashboard.service;

import java.util.Map;

public interface TechnicalIndicatorService {
    Map<String, Object> fetchAndUpdateTechnicalIndicators() throws Exception;
    Map<String, Object> getTechnicalIndicators() throws Exception;
}
