package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.TaskDto;
import com.aman.smart_task_manager.dto.TaskRequest;
import com.aman.smart_task_manager.dto.UserDto;
import com.aman.smart_task_manager.model.*;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.TaskListRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @PostMapping("/api/lists/{taskListId}/tasks")
    public ResponseEntity<TaskDto> createTask(@PathVariable Long taskListId, @Valid @RequestBody TaskRequest request) {
        User currentUser = getCurrentUser();
        TaskList taskList = taskListRepository.findByIdWithUserAccess(taskListId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list not found"));
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .assignee(getAssignee(request.getAssigneeId()))
                .taskList(taskList)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(taskRepository.save(task)));
    }

    @GetMapping("/api/lists/{taskListId}/tasks")
    public List<TaskDto> getTasksByTaskList(@PathVariable Long taskListId,
                                            @RequestParam(required = false) TaskStatus status) {
        User currentUser = getCurrentUser();
        taskListRepository.findByIdWithUserAccess(taskListId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list not found"));

        List<Task> tasks = status == null
                ? taskRepository.findByTaskListId(taskListId)
                : taskRepository.findByTaskListIdAndStatus(taskListId, status);

        return tasks.stream().map(this::toDto).toList();
    }

    @GetMapping("/api/boards/{boardId}/tasks")
    public List<TaskDto> filterTasks(@PathVariable Long boardId,
                                     @RequestParam(required = false) TaskStatus status,
                                     @RequestParam(required = false) Long assigneeId,
                                     @RequestParam(required = false) LocalDate dueDate) {
        User currentUser = getCurrentUser();
        boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));

        LocalDateTime startDate = dueDate == null ? null : dueDate.atStartOfDay();
        LocalDateTime endDate = dueDate == null ? null : dueDate.atTime(LocalTime.MAX);
        List<Task> tasks = taskRepository.findByBoardWithFilters(boardId, status, assigneeId, startDate, endDate);
        return tasks.stream().map(this::toDto).toList();
    }

    @GetMapping("/api/tasks/{taskId}")
    public TaskDto getTask(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findByIdWithUserAccess(taskId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return toDto(task);
    }

    @PutMapping("/api/tasks/{taskId}")
    public TaskDto updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findByIdWithUserAccess(taskId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssignee(getAssignee(request.getAssigneeId()));
        return toDto(taskRepository.save(task));
    }

    @DeleteMapping("/api/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findByIdWithUserAccess(taskId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/tasks/{taskId}/assign/{assigneeId}")
    public TaskDto assignTask(@PathVariable Long taskId, @PathVariable Long assigneeId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findByIdWithUserAccess(taskId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setAssignee(getAssignee(assigneeId));
        return toDto(taskRepository.save(task));
    }

    private User getAssignee(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .assignee(task.getAssignee() == null ? null : toUserDto(task.getAssignee()))
                .taskListId(task.getTaskList().getId())
                .createdAt(task.getCreatedAt())
                .build();
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
