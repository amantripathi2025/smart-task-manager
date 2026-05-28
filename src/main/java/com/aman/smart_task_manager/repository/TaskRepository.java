package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTaskList(TaskList taskList);
    List<Task> findByAssignee(User user);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByDueDateBefore(LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.taskList.board.id = :boardId")
    List<Task> findByBoardId(Long boardId);

    @Query("SELECT t FROM Task t WHERE t.taskList.board.id = :boardId AND t.status = :status")
    List<Task> findByBoardIdAndStatus(Long boardId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'DONE'")
    List<Task> findOverdueTasks(LocalDateTime now);
}