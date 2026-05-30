package com.aman.smart_task_manager.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskListUpdateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull @Min(0) Integer position
) {}
