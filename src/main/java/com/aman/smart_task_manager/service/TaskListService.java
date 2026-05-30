package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.request.TaskListCreateRequest;
import com.aman.smart_task_manager.dto.request.TaskListUpdateRequest;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.dto.response.PagedResponse;
import com.aman.smart_task_manager.dto.response.TaskListDto;
import com.aman.smart_task_manager.exception.NotFoundException;
import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.TaskList;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.TaskListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskListService {

    private final TaskListRepository taskListRepository;
    private final AuthorizationService authorizationService;
    private final DtoMapper mapper;
    private final ActivityService activityService;

    public PagedResponse<TaskListDto> list(Long boardId, int page, int size, String sortDir, User user) {
        Board board = authorizationService.requireBoardAccess(boardId, user);
        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(sortDir) ? Sort.by("position").descending() : Sort.by("position").ascending());
        return PagedResponse.from(taskListRepository.findByBoard(board, pageable).map(mapper::toTaskListDto), "position", sortDir);
    }

    public TaskListDto create(Long boardId, TaskListCreateRequest request, User user) {
        Board board = authorizationService.requireBoardAccess(boardId, user);
        TaskList list = TaskList.builder()
                .name(request.name().trim())
                .position(request.position())
                .board(board)
                .build();
        TaskList saved = taskListRepository.save(list);
        activityService.log(boardId, "LIST_CREATED", "LIST", saved.getId(), "List created", user);
        return mapper.toTaskListDto(saved);
    }

    public TaskListDto update(Long boardId, Long listId, TaskListUpdateRequest request, User user) {
        authorizationService.requireBoardAccess(boardId, user);
        TaskList list = taskListRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("List not found"));
        if (!list.getBoard().getId().equals(boardId)) {
            throw new NotFoundException("List not found in board");
        }
        list.setName(request.name().trim());
        list.setPosition(request.position());
        TaskList saved = taskListRepository.save(list);
        activityService.log(boardId, "LIST_UPDATED", "LIST", listId, "List updated", user);
        return mapper.toTaskListDto(saved);
    }

    public MessageResponse delete(Long boardId, Long listId, User user) {
        Board board = authorizationService.requireBoardOwnerOrAdmin(boardId, user);
        TaskList list = taskListRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("List not found"));
        if (!list.getBoard().getId().equals(board.getId())) {
            throw new NotFoundException("List not found in board");
        }
        taskListRepository.delete(list);
        activityService.log(boardId, "LIST_DELETED", "LIST", listId, "List deleted", user);
        return new MessageResponse("List deleted");
    }
}
