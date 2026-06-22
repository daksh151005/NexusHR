package com.nexushr.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PayrollRequest(@NotBlank String cycle) {
}
