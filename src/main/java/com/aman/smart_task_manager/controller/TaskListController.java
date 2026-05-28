package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.TaskList;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.TaskListRepository;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/lists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    @Data static class ListRequest { String name; Integer position; }

    @GetMapping
    public ResponseEntity<List<TaskList>> getLists(@PathVariable Long boardId) {
        return boardRepository.findById(boardId).map(board ->
                ResponseEntity.ok(taskListRepository.findByBoardOrderByPositionAsc(board))
        ).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createList(@PathVariable Long boardId,
                                        @RequestBody ListRequest req) {
        return boardRepository.findById(boardId).map(board -> {
            TaskList list = TaskList.builder()
                    .name(req.name)
                    .position(req.position)
                    .board(board)
                    .build();
            return ResponseEntity.ok(taskListRepository.save(list));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{listId}")
    public ResponseEntity<?> updateList(@PathVariable Long listId,
                                        @RequestBody ListRequest req) {
        return taskListRepository.findById(listId).map(list -> {
            list.setName(req.name);
            list.setPosition(req.position);
            return ResponseEntity.ok(taskListRepository.save(list));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<?> deleteList(@PathVariable Long listId) {
        taskListRepository.deleteById(listId);
        return ResponseEntity.ok("List deleted");
    }
}