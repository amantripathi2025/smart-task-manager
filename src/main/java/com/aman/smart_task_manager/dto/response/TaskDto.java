package com.aman.smart_task_manager.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record TaskDto(Long id, String title, String description, String status, String priority,
                      LocalDateTime dueDate, LocalDateTime reminderAt, UserSummaryDto assignee,
                      Long taskListId, Set<LabelDto> labels, LocalDateTime createdAt) {}
