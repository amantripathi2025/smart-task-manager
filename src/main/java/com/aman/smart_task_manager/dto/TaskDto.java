package com.aman.smart_task_manager.dto;

import com.aman.smart_task_manager.model.Priority;
import com.aman.smart_task_manager.model.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private UserDto assignee;
    private Long taskListId;
    private LocalDateTime createdAt;
}
