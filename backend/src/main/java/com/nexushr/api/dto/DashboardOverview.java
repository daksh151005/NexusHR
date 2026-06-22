package com.nexushr.api.dto;

import java.util.List;

public record DashboardOverview(
        int totalEmployees,
        int openPositions,
        double attritionRiskPercent,
        double payrollProcessedPercent,
        List<InsightCard> highlights
) {
}
