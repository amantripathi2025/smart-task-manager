package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.request.CommentCreateRequest;
import com.aman.smart_task_manager.dto.response.CommentDto;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.service.CommentService;
import com.aman.smart_task_manager.service.CurrentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long taskId) {
        return commentService.list(taskId, currentUserService.getCurrentUser());
    }

    @PostMapping
    public CommentDto addComment(@PathVariable Long taskId, @Valid @RequestBody CommentCreateRequest request) {
        return commentService.create(taskId, request, currentUserService.getCurrentUser());
    }

    @DeleteMapping("/{commentId}")
    public MessageResponse deleteComment(@PathVariable Long taskId, @PathVariable Long commentId) {
        return commentService.delete(taskId, commentId, currentUserService.getCurrentUser());
    }
}
