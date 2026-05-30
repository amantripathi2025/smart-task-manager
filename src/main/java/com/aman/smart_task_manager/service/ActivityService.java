package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.model.Activity;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public void log(Long boardId, String action, String entityType, Long entityId, String message, User actor) {
        activityRepository.save(Activity.builder()
                .boardId(boardId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .message(message)
                .actor(actor)
                .build());
    }
}
