package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Comment;
import com.aman.smart_task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    @Query("SELECT c FROM Comment c WHERE c.id = :id AND (c.task.taskList.board.owner.id = :userId OR :user MEMBER OF c.task.taskList.board.members)")
    Optional<Comment> findByIdWithUserAccess(@Param("id") Long id, @Param("userId") Long userId, @Param("user") User user);
}
