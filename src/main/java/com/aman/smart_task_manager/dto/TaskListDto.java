package com.aman.smart_task_manager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskListDto {
    private Long id;
    private String name;
    private Integer position;
    private Long boardId;
}
