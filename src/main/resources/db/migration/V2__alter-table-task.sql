ALTER TABLE tasks
    ADD COLUMN data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN status VARCHAR(20) DEFAULT 'A fazer' CHECK (status IN ('A fazer', 'Em andamento', 'Concluída'));
