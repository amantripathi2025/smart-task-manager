package com.aman.smart_task_manager.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record BoardDto(Long id, String name, String description, UserSummaryDto owner,
                       Set<UserSummaryDto> members, LocalDateTime createdAt) {}
