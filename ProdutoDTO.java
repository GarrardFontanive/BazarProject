package com.classes.DTO;

import com.classes.enums.CategoriaProduto;

public class ProdutoDTO {
    private String codigoBarras;
    private String nome;
    private CategoriaProduto categoria;
    private double precoUnitario;
    private int quantidadeEstoque;
    private boolean comDefeito;
    private boolean ativo;

    public ProdutoDTO() {
    }

    public ProdutoDTO(String codigoBarras, String nome, CategoriaProduto categoria, double precoUnitario, int quantidadeEstoque, boolean comDefeito) {
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
        this.quantidadeEstoque = quantidadeEstoque;
        this.comDefeito = comDefeito;
        this.ativo = true;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    // --- GETTER E SETTER DO ESTOQUE ---
    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public boolean isComDefeito() {
        return comDefeito;
    }

    public void setComDefeito(boolean comDefeito) {
        this.comDefeito = comDefeito;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProdutoDTO{");
        sb.append("codigoBarras='").append(codigoBarras).append('\'');
        sb.append(", nome='").append(nome).append('\'');
        sb.append(", categoria=").append(categoria);
        sb.append(", precoUnitario=").append(precoUnitario);
        sb.append(", quantidadeEstoque=").append(quantidadeEstoque);
        sb.append(", comDefeito=").append(comDefeito);
        sb.append(", ativo=").append(ativo);
        sb.append('}');
        return sb.toString();
    }
}