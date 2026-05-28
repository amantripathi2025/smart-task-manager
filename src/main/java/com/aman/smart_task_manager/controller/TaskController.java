package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.*;
import com.aman.smart_task_manager.repository.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    @Data
    static class TaskRequest {
        String title, description, status, priority;
        Long taskListId, assigneeId;
        LocalDateTime dueDate;
    }

    @GetMapping("/board/{boardId}")
    public List<Task> getTasksByBoard(@PathVariable Long boardId,
                                      @RequestParam(required = false) String status) {
        if (status != null) {
            return taskRepository.findByBoardIdAndStatus(boardId, TaskStatus.valueOf(status));
        }
        return taskRepository.findByBoardId(boardId);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest req) {
        return taskListRepository.findById(req.taskListId).map(list -> {
            Task task = Task.builder()
                    .title(req.title)
                    .description(req.description)
                    .taskList(list)
                    .dueDate(req.dueDate)
                    .build();
            if (req.status != null) task.setStatus(TaskStatus.valueOf(req.status));
            if (req.priority != null) task.setPriority(Priority.valueOf(req.priority));
            if (req.assigneeId != null) {
                userRepository.findById(req.assigneeId).ifPresent(task::setAssignee);
            }
            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id,
                                        @RequestBody TaskRequest req) {
        return taskRepository.findById(id).map(task -> {
            if (req.title != null) task.setTitle(req.title);
            if (req.description != null) task.setDescription(req.description);
            if (req.status != null) task.setStatus(TaskStatus.valueOf(req.status));
            if (req.priority != null) task.setPriority(Priority.valueOf(req.priority));
            if (req.dueDate != null) task.setDueDate(req.dueDate);
            if (req.assigneeId != null) {
                userRepository.findById(req.assigneeId).ifPresent(task::setAssignee);
            }
            if (req.taskListId != null) {
                taskListRepository.findById(req.taskListId).ifPresent(task::setTaskList);
            }
            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task deleted");
    }

    @GetMapping("/overdue")
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }
}