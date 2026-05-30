package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select distinct b from Board b left join b.members m where b.owner = :user or m = :user")
    Page<Board> findAccessibleBoards(User user, Pageable pageable);

    @Query("select distinct b from Board b left join b.members m where b.id = :boardId and (b.owner = :user or m = :user)")
    Optional<Board> findAccessibleById(Long boardId, User user);
}
