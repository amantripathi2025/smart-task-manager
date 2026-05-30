package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.response.*;
import com.aman.smart_task_manager.model.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public UserSummaryDto toUserSummary(User user) {
        if (user == null) return null;
        return new UserSummaryDto(user.getId(), user.getName(), user.getEmail());
    }

    public LabelDto toLabelDto(Label label) {
        return new LabelDto(label.getId(), label.getName(), label.getColor());
    }

    public TaskDto toTaskDto(Task task) {
        Set<LabelDto> labels = task.getLabels() == null ? Set.of() : task.getLabels().stream()
                .map(this::toLabelDto)
                .sorted(Comparator.comparing(LabelDto::name))
                .collect(Collectors.toSet());
        return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getStatus().name(),
                task.getPriority().name(), task.getDueDate(), task.getReminderAt(), toUserSummary(task.getAssignee()),
                task.getTaskList().getId(), labels, task.getCreatedAt());
    }

    public BoardDto toBoardDto(Board board) {
        return new BoardDto(board.getId(), board.getName(), board.getDescription(), toUserSummary(board.getOwner()),
                board.getMembers().stream().map(this::toUserSummary).collect(Collectors.toSet()), board.getCreatedAt());
    }

    public TaskListDto toTaskListDto(TaskList list) {
        return new TaskListDto(list.getId(), list.getName(), list.getPosition(), list.getBoard().getId());
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getContent(), toUserSummary(comment.getAuthor()),
                comment.getTask().getId(), comment.getCreatedAt());
    }

    public ActivityDto toActivityDto(Activity activity) {
        return new ActivityDto(activity.getId(), activity.getAction(), activity.getEntityType(), activity.getEntityId(),
                activity.getMessage(), toUserSummary(activity.getActor()), activity.getCreatedAt());
    }
}
