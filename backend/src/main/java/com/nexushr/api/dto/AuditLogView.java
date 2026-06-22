package com.nexushr.api.dto;

public record AuditLogView(
        Long id,
        String actor,
        String action,
        String entityType,
        String entityId,
        String createdAt
) {
}
