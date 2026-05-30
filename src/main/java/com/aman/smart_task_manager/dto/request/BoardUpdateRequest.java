package com.aman.smart_task_manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardUpdateRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 2000) String description
) {}
