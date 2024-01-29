import com.lgmarques.taskhub.domain.task.*;
import com.lgmarques.taskhub.domain.user.User;
import com.lgmarques.taskhub.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testPerformanceWithLargeNumberOfTasksAndUsers() {
        // Mocking a large number of tasks and users
        int numberOfTasks = 10000;
        int numberOfUsers = 1000;

        for (int i = 0; i < numberOfUsers; i++) {
            User user = new User();
            user.setId((long) i);
            user.setEmail("user" + i + "@example.com");
            Mockito.when(userRepository.findUserByEmail(eq(user.getEmail()))).thenReturn(user);

            for (int j = 0; j < numberOfTasks / numberOfUsers; j++) {
                Task task = new Task();
                task.setId((long) (i * numberOfTasks / numberOfUsers + j));
                task.setTitle("Task " + task.getId());
                task.setUserId(user.getId());
                Mockito.when(taskService.get(eq(task.getId()))).thenReturn(task);
                Mockito.when(taskService.getTasks(eq(user.getEmail()))).thenReturn(Collections.singletonList(task));
            }
        }

        // Performance test - Retrieve tasks for all users
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfUsers; i++) {
            List<Task> tasks = taskController.getTasks("user" + i + "@example.com");
            assertEquals(numberOfTasks / numberOfUsers, tasks.size());
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Performance Test - Retrieve tasks for all users: " + (endTime - startTime) + " ms");
    }

    @Test
    public void testScalabilityByAddingNewTasksAndUsers() {
        // Mocking a new user and task
        User newUser = new User();
        newUser.setId(1001L);
        newUser.setEmail("newuser@example.com");
        Mockito.when(userRepository.findUserByEmail(eq(newUser.getEmail()))).thenReturn(newUser);

        Task newTask = new Task();
        newTask.setId(10001L);
        newTask.setTitle("New Task");
        newTask.setUserId(newUser.getId());
        Mockito.when(taskService.get(eq(newTask.getId()))).thenReturn(newTask);
        Mockito.when(taskService.getTasks(eq(newUser.getEmail()))).thenReturn(Collections.singletonList(newTask));

        // Scalability test - Add new user and task
        long startTime = System.currentTimeMillis();
        List<Task> tasks = taskController.getTasks("newuser@example.com");
        assertEquals(1, tasks.size());
        long endTime = System.currentTimeMillis();

        System.out.println("Scalability Test - Add new user and task: " + (endTime - startTime) + " ms");
    }

    @Test
    public void testUnexpectedExceptionsHandling() {
        // Mocking an unexpected exception
        Mockito.when(taskService.get(eq(1L))).thenThrow(new RuntimeException("Unexpected exception"));

        // Test unexpected exceptions handling
        assertThrows(RuntimeException.class, () -> taskController.getTask(1L));
    }

    @Test
    public void testSpecificErrorHandling() {
        // Mocking a specific error scenario
        Mockito.when(taskService.delete(eq(2L))).thenThrow(new RuntimeException("Error deleting task"));

        // Test specific error handling
        assertThrows(RuntimeException.class, () -> taskController.deleteTask(2L));
    }
}
