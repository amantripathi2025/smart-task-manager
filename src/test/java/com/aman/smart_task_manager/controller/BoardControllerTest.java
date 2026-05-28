package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.Board;
import com.aman.smart_task_manager.model.Role;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.BoardRepository;
import com.aman.smart_task_manager.repository.UserRepository;
import com.aman.smart_task_manager.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardRepository boardRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "member@example.com")
    void updateBoardForNonOwnerShouldReturnForbidden() throws Exception {
        User member = User.builder().id(1L).name("Member").email("member@example.com").role(Role.USER).build();
        User owner = User.builder().id(2L).name("Owner").email("owner@example.com").role(Role.USER).build();
        Board board = Board.builder().id(5L).name("Board").owner(owner).members(Set.of(member)).build();

        when(userRepository.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(boardRepository.findBoardByIdAndUserAccess(5L, 1L, member)).thenReturn(Optional.of(board));

        mockMvc.perform(put("/api/boards/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"Desc\"}"))
                .andExpect(status().isForbidden());
    }
}
