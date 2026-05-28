package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.*;
import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<BoardDto> createBoard(@Valid @RequestBody BoardRequest request) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.save(Board.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(currentUser)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(board));
    }

    @GetMapping
    public List<BoardDto> getBoards() {
        User currentUser = getCurrentUser();
        return boardRepository.findBoardsByUserIdOrMember(currentUser.getId(), currentUser)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{boardId}")
    public BoardDto getBoard(@PathVariable Long boardId) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        return toDto(board);
    }

    @PutMapping("/{boardId}")
    public BoardDto updateBoard(@PathVariable Long boardId, @Valid @RequestBody BoardRequest request) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        if (!board.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only board owner can update board");
        }
        board.setName(request.getName());
        board.setDescription(request.getDescription());
        return toDto(boardRepository.save(board));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        if (!board.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only board owner can delete board");
        }
        boardRepository.delete(board);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/members/{userId}")
    public BoardDto addMember(@PathVariable Long boardId, @PathVariable Long userId) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        if (!board.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only board owner can add members");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        board.getMembers().add(user);
        return toDto(boardRepository.save(board));
    }

    @DeleteMapping("/{boardId}/members/{userId}")
    public BoardDto removeMember(@PathVariable Long boardId, @PathVariable Long userId) {
        User currentUser = getCurrentUser();
        Board board = boardRepository.findBoardByIdAndUserAccess(boardId, currentUser.getId(), currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        if (!board.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only board owner can remove members");
        }
        board.getMembers().removeIf(member -> member.getId().equals(userId));
        return toDto(boardRepository.save(board));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private BoardDto toDto(Board board) {
        Set<UserDto> memberDtos = board.getMembers() == null ? Set.of() : board.getMembers().stream().map(this::toUserDto).collect(Collectors.toSet());
        return BoardDto.builder()
                .id(board.getId())
                .name(board.getName())
                .description(board.getDescription())
                .owner(toUserDto(board.getOwner()))
                .members(memberDtos)
                .createdAt(board.getCreatedAt())
                .build();
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
