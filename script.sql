-- -----------------------------------------------------
-- 1. ATUALIZAR TABELA DOADOR
-- Adiciona colunas de documento (CPF/CNPJ) se não existirem
-- -----------------------------------------------------

-- Se a tabela doador não existir, cria ela. Se existir, o comando é ignorado.
CREATE TABLE IF NOT EXISTS doador (
    id_doador INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Adiciona as colunas novas na tabela existente
-- ATENÇÃO: Se você já rodou isso antes, pode dar erro dizendo que a coluna existe. 
-- Se der erro, apenas ignore e pule para a próxima linha.
ALTER TABLE doador ADD COLUMN tipo_documento ENUM('CPF', 'CNPJ') DEFAULT 'CPF';
ALTER TABLE doador ADD COLUMN documento VARCHAR(20);


-- -----------------------------------------------------
-- 2. ATUALIZAR TABELA PRODUTO
-- Removemos a ligação direta antiga (se você chegou a criar)
-- -----------------------------------------------------

-- Tenta remover a chave estrangeira antiga (se existir)
-- Se der erro dizendo que não existe, ÓTIMO, significa que está limpo.
-- Pode ser necessário ajustar o nome 'fk_produto_doador' se o seu banco gerou outro nome.
-- ALTER TABLE produto DROP FOREIGN KEY fk_produto_doador; 
-- ALTER TABLE produto DROP COLUMN id_doador;


-- -----------------------------------------------------
-- 3. REFAZER A MOVIMENTACAO_ESTOQUE (A Associativa)
-- Como essa tabela muda drasticamente (vira associativa), 
-- o mais seguro para garantir a estrutura correta é recriá-la.
-- -----------------------------------------------------

DROP TABLE IF EXISTS movimentacao_estoque;

CREATE TABLE movimentacao_estoque (
    id_movimentacao INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Quem é o produto?
    codigo_barras VARCHAR(50) NOT NULL,
    
    -- De onde veio? (Doador - Só preenche se for ENTRADA)
    id_doador INT, 
    
    -- Dados da Movimentação
    tipo_movimento ENUM('ENTRADA', 'SAIDA', 'AJUSTE') NOT NULL,
    quantidade INT NOT NULL,
    data_movimentacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    observacao VARCHAR(200),

    -- Chaves Estrangeiras (Associando Produto e Doador)
    CONSTRAINT fk_mov_produto FOREIGN KEY (codigo_barras) REFERENCES produto(codigo_barras),
    CONSTRAINT fk_mov_doador FOREIGN KEY (id_doador) REFERENCES doador(id_doador)
);

-- -----------------------------------------------------
-- 4. GARANTIR USUÁRIO (Se não existir)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(50) NOT NULL,
    nome_completo VARCHAR(100),
    admin BOOLEAN DEFAULT FALSE
);