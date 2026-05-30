package com.aman.smart_task_manager.dto.response;

import java.util.List;

public record DashboardDto(long total, long todo, long inProgress, long done,
                           long overdue, List<TaskDto> overdueTasks) {}
