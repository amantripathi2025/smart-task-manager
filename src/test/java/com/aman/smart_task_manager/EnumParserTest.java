package com.aman.smart_task_manager;

import com.aman.smart_task_manager.exception.BadRequestException;
import com.aman.smart_task_manager.model.TaskStatus;
import com.aman.smart_task_manager.util.EnumParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumParserTest {

    @Test
    void parseValidEnumCaseInsensitive() {
        assertEquals(TaskStatus.IN_PROGRESS, EnumParser.parse("in_progress", TaskStatus.class, "status"));
    }

    @Test
    void parseInvalidEnumThrowsBadRequest() {
        assertThrows(BadRequestException.class, () -> EnumParser.parse("INVALID", TaskStatus.class, "status"));
    }
}
