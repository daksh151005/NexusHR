package com.nexushr.api.dto;

public record PerformanceReviewView(
        Long id,
        String employeeName,
        String reviewPeriod,
        int managerScore,
        int goalCompletion,
        String performanceBand
) {
}
