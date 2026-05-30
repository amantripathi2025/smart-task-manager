package com.aman.smart_task_manager.util;

import com.aman.smart_task_manager.exception.BadRequestException;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class EnumParser {
    private EnumParser() {}

    public static <T extends Enum<T>> T parse(String raw, Class<T> type, String field) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return Enum.valueOf(type, raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            String values = Arrays.stream(type.getEnumConstants()).map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Invalid " + field + ". Allowed values: " + values);
        }
    }
}
