package com.aman.smart_task_manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {
    @NotBlank(message = "Board name is required")
    private String name;
    private String description;
}
