package com.aman.smart_task_manager.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(List<T> content, int page, int size, long totalElements,
                               int totalPages, String sortBy, String sortDir) {
    public static <T> PagedResponse<T> from(Page<T> page, String sortBy, String sortDir) {
        return new PagedResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), sortBy, sortDir);
    }
}
