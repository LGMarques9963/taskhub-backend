package com.lgmarques.taskhub.domain.task;

import java.time.format.DateTimeFormatter;

public record TaskData(String title,
                       String description,
                       String status,
                       String priority,
                       String dueDate,
                       String userEmail) {
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
