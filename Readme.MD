# TaskHub - Task Management System

TaskHub is a task management system developed using the robustness and efficiency of the Spring Boot framework. Designed to provide a simplified and centralized task management experience, TaskHub empowers users to organize their responsibilities efficiently, providing a clear and comprehensive view of their daily, weekly, and monthly tasks.

## Key Features

- **User Authentication:** Register in the system, securely log in, and keep your information protected.
    - **JWT Token:** The system uses JWT tokens for user authentication, stored in the browser's LocalStorage.
    - **Password Encryption:** User passwords are encrypted with the BCrypt algorithm before being stored in the database.
    - **Data Validation:** The system validates user input data to ensure correctness and compliance with database constraints.
    - **Access Restrictions:** The system prevents unauthenticated users from accessing restricted resources and unauthorized users from accessing other users' resources.
    - **Token Expiration:** Authentication tokens expire after 24 hours to ensure users log in regularly and keep their information up-to-date.
- **Basic Task Operations:** Create, edit, and delete tasks, specifying title, description, due date, and priority.
    - **Priorities:** Tasks can be categorized into three priorities: low, medium, and high.
    - **Due Dates:** Tasks can be assigned a due date, set as a specific date, or as a recurring date.
    - **Recurring Due Dates [to-do]:** Recurring due dates can be set as daily, weekly, or monthly.
    - **Notifications [to-do]:** The system sends email notifications to remind users of tasks with upcoming due dates.
- **Task Viewing:** View your tasks in an organized list and filter or sort based on different criteria.

## Technologies Used

- **Backend:**
    - Java Spring Boot
    - MySQL Database
    - Spring Security with JWT for authentication
    - JPA/Hibernate for object-relational mapping

## Prerequisites

- Java JDK 8 or higher
- Maven

## Configuration

1. Clone the repository: `git clone https://github.com/LGMarques9963/taskhub.git`
2. Configure the database properties in the `src/main/resources/application.properties` file.
3. Start the backend: `mvn spring-boot:run`

## API Endpoints

- **GET `/api/task`**: Get all tasks.
- **POST `/api/tasks`**: Create a new task.
- **PUT `/api/tasks/{id}`**: Edit an existing task.
- **DELETE `/api/tasks/{id}`**: Delete a task.
- **POST `/api/authentication/register`**: Register a new user.
- **POST `/api/authentication/login`**: Authenticate a user.

## Contributions

Contributions are welcome! Feel free to open issues or send pull requests to improve this project.

## License

This project is licensed under the [MIT License](LICENSE).
