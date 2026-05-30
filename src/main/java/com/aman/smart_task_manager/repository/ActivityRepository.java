package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByBoardIdOrderByCreatedAtDesc(Long boardId, Pageable pageable);
}
