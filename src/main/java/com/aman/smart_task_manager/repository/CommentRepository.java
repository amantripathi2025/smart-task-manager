package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Comment;
import com.aman.smart_task_manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtAsc(Task task);
}