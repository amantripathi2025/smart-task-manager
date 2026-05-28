package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.Task;
import com.aman.smart_task_manager.model.TaskStatus;
import com.aman.smart_task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskRepository taskRepository;

    @GetMapping
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        long todo = taskRepository.findByStatus(TaskStatus.TODO).size();
        long inProgress = taskRepository.findByStatus(TaskStatus.IN_PROGRESS).size();
        long done = taskRepository.findByStatus(TaskStatus.DONE).size();
        List<Task> overdue = taskRepository.findOverdueTasks(LocalDateTime.now());

        dashboard.put("total", todo + inProgress + done);
        dashboard.put("todo", todo);
        dashboard.put("inProgress", inProgress);
        dashboard.put("done", done);
        dashboard.put("overdue", overdue.size());
        dashboard.put("overdueTasks", overdue);

        return dashboard;
    }
}