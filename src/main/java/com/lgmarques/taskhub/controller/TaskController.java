package com.lgmarques.taskhub.controller;

import com.lgmarques.taskhub.domain.task.Task;
import com.lgmarques.taskhub.domain.task.TaskData;
import com.lgmarques.taskhub.domain.task.TaskService;
import com.lgmarques.taskhub.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskData taskData) {
        Task task = taskService.create(taskData);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        Task task = taskService.get(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = tokenService.getSubject(token);
        List<Task> tasks = taskService.getTasks(email);
        return ResponseEntity.ok(tasks);
    }
}
