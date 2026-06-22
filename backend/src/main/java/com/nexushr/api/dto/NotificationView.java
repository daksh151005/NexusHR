package com.nexushr.api.dto;

public record NotificationView(
        Long id,
        String channel,
        String recipient,
        String subject,
        String body,
        String status,
        String createdAt
) {
}
