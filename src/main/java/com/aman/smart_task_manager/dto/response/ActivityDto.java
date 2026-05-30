package com.aman.smart_task_manager.dto.response;

import java.time.LocalDateTime;

public record ActivityDto(Long id, String action, String entityType, Long entityId,
                          String message, UserSummaryDto actor, LocalDateTime createdAt) {}
