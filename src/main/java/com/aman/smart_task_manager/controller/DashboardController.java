package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.response.DashboardDto;
import com.aman.smart_task_manager.service.CurrentUserService;
import com.aman.smart_task_manager.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    @GetMapping("/{boardId}")
    public DashboardDto getDashboard(@PathVariable Long boardId) {
        return dashboardService.getDashboard(boardId, currentUserService.getCurrentUser());
    }
}
