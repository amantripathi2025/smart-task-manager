package com.aman.smart_task_manager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_lists")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskList {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}