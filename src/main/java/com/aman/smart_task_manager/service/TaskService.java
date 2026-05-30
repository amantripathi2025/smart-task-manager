package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.request.TaskCreateRequest;
import com.aman.smart_task_manager.dto.request.TaskUpdateRequest;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.dto.response.PagedResponse;
import com.aman.smart_task_manager.dto.response.TaskDto;
import com.aman.smart_task_manager.exception.ForbiddenException;
import com.aman.smart_task_manager.exception.NotFoundException;
import com.aman.smart_task_manager.model.*;
import com.aman.smart_task_manager.repository.LabelRepository;
import com.aman.smart_task_manager.repository.TaskListRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import com.aman.smart_task_manager.util.EnumParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final AuthorizationService authorizationService;
    private final DtoMapper mapper;
    private final ActivityService activityService;

    public PagedResponse<TaskDto> search(Long boardId, String status, String priority, Long assigneeId, String query,
                                         int page, int size, String sortBy, String sortDir, User user) {
        authorizationService.requireBoardAccess(boardId, user);
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return PagedResponse.from(taskRepository.search(boardId,
                EnumParser.parse(status, TaskStatus.class, "status"),
                EnumParser.parse(priority, Priority.class, "priority"),
                assigneeId, query, pageable).map(mapper::toTaskDto), sortBy, sortDir);
    }

    public TaskDto create(TaskCreateRequest request, User user) {
        TaskList list = taskListRepository.findById(request.taskListId())
                .orElseThrow(() -> new NotFoundException("List not found"));
        Board board = authorizationService.requireBoardAccess(list.getBoard().getId(), user);

        Task task = Task.builder()
                .title(request.title().trim())
                .description(request.description())
                .taskList(list)
                .status(EnumParser.parse(request.status(), TaskStatus.class, "status"))
                .priority(EnumParser.parse(request.priority(), Priority.class, "priority"))
                .dueDate(request.dueDate())
                .reminderAt(request.reminderAt())
                .labels(resolveLabels(request.labels()))
                .build();
        if (task.getStatus() == null) task.setStatus(TaskStatus.TODO);
        if (task.getPriority() == null) task.setPriority(Priority.MEDIUM);
        task.setAssignee(resolveAssignee(request.assigneeId(), board));
        Task saved = taskRepository.save(task);
        activityService.log(board.getId(), "TASK_CREATED", "TASK", saved.getId(), "Task created", user);
        return mapper.toTaskDto(saved);
    }

    public TaskDto update(Long taskId, TaskUpdateRequest request, User user) {
        Task task = authorizationService.requireTaskAccess(taskId, user);
        Board board = task.getTaskList().getBoard();
        boolean boardMember = authorizationService.canAccessBoard(board, user);
        boolean assignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        if (!boardMember && !assignee) {
            throw new ForbiddenException("You cannot update this task");
        }

        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        TaskStatus status = EnumParser.parse(request.status(), TaskStatus.class, "status");
        Priority priority = EnumParser.parse(request.priority(), Priority.class, "priority");
        if (status != null) task.setStatus(status);
        if (priority != null) task.setPriority(priority);
        task.setDueDate(request.dueDate());
        task.setReminderAt(request.reminderAt());

        if (request.assigneeId() != null) {
            task.setAssignee(resolveAssignee(request.assigneeId(), board));
        }
        if (request.taskListId() != null) {
            TaskList list = taskListRepository.findById(request.taskListId())
                    .orElseThrow(() -> new NotFoundException("List not found"));
            if (!list.getBoard().getId().equals(board.getId())) {
                throw new ForbiddenException("Cannot move task to another board");
            }
            task.setTaskList(list);
        }
        if (request.labels() != null) {
            task.setLabels(resolveLabels(request.labels()));
        }

        Task saved = taskRepository.save(task);
        activityService.log(board.getId(), "TASK_UPDATED", "TASK", taskId, "Task updated", user);
        return mapper.toTaskDto(saved);
    }

    public MessageResponse delete(Long taskId, User user) {
        Task task = authorizationService.requireTaskAccess(taskId, user);
        Board board = task.getTaskList().getBoard();
        if (!authorizationService.isBoardOwner(board, user)) {
            throw new ForbiddenException("Only board owner can delete tasks");
        }
        taskRepository.delete(task);
        activityService.log(board.getId(), "TASK_DELETED", "TASK", taskId, "Task deleted", user);
        return new MessageResponse("Task deleted");
    }

    public java.util.List<TaskDto> overdue(Long boardId, User user) {
        authorizationService.requireBoardAccess(boardId, user);
        return taskRepository.findOverdueTasks(boardId, LocalDateTime.now()).stream().map(mapper::toTaskDto).toList();
    }

    public java.util.List<TaskDto> reminders(Long boardId, Integer hours, User user) {
        authorizationService.requireBoardAccess(boardId, user);
        int window = (hours == null || hours < 1) ? 24 : hours;
        return taskRepository.findDueReminders(boardId, LocalDateTime.now().plusHours(window)).stream().map(mapper::toTaskDto).toList();
    }

    private User resolveAssignee(Long assigneeId, Board board) {
        if (assigneeId == null) return null;
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new NotFoundException("Assignee not found"));
        boolean inBoard = board.getOwner().getId().equals(assignee.getId()) ||
                board.getMembers().stream().anyMatch(m -> m.getId().equals(assignee.getId()));
        if (!inBoard) {
            throw new ForbiddenException("Assignee must be board owner/member");
        }
        return assignee;
    }

    private Set<Label> resolveLabels(Set<String> labelNames) {
        if (labelNames == null) return Set.of();
        return labelNames.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(this::resolveLabel)
                .collect(Collectors.toSet());
    }

    private Label resolveLabel(String name) {
        return labelRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> labelRepository.save(Label.builder().name(name.toLowerCase())
                        .color("#6366f1").build()));
    }
}
