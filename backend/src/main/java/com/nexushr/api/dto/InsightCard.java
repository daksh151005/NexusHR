package com.nexushr.api.dto;

public record InsightCard(
        String title,
        String value,
        String trend,
        String recommendation
) {
}
