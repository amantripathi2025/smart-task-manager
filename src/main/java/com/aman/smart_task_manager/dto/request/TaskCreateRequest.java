package com.aman.smart_task_manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public record TaskCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5000) String description,
        String status,
        String priority,
        @NotNull Long taskListId,
        Long assigneeId,
        LocalDateTime dueDate,
        LocalDateTime reminderAt,
        Set<@NotBlank @Size(max = 40) String> labels
) {}
