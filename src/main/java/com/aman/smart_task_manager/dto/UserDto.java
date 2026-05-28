package com.aman.smart_task_manager.dto;

import com.aman.smart_task_manager.model.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
