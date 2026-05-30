package com.aman.smart_task_manager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_lists", indexes = {
        @Index(name = "idx_task_list_board", columnList = "board_id"),
        @Index(name = "idx_task_list_board_position", columnList = "board_id,position")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
