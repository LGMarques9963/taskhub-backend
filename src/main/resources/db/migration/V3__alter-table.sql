-- Renomear as colunas em inglês
ALTER TABLE tasks
    RENAME COLUMN expiration_date TO due_date,
    RENAME COLUMN data_criacao TO created_at,
    RENAME COLUMN data_atualizacao TO updated_at,
    ALTER COLUMN status TYPE ENUM('To do', 'Doing', 'Done') USING status::text::enum_status;
