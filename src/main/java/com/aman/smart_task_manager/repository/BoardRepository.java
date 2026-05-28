package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwner(User owner);
    List<Board> findByMembersContaining(User user);
}