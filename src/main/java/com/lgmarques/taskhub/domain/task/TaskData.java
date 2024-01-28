package com.lgmarques.taskhub.domain.task;

public record TaskData(String title,
                       String description,
                       String status,
                       String priority,
                       String dueDate,
                       String createdAt,
                       String updatedAt,
                       String userEmail) {
}
