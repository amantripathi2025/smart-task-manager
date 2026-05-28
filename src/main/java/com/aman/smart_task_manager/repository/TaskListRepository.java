package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.TaskList;
import com.aman.smart_task_manager.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findByBoardOrderByPositionAsc(Board board);
}