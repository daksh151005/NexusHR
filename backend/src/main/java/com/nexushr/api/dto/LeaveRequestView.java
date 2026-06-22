package com.nexushr.api.dto;

public record LeaveRequestView(
        Long id,
        String employeeName,
        String leaveType,
        String startDate,
        String endDate,
        String status
) {
}
