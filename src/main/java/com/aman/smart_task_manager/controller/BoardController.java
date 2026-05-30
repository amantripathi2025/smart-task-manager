package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.request.BoardCreateRequest;
import com.aman.smart_task_manager.dto.request.BoardUpdateRequest;
import com.aman.smart_task_manager.dto.request.MemberRequest;
import com.aman.smart_task_manager.dto.response.ActivityDto;
import com.aman.smart_task_manager.dto.response.BoardDto;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.dto.response.PagedResponse;
import com.aman.smart_task_manager.service.BoardService;
import com.aman.smart_task_manager.service.CurrentUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Tag(name = "Boards")
public class BoardController {

    private final BoardService boardService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public PagedResponse<BoardDto> getMyBoards(@RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                               @RequestParam(defaultValue = "desc") String sortDir) {
        return boardService.getBoards(currentUserService.getCurrentUser(), page, size, sortBy, sortDir);
    }

    @PostMapping
    public BoardDto createBoard(@Valid @RequestBody BoardCreateRequest request) {
        return boardService.create(request, currentUserService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public BoardDto getBoard(@PathVariable Long id) {
        return boardService.getById(id, currentUserService.getCurrentUser());
    }

    @PutMapping("/{id}")
    public BoardDto updateBoard(@PathVariable Long id, @Valid @RequestBody BoardUpdateRequest request) {
        return boardService.update(id, request, currentUserService.getCurrentUser());
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteBoard(@PathVariable Long id) {
        return boardService.delete(id, currentUserService.getCurrentUser());
    }

    @PostMapping("/{id}/members")
    public BoardDto addMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return boardService.addMember(id, request.userId(), currentUserService.getCurrentUser());
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public BoardDto removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        return boardService.removeMember(id, memberId, currentUserService.getCurrentUser());
    }

    @GetMapping("/{id}/activities")
    public PagedResponse<ActivityDto> activities(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "0") @Min(0) int page,
                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return boardService.getActivities(id, page, size, currentUserService.getCurrentUser());
    }
}
