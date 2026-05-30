package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.request.BoardCreateRequest;
import com.aman.smart_task_manager.dto.request.BoardUpdateRequest;
import com.aman.smart_task_manager.dto.response.*;
import com.aman.smart_task_manager.exception.NotFoundException;
import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.ActivityRepository;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final DtoMapper mapper;
    private final AuthorizationService authorizationService;
    private final ActivityService activityService;

    public PagedResponse<BoardDto> getBoards(User user, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        return PagedResponse.from(boardRepository.findAccessibleBoards(user, pageable).map(mapper::toBoardDto), sortBy, sortDir);
    }

    public BoardDto create(BoardCreateRequest request, User user) {
        Board board = Board.builder()
                .name(request.name().trim())
                .description(request.description())
                .owner(user)
                .build();
        Board saved = boardRepository.save(board);
        activityService.log(saved.getId(), "BOARD_CREATED", "BOARD", saved.getId(), "Board created", user);
        return mapper.toBoardDto(saved);
    }

    public BoardDto getById(Long boardId, User user) {
        return mapper.toBoardDto(authorizationService.requireBoardAccess(boardId, user));
    }

    public BoardDto update(Long boardId, BoardUpdateRequest request, User user) {
        Board board = authorizationService.requireBoardOwnerOrAdmin(boardId, user);
        board.setName(request.name().trim());
        board.setDescription(request.description());
        Board saved = boardRepository.save(board);
        activityService.log(boardId, "BOARD_UPDATED", "BOARD", boardId, "Board updated", user);
        return mapper.toBoardDto(saved);
    }

    public MessageResponse delete(Long boardId, User user) {
        authorizationService.requireBoardOwnerOrAdmin(boardId, user);
        boardRepository.deleteById(boardId);
        return new MessageResponse("Board deleted");
    }

    public BoardDto addMember(Long boardId, Long memberId, User user) {
        Board board = authorizationService.requireBoardOwnerOrAdmin(boardId, user);
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        board.getMembers().add(member);
        Board saved = boardRepository.save(board);
        activityService.log(boardId, "BOARD_MEMBER_ADDED", "USER", memberId, "Member added to board", user);
        return mapper.toBoardDto(saved);
    }

    public BoardDto removeMember(Long boardId, Long memberId, User user) {
        Board board = authorizationService.requireBoardOwnerOrAdmin(boardId, user);
        board.getMembers().removeIf(m -> m.getId().equals(memberId));
        Board saved = boardRepository.save(board);
        activityService.log(boardId, "BOARD_MEMBER_REMOVED", "USER", memberId, "Member removed from board", user);
        return mapper.toBoardDto(saved);
    }

    public PagedResponse<ActivityDto> getActivities(Long boardId, int page, int size, User user) {
        authorizationService.requireBoardAccess(boardId, user);
        Pageable pageable = PageRequest.of(page, size);
        return PagedResponse.from(activityRepository.findByBoardIdOrderByCreatedAtDesc(boardId, pageable).map(mapper::toActivityDto),
                "createdAt", "desc");
    }
}
