package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Task;
import com.aman.smart_task_manager.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
            select t from Task t
            where t.taskList.board.id = :boardId
              and (:status is null or t.status = :status)
              and (:priority is null or t.priority = :priority)
              and (:assigneeId is null or t.assignee.id = :assigneeId)
              and (:query is null or lower(t.title) like lower(concat('%', :query, '%')) or lower(coalesce(t.description, '')) like lower(concat('%', :query, '%')))
            """)
    Page<Task> search(Long boardId, TaskStatus status, com.aman.smart_task_manager.model.Priority priority,
                      Long assigneeId, String query, Pageable pageable);

    @Query("select t from Task t where t.taskList.board.id = :boardId and t.dueDate < :now and t.status <> 'DONE'")
    List<Task> findOverdueTasks(Long boardId, LocalDateTime now);

    @Query("select t.status, count(t) from Task t where t.taskList.board.id = :boardId group by t.status")
    List<Object[]> countByStatusForBoard(Long boardId);

    @Query("select count(t) from Task t where t.taskList.board.id = :boardId and t.dueDate < :now and t.status <> 'DONE'")
    long countOverdue(Long boardId, LocalDateTime now);

    @Query("select t from Task t where t.taskList.board.id = :boardId and t.reminderAt <= :nowPlus and t.status <> 'DONE' order by t.reminderAt asc")
    List<Task> findDueReminders(Long boardId, LocalDateTime nowPlus);
}
