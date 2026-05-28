package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Task;
import com.aman.smart_task_manager.model.TaskStatus;
import com.aman.smart_task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTaskListId(Long taskListId);

    List<Task> findByTaskListBoardId(Long boardId);

    List<Task> findByTaskListIdAndStatus(Long taskListId, TaskStatus status);

    List<Task> findByTaskListBoardIdAndAssigneeId(Long boardId, Long assigneeId);

    List<Task> findByTaskListBoardIdAndDueDateBetween(Long boardId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND (t.taskList.board.owner.id = :userId OR :user MEMBER OF t.taskList.board.members)")
    Optional<Task> findByIdWithUserAccess(@Param("id") Long id, @Param("userId") Long userId, @Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.taskList.board.id = :boardId AND t.status = :status")
    List<Task> findByBoardIdAndStatus(@Param("boardId") Long boardId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.taskList.board.id = :boardId AND t.assignee.id = :assigneeId")
    List<Task> findByBoardIdAndAssigneeId(@Param("boardId") Long boardId, @Param("assigneeId") Long assigneeId);

    @Query("SELECT t FROM Task t WHERE t.taskList.board.id = :boardId AND t.assignee.id = :assigneeId AND t.status = :status")
    List<Task> findByBoardIdAndAssigneeIdAndStatus(@Param("boardId") Long boardId,
                                                   @Param("assigneeId") Long assigneeId,
                                                   @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.taskList.board.id = :boardId AND t.dueDate < :now AND t.status <> com.aman.smart_task_manager.model.TaskStatus.DONE")
    List<Task> findOverdueTasksByBoardId(@Param("boardId") Long boardId, @Param("now") LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId AND t.dueDate < :now AND t.status <> com.aman.smart_task_manager.model.TaskStatus.DONE")
    List<Task> findOverdueTasksByAssignee(@Param("assigneeId") Long assigneeId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT t FROM Task t
            WHERE t.taskList.board.id = :boardId
              AND (:status IS NULL OR t.status = :status)
              AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
              AND (:startDate IS NULL OR t.dueDate >= :startDate)
              AND (:endDate IS NULL OR t.dueDate <= :endDate)
            """)
    List<Task> findByBoardWithFilters(@Param("boardId") Long boardId,
                                      @Param("status") TaskStatus status,
                                      @Param("assigneeId") Long assigneeId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskList.board.id = :boardId AND t.status = :status")
    long countByBoardIdAndStatus(@Param("boardId") Long boardId, @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskList.board.id = :boardId")
    long countByBoardId(@Param("boardId") Long boardId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskList.board.id = :boardId AND t.assignee.id = :assigneeId")
    long countByBoardIdAndAssigneeId(@Param("boardId") Long boardId, @Param("assigneeId") Long assigneeId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskList.board.id = :boardId AND t.dueDate < :now AND t.status <> com.aman.smart_task_manager.model.TaskStatus.DONE")
    long countOverdueTasksByBoardId(@Param("boardId") Long boardId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT COUNT(t) FROM Task t
            WHERE t.taskList.board.id = :boardId
              AND t.assignee.id = :assigneeId
              AND t.dueDate < :now
              AND t.status <> com.aman.smart_task_manager.model.TaskStatus.DONE
            """)
    long countOverdueTasksByBoardIdAndAssigneeId(@Param("boardId") Long boardId,
                                                 @Param("assigneeId") Long assigneeId,
                                                 @Param("now") LocalDateTime now);
}
