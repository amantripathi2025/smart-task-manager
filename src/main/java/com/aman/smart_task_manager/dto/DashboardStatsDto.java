package com.aman.smart_task_manager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private Long totalTasks;
    private Long todoCount;
    private Long inProgressCount;
    private Long inReviewCount;
    private Long doneCount;
    private Long overdueCount;
    private Long assignedToMeCount;
    private Long overdueToMeCount;
}
