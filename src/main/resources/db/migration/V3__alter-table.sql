-- Renomear as colunas em inglês
ALTER TABLE tasks
    CHANGE COLUMN expiration_date due_date DATE,
    CHANGE COLUMN data_criacao created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHANGE COLUMN data_atualizacao updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHANGE COLUMN status status ENUM('To do', 'Doing', 'Done') DEFAULT 'To do';
