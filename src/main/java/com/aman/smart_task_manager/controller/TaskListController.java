package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.request.TaskListCreateRequest;
import com.aman.smart_task_manager.dto.request.TaskListUpdateRequest;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.dto.response.PagedResponse;
import com.aman.smart_task_manager.dto.response.TaskListDto;
import com.aman.smart_task_manager.service.CurrentUserService;
import com.aman.smart_task_manager.service.TaskListService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/boards/{boardId}/lists")
@RequiredArgsConstructor
@Tag(name = "Task Lists")
public class TaskListController {

    private final TaskListService taskListService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public PagedResponse<TaskListDto> getLists(@PathVariable Long boardId,
                                               @RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                                               @RequestParam(defaultValue = "asc") String sortDir) {
        return taskListService.list(boardId, page, size, sortDir, currentUserService.getCurrentUser());
    }

    @PostMapping
    public TaskListDto createList(@PathVariable Long boardId, @Valid @RequestBody TaskListCreateRequest request) {
        return taskListService.create(boardId, request, currentUserService.getCurrentUser());
    }

    @PutMapping("/{listId}")
    public TaskListDto updateList(@PathVariable Long boardId, @PathVariable Long listId,
                                  @Valid @RequestBody TaskListUpdateRequest request) {
        return taskListService.update(boardId, listId, request, currentUserService.getCurrentUser());
    }

    @DeleteMapping("/{listId}")
    public MessageResponse deleteList(@PathVariable Long boardId, @PathVariable Long listId) {
        return taskListService.delete(boardId, listId, currentUserService.getCurrentUser());
    }
}
