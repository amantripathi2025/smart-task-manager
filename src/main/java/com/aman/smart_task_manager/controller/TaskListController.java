package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.TaskListDto;
import com.aman.smart_task_manager.dto.TaskListRequest;
import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.TaskList;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.TaskListRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/lists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TaskListDto> createTaskList(@PathVariable Long boardId,
                                                      @Valid @RequestBody TaskListRequest request) {
        User currentUser = getCurrentUser();
        Board board = getAccessibleBoard(boardId, currentUser);
        TaskList taskList = taskListRepository.save(TaskList.builder()
                .name(request.getName())
                .position(request.getPosition())
                .board(board)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(taskList));
    }

    @GetMapping
    public List<TaskListDto> getTaskLists(@PathVariable Long boardId) {
        User currentUser = getCurrentUser();
        getAccessibleBoard(boardId, currentUser);
        return taskListRepository.findByBoardIdOrderByPosition(boardId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{listId}")
    public TaskListDto getTaskList(@PathVariable Long boardId, @PathVariable Long listId) {
        User currentUser = getCurrentUser();
        TaskList taskList = taskListRepository.findByIdWithUserAccess(listId, currentUser.getId(), currentUser)
                .filter(list -> list.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list not found"));
        return toDto(taskList);
    }

    @PutMapping("/{listId}")
    public TaskListDto updateTaskList(@PathVariable Long boardId,
                                      @PathVariable Long listId,
                                      @Valid @RequestBody TaskListRequest request) {
        User currentUser = getCurrentUser();
        TaskList taskList = taskListRepository.findByIdWithUserAccess(listId, currentUser.getId(), currentUser)
                .filter(list -> list.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list not found"));
        taskList.setName(request.getName());
        taskList.setPosition(request.getPosition());
        return toDto(taskListRepository.save(taskList));
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteTaskList(@PathVariable Long boardId, @PathVariable Long listId) {
        User currentUser = getCurrentUser();
        TaskList taskList = taskListRepository.findByIdWithUserAccess(listId, currentUser.getId(), currentUser)
                .filter(list -> list.getBoard().getId().equals(boardId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list not found"));
        taskListRepository.delete(taskList);
        return ResponseEntity.noContent().build();
    }

    private Board getAccessibleBoard(Long boardId, User currentUser) {
        return boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private TaskListDto toDto(TaskList taskList) {
        return TaskListDto.builder()
                .id(taskList.getId())
                .name(taskList.getName())
                .position(taskList.getPosition())
                .boardId(taskList.getBoard().getId())
                .build();
    }
}
