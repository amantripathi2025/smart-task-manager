package com.aman.smart_task_manager.dto.response;

import java.time.LocalDateTime;

public record CommentDto(Long id, String content, UserSummaryDto author, Long taskId, LocalDateTime createdAt) {}
