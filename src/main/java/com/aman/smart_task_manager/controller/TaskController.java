package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.request.TaskCreateRequest;
import com.aman.smart_task_manager.dto.request.TaskUpdateRequest;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.dto.response.PagedResponse;
import com.aman.smart_task_manager.dto.response.TaskDto;
import com.aman.smart_task_manager.service.CurrentUserService;
import com.aman.smart_task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public PagedResponse<TaskDto> search(@RequestParam Long boardId,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String priority,
                                         @RequestParam(required = false) Long assigneeId,
                                         @RequestParam(required = false, name = "q") String query,
                                         @RequestParam(defaultValue = "0") @Min(0) int page,
                                         @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                                         @RequestParam(defaultValue = "createdAt") String sortBy,
                                         @RequestParam(defaultValue = "desc") String sortDir) {
        return taskService.search(boardId, status, priority, assigneeId, query, page, size, sortBy, sortDir,
                currentUserService.getCurrentUser());
    }

    @PostMapping
    public TaskDto createTask(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.create(request, currentUserService.getCurrentUser());
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(id, request, currentUserService.getCurrentUser());
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteTask(@PathVariable Long id) {
        return taskService.delete(id, currentUserService.getCurrentUser());
    }

    @GetMapping("/overdue")
    public List<TaskDto> getOverdueTasks(@RequestParam Long boardId) {
        return taskService.overdue(boardId, currentUserService.getCurrentUser());
    }

    @GetMapping("/reminders")
    public List<TaskDto> getReminders(@RequestParam Long boardId,
                                      @RequestParam(required = false) Integer hours) {
        return taskService.reminders(boardId, hours, currentUserService.getCurrentUser());
    }
}
