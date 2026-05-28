package com.aman.smart_task_manager.dto;

import com.aman.smart_task_manager.model.Priority;
import com.aman.smart_task_manager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Task title is required")
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private Long assigneeId;
}
