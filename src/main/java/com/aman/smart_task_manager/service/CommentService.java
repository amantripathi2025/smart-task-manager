package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.request.CommentCreateRequest;
import com.aman.smart_task_manager.dto.response.CommentDto;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.exception.ForbiddenException;
import com.aman.smart_task_manager.exception.NotFoundException;
import com.aman.smart_task_manager.model.Comment;
import com.aman.smart_task_manager.model.Task;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.CommentRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final AuthorizationService authorizationService;
    private final DtoMapper mapper;
    private final ActivityService activityService;

    public List<CommentDto> list(Long taskId, User user) {
        Task task = authorizationService.requireTaskAccess(taskId, user);
        return commentRepository.findByTaskOrderByCreatedAtAsc(task).stream().map(mapper::toCommentDto).toList();
    }

    public CommentDto create(Long taskId, CommentCreateRequest request, User user) {
        Task task = authorizationService.requireTaskAccess(taskId, user);
        Comment comment = Comment.builder()
                .content(request.content().trim())
                .author(user)
                .task(task)
                .build();
        Comment saved = commentRepository.save(comment);
        activityService.log(task.getTaskList().getBoard().getId(), "COMMENT_ADDED", "COMMENT", saved.getId(),
                "Comment added", user);
        return mapper.toCommentDto(saved);
    }

    public MessageResponse delete(Long taskId, Long commentId, User user) {
        Task task = authorizationService.requireTaskAccess(taskId, user);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getTask().getId().equals(task.getId())) {
            throw new NotFoundException("Comment not found for task");
        }
        boolean boardOwner = authorizationService.isBoardOwner(task.getTaskList().getBoard(), user);
        boolean author = comment.getAuthor().getId().equals(user.getId());
        if (!boardOwner && !author) {
            throw new ForbiddenException("Only comment author or board owner can delete comment");
        }
        commentRepository.delete(comment);
        activityService.log(task.getTaskList().getBoard().getId(), "COMMENT_DELETED", "COMMENT", commentId,
                "Comment deleted", user);
        return new MessageResponse("Comment deleted");
    }
}
