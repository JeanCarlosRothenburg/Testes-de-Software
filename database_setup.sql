-- Script SQL para criar tabela de assinaturas (PostgreSQL)
-- Execute este script se quiser testar com banco de dados real

CREATE TABLE IF NOT EXISTS assinaturas (
    id VARCHAR(255) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10,2) NOT NULL,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('MENSAL', 'TRIMESTRAL', 'SEMESTRAL', 'ANUAL')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('ATIVA', 'INATIVA', 'CANCELADA', 'VENCIDA')),
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    usuario_id VARCHAR(255) NOT NULL,
    auto_renovacao BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhorar performance
CREATE INDEX IF NOT EXISTS idx_assinaturas_usuario_id ON assinaturas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_assinaturas_status ON assinaturas(status);
CREATE INDEX IF NOT EXISTS idx_assinaturas_data_fim ON assinaturas(data_fim);

-- Dados de teste (opcional)
INSERT INTO assinaturas (id, nome, descricao, preco, tipo, status, data_inicio, data_fim, usuario_id, auto_renovacao) 
VALUES 
    ('prime001', 'Amazon Prime', 'Entrega rápida e streaming', 14.90, 'MENSAL', 'ATIVA', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month', 'userActiveSubs', true),
    ('music001', 'Amazon Music', 'Streaming de música', 9.90, 'MENSAL', 'ATIVA', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month', 'userActiveSubs', false),
    ('kindle001', 'Kindle Unlimited', 'Livros digitais', 19.90, 'MENSAL', 'INATIVA', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1 month', 'userActiveSubs', false)
ON CONFLICT (id) DO NOTHING;

-- Trigger para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_assinaturas_updated_at ON assinaturas;
CREATE TRIGGER update_assinaturas_updated_at 
    BEFORE UPDATE ON assinaturas 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
