package com.nexushr.api.dto;

public record AttendanceView(
        Long id,
        String employeeName,
        String attendanceDate,
        String checkIn,
        String checkOut,
        String status,
        String source
) {
}
