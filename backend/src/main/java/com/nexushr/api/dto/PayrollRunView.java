package com.nexushr.api.dto;

import java.math.BigDecimal;

public record PayrollRunView(
        Long id,
        String cycle,
        BigDecimal grossPayroll,
        BigDecimal netPayroll,
        int processedEmployees,
        String status
) {
}
