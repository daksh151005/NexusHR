package com.nexushr.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank String employeeCode,
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String jobTitle,
        @NotBlank String departmentName,
        @NotBlank String employmentStatus,
        @NotBlank String workLocation,
        @NotNull LocalDate hireDate,
        @Positive BigDecimal baseSalary,
        int engagementScore,
        String skillProfile,
        String documentStatus
) {
}
