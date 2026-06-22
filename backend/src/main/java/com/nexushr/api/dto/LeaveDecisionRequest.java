package com.nexushr.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LeaveDecisionRequest(
        @NotBlank String status,
        String approverComment
) {
}
