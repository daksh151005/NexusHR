package com.nexushr.api.dto;

import java.math.BigDecimal;

public record PayslipView(
        Long id,
        String employeeName,
        String cycle,
        BigDecimal grossSalary,
        BigDecimal taxDeduction,
        BigDecimal benefitDeduction,
        BigDecimal netSalary,
        String downloadUrl
) {
}
