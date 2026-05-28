package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.*;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.TaskListRepository;
import com.aman.smart_task_manager.repository.TaskRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import com.aman.smart_task_manager.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private TaskListRepository taskListRepository;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "alice@example.com")
    void filterTasksByStatusAndDueDate() throws Exception {
        User currentUser = User.builder().id(1L).name("Alice").email("alice@example.com").role(Role.USER).build();
        Board board = Board.builder().id(10L).name("Board").owner(currentUser).members(Set.of()).build();
        TaskList list = TaskList.builder().id(20L).name("Todo").board(board).build();
        Task task1 = Task.builder().id(100L).title("T1").status(TaskStatus.IN_PROGRESS).taskList(list)
                .dueDate(LocalDateTime.of(2026, 5, 28, 12, 0)).build();
        Task task2 = Task.builder().id(101L).title("T2").status(TaskStatus.DONE).taskList(list)
                .dueDate(LocalDateTime.of(2026, 5, 28, 15, 0)).build();

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(currentUser));
        when(boardRepository.findBoardByIdAndUserAccess(10L, 1L, currentUser)).thenReturn(Optional.of(board));
        when(taskRepository.findByBoardWithFilters(any(), any(), any(), any(), any())).thenReturn(List.of(task1));

        mockMvc.perform(get("/api/boards/10/tasks")
                        .param("status", "IN_PROGRESS")
                        .param("dueDate", "2026-05-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$.length()").value(1));
    }
}
