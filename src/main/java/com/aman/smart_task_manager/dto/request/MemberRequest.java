package com.aman.smart_task_manager.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberRequest(@NotNull Long userId) {}
