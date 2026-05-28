package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.DashboardStatsDto;
import com.aman.smart_task_manager.model.TaskStatus;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @GetMapping("/boards/{boardId}/stats")
    public DashboardStatsDto getBoardStats(@PathVariable Long boardId) {
        User currentUser = getCurrentUser();
        boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));

        LocalDateTime now = LocalDateTime.now();
        return DashboardStatsDto.builder()
                .totalTasks(taskRepository.countByBoardId(boardId))
                .todoCount(taskRepository.countByBoardIdAndStatus(boardId, TaskStatus.TODO))
                .inProgressCount(taskRepository.countByBoardIdAndStatus(boardId, TaskStatus.IN_PROGRESS))
                .inReviewCount(taskRepository.countByBoardIdAndStatus(boardId, TaskStatus.IN_REVIEW))
                .doneCount(taskRepository.countByBoardIdAndStatus(boardId, TaskStatus.DONE))
                .overdueCount(taskRepository.countOverdueTasksByBoardId(boardId, now))
                .assignedToMeCount(taskRepository.countByBoardIdAndAssigneeId(boardId, currentUser.getId()))
                .overdueToMeCount(taskRepository.countOverdueTasksByBoardIdAndAssigneeId(boardId, currentUser.getId(), now))
                .build();
    }

    private User getCurrentUser() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
