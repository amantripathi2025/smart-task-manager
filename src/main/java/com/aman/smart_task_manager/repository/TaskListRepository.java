package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.TaskList;
import com.aman.smart_task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByBoardIdOrderByPosition(Long boardId);

    @Query("SELECT t FROM TaskList t WHERE t.id = :id AND (t.board.owner.id = :userId OR :user MEMBER OF t.board.members)")
    Optional<TaskList> findByIdWithUserAccess(@Param("id") Long id, @Param("userId") Long userId, @Param("user") User user);
}
