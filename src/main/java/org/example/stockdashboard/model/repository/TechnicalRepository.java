package org.example.stockdashboard.model.repository;

import java.util.Map;

public interface TechnicalRepository extends JDBCRepository{
    void saveTechnicalIndicator(Map<String, Object> indicators) throws Exception;
    Map<String, Object> getLatestTechnicalIndicator() throws Exception;
}
