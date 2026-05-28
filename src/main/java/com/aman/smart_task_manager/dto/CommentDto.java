package com.aman.smart_task_manager.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private String content;
    private UserDto author;
    private Long taskId;
    private LocalDateTime createdAt;
}
