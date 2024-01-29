import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        mockTaskData = new TaskData();
        mockTaskData.setTitle("Test Task");
        mockTaskData.setDescription("Test Description");
        mockTaskData.setStatus(TaskStatus.OPEN);
        mockTaskData.setPriority(TaskPriority.LOW);
        mockTaskData.setDueDate(LocalDate.now().plusDays(1));
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        Mockito.when(userRepository.findUserByEmail(eq("test@example.com"))).thenReturn(mockUser);

        Task task1 = new Task(1L, "Task 1", "Description 1");
        task1.setUserID(mockUser.getId());
        Task task2 = new Task(2L, "Task 2", "Description 2");
        task2.setUserID(mockUser.getId());

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
        Mockito.when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        Task createdTask = taskService.create(mockTaskData);

        assertNotNull(createdTask);
        assertEquals(mockUser.getId(), createdTask.getUserId());
        assertEquals(mockTaskData.getTitle(), createdTask.getTitle());
        assertEquals(mockTaskData.getDescription(), createdTask.getDescription());
        assertEquals(mockTaskData.getPriority(), createdTask.getPriority());
        assertEquals(mockTaskData.getStatus(), createdTask.getStatus());
        assertEquals(mockTaskData.getDueDate(), createdTask.getDueDate());
    
    }

    @Test
    void deleteTask_shouldDeleteTask() {
        taskService.delete(1L);

        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(eq(1L));
    
    }

    @Test
    void updateTask_shouldUpdateTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        Mockito.when(taskRepository.findById(eq(1L))).thenReturn(Optional.of(existingTask));

        Task updatedTask = taskService.update(1L, mockTaskData);

        assertNotNull(updatedTask);
        assertEquals(existingTask.getId(), updatedTask.getId());
        assertEquals(mockTaskData.getTitle(), updatedTask.getTitle());
        assertEquals(mockTaskData.getDescription(), updatedTask.getDescription());
        assertEquals(mockTaskData.getPriority(), updatedTask.getPriority());
        assertEquals(mockTaskData.getStatus(), updatedTask.getStatus());
        assertEquals(mockTaskData.getDueDate(), updatedTask.getDueDate());
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
    void createTaskWithNullTitle_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.createTask(null, null));
    }

    @Test
    void createTaskWithEmptyTitle_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.createTask("", null));
    }

    @Test
    void updateTaskWithNullTitle_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, null, null));
    }

    @Test
    void updateTaskWithEmptyTitle_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, "", null));
    }

    @Test
    void createTaskWithNullUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.createTask("Test Title", null, null));
    }

    @Test
    void createTaskWithEmptyUser_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> taskService.createTask("Test Title", "", null));
    }

    @Test
    void createTaskWithMinimumData_shouldCreateTask() {
        // Act
        Task result = taskService.createTask("Min Title", null);

        // Assert
        assertNotNull(result);
        assertEquals("Min Title", result.getTitle());
        assertNull(result.getDescription());
    }

    @Test
    void createTaskWithMaximumData_shouldCreateTask() {
        // Arrange
        String longTitle = "A".repeat(255);
        String longDescription = "B".repeat(1000);

        // Act
        Task result = taskService.createTask(longTitle, longDescription);

        // Assert
        assertNotNull(result);
        assertEquals(longTitle, result.getTitle());
        assertEquals(longDescription, result.getDescription());
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
    void createTaskWithExpiredDueDate_shouldCreateTask() {
        // Arrange
        LocalDate expiredDate = LocalDate.now().minusDays(1);

        // Act
        Task result = taskService.createTaskWithDueDate("Expired Task", expiredDate);

        // Assert
        assertNotNull(result);
        assertEquals("Expired Task", result.getTitle());
        assertEquals(expiredDate, result.getDueDate());
    }

    @Test
    void createTaskWithFutureDueDate_shouldCreateTask() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);

        // Act
        Task result = taskService.createTaskWithDueDate("Future Task", futureDate);

        // Assert
        assertNotNull(result);
        assertEquals("Future Task", result.getTitle());
        assertEquals(futureDate, result.getDueDate());
    }

    @Test
    void createTaskWithNullDueDate_shouldCreateTask() {
        // Act
        Task result = taskService.createTaskWithDueDate("Null Due Date Task", null);

        // Assert
        assertNotNull(result);
        assertEquals("Null Due Date Task", result.getTitle());
        assertNull(result.getDueDate());
    }

    void createTaskWithPastDueDate_shouldThrowException() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTaskWithDueDate("Past Task", pastDate));
    }

    @Test
    void createTaskWithDueDateInOneYear_shouldCreateTask() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusYears(1);

        // Act
        Task result = taskService.createTaskWithDueDate("Future Task", futureDate);

        // Assert
        assertNotNull(result);
        assertEquals("Future Task", result.getTitle());
        assertEquals(futureDate, result.getDueDate());
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
