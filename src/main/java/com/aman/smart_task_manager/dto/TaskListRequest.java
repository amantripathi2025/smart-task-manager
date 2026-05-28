package com.aman.smart_task_manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListRequest {
    @NotBlank(message = "Task list name is required")
    private String name;
    private Integer position;
}
