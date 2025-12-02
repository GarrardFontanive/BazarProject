package com.classes.enums;

public enum CategoriaProduto {
    // --- ROUPAS ---
    ROUPA_FEMININA("Roupa Feminina"),
    ROUPA_MASCULINA("Roupa Masculina"),
    ROUPA_INFANTIL("Roupa Infantil"),
    ROUPA_INVERNO("Inverno/Pesado"), // Para casacos pesados, jaquetas

    // --- ESPECÍFICOS DA TABELA ---
    VESTIDOS("Vestidos"),
    PECAS_INTIMAS("Peças Íntimas"),

    // --- CALÇADOS ---
    SAPATO_ADULTO("Sapatos Adulto"),
    SAPATO_INFANTIL("Sapatos Infantil"),

    // --- OUTROS ---
    ACESSORIOS("Acessórios"),
    CAMA_MESA_BANHO("Cama/Mesa/Banho"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}