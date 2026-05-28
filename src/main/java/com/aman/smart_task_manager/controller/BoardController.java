package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Data
    static class BoardRequest {
        String name, description;
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping
    public List<Board> getMyBoards(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return boardRepository.findByOwner(user);
    }

    @PostMapping
    public Board createBoard(@RequestBody BoardRequest req,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        Board board = Board.builder()
                .name(req.name)
                .description(req.description)
                .owner(user)
                .build();
        return boardRepository.save(board);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        return boardRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id,
                                             @RequestBody BoardRequest req) {
        return boardRepository.findById(id).map(board -> {
            board.setName(req.name);
            board.setDescription(req.description);
            return ResponseEntity.ok(boardRepository.save(board));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
        boardRepository.deleteById(id);
        return ResponseEntity.ok("Board deleted");
    }
}