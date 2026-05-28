package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.Comment;
import com.aman.smart_task_manager.repository.CommentRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Data static class CommentRequest { String content; }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long taskId) {
        return taskRepository.findById(taskId).map(task ->
                ResponseEntity.ok(commentRepository.findByTaskOrderByCreatedAtAsc(task))
        ).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long taskId,
                                        @RequestBody CommentRequest req,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return taskRepository.findById(taskId).map(task -> {
            var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            Comment comment = Comment.builder()
                    .content(req.content)
                    .task(task)
                    .author(user)
                    .build();
            return ResponseEntity.ok(commentRepository.save(comment));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentRepository.deleteById(commentId);
        return ResponseEntity.ok("Comment deleted");
    }
}