package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.response.DashboardDto;
import com.aman.smart_task_manager.dto.response.TaskDto;
import com.aman.smart_task_manager.model.TaskStatus;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TaskRepository taskRepository;
    private final DtoMapper mapper;
    private final AuthorizationService authorizationService;

    public DashboardDto getDashboard(Long boardId, User user) {
        authorizationService.requireBoardAccess(boardId, user);
        Map<TaskStatus, Long> statusMap = new EnumMap<>(TaskStatus.class);
        taskRepository.countByStatusForBoard(boardId)
                .forEach(row -> statusMap.put((TaskStatus) row[0], (Long) row[1]));

        long todo = statusMap.getOrDefault(TaskStatus.TODO, 0L);
        long inProgress = statusMap.getOrDefault(TaskStatus.IN_PROGRESS, 0L);
        long done = statusMap.getOrDefault(TaskStatus.DONE, 0L);
        long overdue = taskRepository.countOverdue(boardId, LocalDateTime.now());
        List<TaskDto> overdueTasks = taskRepository.findOverdueTasks(boardId, LocalDateTime.now()).stream()
                .map(mapper::toTaskDto).toList();

        return new DashboardDto(todo + inProgress + done, todo, inProgress, done, overdue, overdueTasks);
    }
}
