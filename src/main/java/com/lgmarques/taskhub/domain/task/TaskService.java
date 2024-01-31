package com.lgmarques.taskhub.domain.task;

import com.lgmarques.taskhub.domain.user.User;
import com.lgmarques.taskhub.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Task create(TaskData taskData) {
        if (!userRepository.existsByEmail(taskData.userEmail())) throw new RuntimeException("User not found");
        Long userId = userRepository.findUserByEmail(taskData.userEmail()).getId();
        Task task = new Task();
        task.setTitle(taskData.title());
        task.setDescription(taskData.description());
        task.setUserId(userId);
        if (taskData.status() != null) task.setStatus(taskData.status());
        if (taskData.priority() != null) task.setPriority(taskData.priority());
        if (taskData.dueDate() != null) task.setDueDate(taskData.dueDate());
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) throw new RuntimeException("Task not found");
        taskRepository.deleteById(id);
    }

    public Task get(Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public List<Task> getTasks(String email) {
        User user = userRepository.findUserByEmail(email);
        return taskRepository.findAllByUserId(user.getId());
    }

    public Task update(Long id, TaskData taskData) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(taskData.title());
        task.setDescription(taskData.description());
        if (taskData.status() != null) task.setStatus(taskData.status());
        if (taskData.priority() != null) task.setPriority(taskData.priority());
        if (taskData.dueDate() != null) task.setDueDate(taskData.dueDate());
        task.setUpdatedAt(LocalDateTime.now().format(TaskData.DATE_FORMATTER));
        return taskRepository.save(task);
    }
}
