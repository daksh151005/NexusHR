package com.nexushr.api.dto;

public record EmployeeSummary(
        Long id,
        String name,
        String role,
        String department,
        String status,
        String location,
        int engagementScore
) {
}
