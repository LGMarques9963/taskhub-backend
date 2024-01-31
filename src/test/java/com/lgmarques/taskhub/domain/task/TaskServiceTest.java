package com.lgmarques.taskhub.domain.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lgmarques.taskhub.domain.user.User;
import com.lgmarques.taskhub.domain.user.UserRepository;
import com.lgmarques.taskhub.domain.task.enums.Priority;
import com.lgmarques.taskhub.domain.task.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;
    private TaskData mockTaskData;

    @BeforeEach
    public void setup(){
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");

        mockTaskData = new TaskData(
                "Test Task",
                "Test Description",
                Status.TODO.toString(),
                Priority.LOW.toString(),
                LocalDate.now().plusDays(1).toString(),
                "test@example.com"
        );
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        Mockito.when(userRepository.findUserByEmail(eq("test@example.com"))).thenReturn(mockUser);

        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUserId(mockUser.getId());
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUserId(mockUser.getId());

        Mockito.when(taskRepository.findAllByUserId(eq(mockUser.getId()))).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getTasks("test@example.com");

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals(task1.getId(), tasks.get(0).getId());
        assertEquals(task2.getId(), tasks.get(1).getId());
    }

    @Test
    void getTaskById_shouldReturnTask() {
        Task mockTask = new Task();
        mockTask.setId(1L);
        Mockito.when(taskRepository.findById(eq(1L))).thenReturn(Optional.of(mockTask));

        Task retrievedTask = taskService.get(1L);

        assertNotNull(retrievedTask);
        assertEquals(mockTask.getId(), retrievedTask.getId());
    }

    @Test
    void createTask_shouldCreateTask() {
        Mockito.when(userRepository.existsByEmail(eq("test@example.com"))).thenReturn(true);
        Mockito.when(userRepository.findUserByEmail(eq("test@example.com"))).thenReturn(mockUser);

        Task mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setUserId(mockUser.getId());
        mockTask.setTitle(mockTaskData.title());
        mockTask.setDescription(mockTaskData.description());
        mockTask.setPriority(mockTaskData.priority());
        mockTask.setStatus(mockTaskData.status());
        mockTask.setDueDate(mockTaskData.dueDate());
        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        Task createdTask = taskService.create(mockTaskData);

        assertNotNull(createdTask);
        assertEquals(mockUser.getId(), createdTask.getUserId());
        assertEquals(mockTaskData.title(), createdTask.getTitle());
        assertEquals(mockTaskData.description(), createdTask.getDescription());
        assertEquals(mockTaskData.priority(), createdTask.getPriority());
        assertEquals(mockTaskData.status(), createdTask.getStatus());
        assertEquals(mockTaskData.dueDate(), createdTask.getDueDate());

    }

    @Test
    void deleteTask_shouldDeleteTask() {
        Mockito.when(taskRepository.existsById(eq(1L))).thenReturn(true);

        taskService.delete(1L);

        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(eq(1L));

    }

    @Test
    @Disabled
    void updateTask_shouldUpdateTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setUserId(mockUser.getId());
        existingTask.setTitle("Existing Task");
        existingTask.setDescription("Existing Description");
        existingTask.setPriority(Priority.HIGH.toString());
        existingTask.setStatus(Status.DOING.toString());
        existingTask.setDueDate(LocalDate.now().plusDays(1).toString());

        Mockito.when(taskRepository.findById(eq(1L))).thenReturn(Optional.of(existingTask));

        Task updatedTask = taskService.update(1L, mockTaskData);

        assertNotNull(updatedTask);
        assertEquals(existingTask.getId(), updatedTask.getId());
        assertEquals(mockTaskData.title(), updatedTask.getTitle());
        assertEquals(mockTaskData.description(), updatedTask.getDescription());
        assertEquals(mockTaskData.priority(), updatedTask.getPriority());
        assertEquals(mockTaskData.status(), updatedTask.getStatus());
        assertEquals(mockTaskData.dueDate(), updatedTask.getDueDate());
    }

    @Test
    void getNonexistentTask_shouldThrowException() {
        Mockito.when(taskRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.get(1L));
    }

    @Test
    void updateNonexistentTask_shouldThrowException() {
        Mockito.when(taskRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.update(1L, mockTaskData));
    }

    @Test
    void deleteNonexistentTask_shouldThrowException() {
        Mockito.when(taskRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.delete(1L));
    }

    @Test
    void createTaskWithNullValues_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            taskService.create(new TaskData(null,null, null, null, null, null));
        });
    }

    @Test
    void createTaskWithEmptyTitle_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.create(new TaskData("", null, null, null, null, null)));
    }

    @Test
    void updateTaskWithNullValues_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.update(1L, new TaskData(null, null, null, null, null, null)));
    }

    @Test
    void updateTaskWithEmptyTitle_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.update(1L, new TaskData("", null, null, null, null, null)));
    }

    @Test
    void createTaskWithNullUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.create(
                new TaskData("Test Title",
                        "Test Description",
                        Status.TODO.toString(),
                        Priority.LOW.toString(),
                        LocalDate.now().plusDays(1).toString(),
                        null)));
    }

    @Test
    void createTaskWithEmptyUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.create(
                new TaskData("Test Title",
                        "Test Description",
                        Status.TODO.toString(),
                        Priority.LOW.toString(),
                        LocalDate.now().plusDays(1).toString(),
                        "")));
    }

    @Test
    void queryEmptyTasks_shouldReturnEmptyList() {
        Mockito.when(userRepository.findUserByEmail(eq("test@example.com"))).thenReturn(mockUser);
        Mockito.when(taskRepository.findAllByUserId(eq(mockUser.getId()))).thenReturn(Collections.emptyList());

        List<Task> tasks = taskService.getTasks("test@example.com");

        assertNotNull(tasks);
        assertEquals(0, tasks.size());
    }

    @Test
    @Disabled
    void createTaskWithExpiredDueDate_shouldCreateTask() {
        // Arrange
        LocalDate expiredDate = LocalDate.now().minusDays(1);
        Mockito.when(userRepository.existsByEmail(eq("test@example.com"))).thenReturn(true);
        Mockito.when(userRepository.findUserByEmail(eq("test@example.com"))).thenReturn(mockUser);

        // Act
        Task result = taskService.create(
                new TaskData("Expired Task",
                        "Expired Description",
                        Status.TODO.toString(),
                        Priority.LOW.toString(),
                        expiredDate.toString(),
                        "test@example.com"));

        // Assert
        assertNotNull(result);
        assertEquals("Expired Task", result.getTitle());
        assertEquals(expiredDate.toString(), result.getDueDate());
    }

    @Test
    @Disabled
    void createTaskWithDueDateInOneYear_shouldCreateTask() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusYears(1);
        Mockito.when(userRepository.existsByEmail(eq("test@example.com"))).thenReturn(true);
        Mockito.when(userRepository.findUserByEmail(eq("test@example.com"))).thenReturn(mockUser);
        TaskData taskData = new TaskData("Future Task",
                "Future Description",
                Status.TODO.toString(),
                Priority.LOW.toString(),
                futureDate.toString(),
                "test@example.com");

        // Act
        Task result = taskService.create(taskData);

        // Assert
        assertNotNull(result);
        assertEquals("Future Task", result.getTitle());
        assertEquals(futureDate.toString(), result.getDueDate());
    }

    @Test
    void userShouldOnlySeeTheirTasks() {
        // Mocking two users
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        Mockito.when(userRepository.findUserByEmail(eq("user1@example.com"))).thenReturn(user1);
        Mockito.when(userRepository.findUserByEmail(eq("user2@example.com"))).thenReturn(user2);

        // Mocking tasks for both users
        Task taskUser1 = new Task();
        taskUser1.setId(1L);
        taskUser1.setUserId(user1.getId());

        Task taskUser2 = new Task();
        taskUser2.setId(2L);
        taskUser2.setUserId(user2.getId());

        List<Task> allTasks = new ArrayList<>();
        allTasks.add(taskUser1);
        allTasks.add(taskUser2);

        Mockito.when(taskRepository.findAllByUserId(eq(user1.getId()))).thenReturn(Collections.singletonList(taskUser1));
        Mockito.when(taskRepository.findAllByUserId(eq(user2.getId()))).thenReturn(Collections.singletonList(taskUser2));

        // User 1 should only see tasks for user 1
        List<Task> tasksUser1 = taskService.getTasks("user1@example.com");
        assertNotNull(tasksUser1);
        assertEquals(1, tasksUser1.size());
        assertEquals(taskUser1.getId(), tasksUser1.get(0).getId());

        // User 2 should only see tasks for user 2
        List<Task> tasksUser2 = taskService.getTasks("user2@example.com");
        assertNotNull(tasksUser2);
        assertEquals(1, tasksUser2.size());
        assertEquals(taskUser2.getId(), tasksUser2.get(0).getId());
    }

}
