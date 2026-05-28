package com.aman.smart_task_manager.repository;

import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwnerId(Long ownerId);

    @Query("SELECT b FROM Board b WHERE b.owner.id = :userId OR :user MEMBER OF b.members")
    List<Board> findBoardsByUserIdOrMember(@Param("userId") Long userId, @Param("user") User user);

    @Query("SELECT b FROM Board b WHERE b.id = :boardId AND (b.owner.id = :userId OR :user MEMBER OF b.members)")
    Optional<Board> findBoardByIdAndUserAccess(@Param("boardId") Long boardId, @Param("userId") Long userId, @Param("user") User user);
}
