package com.classes.DTO;

public class ItemVendaDTO {
    private int id_item;
    private ProdutoDTO produto;
    private VendaDTO venda;
    private int quantidade;
    private double subtotal;

    public ItemVendaDTO() {}

    public ItemVendaDTO(int id_item, ProdutoDTO produto, VendaDTO venda, int quantidade, double subtotal) {
        this.id_item = id_item;
        this.produto = produto;
        this.venda = venda;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

    public ProdutoDTO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoDTO produto) {
        this.produto = produto;
    }

    public VendaDTO getVenda() {
        return venda;
    }

    public void setVenda(VendaDTO venda) {
        this.venda = venda;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(produto.getNome());
        sb.append(" x");
        sb.append(quantidade);
        sb.append(" - R$");
        sb.append(subtotal);
        return sb.toString();
    }
}
