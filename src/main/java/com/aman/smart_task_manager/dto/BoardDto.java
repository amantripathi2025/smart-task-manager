package com.aman.smart_task_manager.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    private String name;
    private String description;
    private UserDto owner;
    private Set<UserDto> members;
    private LocalDateTime createdAt;
}
