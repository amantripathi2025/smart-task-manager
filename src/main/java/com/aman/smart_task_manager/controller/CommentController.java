package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.CommentDto;
import com.aman.smart_task_manager.dto.CommentRequest;
import com.aman.smart_task_manager.dto.UserDto;
import com.aman.smart_task_manager.model.Comment;
import com.aman.smart_task_manager.model.Task;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.CommentRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
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
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @PostMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long taskId,
                                                    @Valid @RequestBody CommentRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findByIdWithUserAccess(taskId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        Comment comment = commentRepository.save(Comment.builder()
                .content(request.getContent())
                .author(currentUser)
                .task(task)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(comment));
    }

    @GetMapping("/api/tasks/{taskId}/comments")
    public List<CommentDto> getComments(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        taskRepository.findByIdWithUserAccess(taskId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findByIdWithUserAccess(commentId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isBoardOwner = comment.getTask().getTaskList().getBoard().getOwner().getId().equals(currentUser.getId());
        if (!isAuthor && !isBoardOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this comment");
        }
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(toUserDto(comment.getAuthor()))
                .taskId(comment.getTask().getId())
                .createdAt(comment.getCreatedAt())
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
