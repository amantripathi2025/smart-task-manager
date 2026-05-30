package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.TaskList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    Page<TaskList> findByBoard(Board board, Pageable pageable);
}
