package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.exception.ForbiddenException;
import com.aman.smart_task_manager.exception.NotFoundException;
import com.aman.smart_task_manager.model.*;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;

    public Board requireBoardAccess(Long boardId, User user) {
        return boardRepository.findAccessibleById(boardId, user)
                .orElseThrow(() -> new ForbiddenException("You do not have access to this board"));
    }

    public Board requireBoardOwnerOrAdmin(Long boardId, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found"));
        if (!isAdmin(user) && !board.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("Only board owner can perform this action");
        }
        return board;
    }

    public Task requireTaskAccess(Long taskId, User user) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found"));
        Board board = task.getTaskList().getBoard();
        if (!canAccessBoard(board, user)) {
            throw new ForbiddenException("You do not have access to this task");
        }
        return task;
    }

    public boolean canAccessBoard(Board board, User user) {
        return board.getOwner().getId().equals(user.getId()) ||
                board.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId())) || isAdmin(user);
    }

    public boolean isBoardOwner(Board board, User user) {
        return board.getOwner().getId().equals(user.getId()) || isAdmin(user);
    }

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }
}
