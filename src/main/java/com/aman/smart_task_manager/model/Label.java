package com.aman.smart_task_manager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "labels", uniqueConstraints = {
        @UniqueConstraint(name = "uk_label_name", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 16)
    private String color;
}
