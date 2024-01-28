package com.lgmarques.taskhub.domain.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Table(name = "tasks")
@Entity(name = "Task")
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String dueDate;
    private String createdAt;
    private String updatedAt;
    private Long userId;
}
