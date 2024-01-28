CREATE TABLE users (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL
);

CREATE TABLE tasks (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         expiration_date DATE,
                         priority ENUM('high', 'medium', 'low') DEFAULT 'medium',
                         category VARCHAR(50),
                         user_id INT,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_email ON users (email);
CREATE INDEX idx_user_id ON tasks (user_id);


