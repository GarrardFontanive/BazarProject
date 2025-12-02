package com.classes.DTO;

import com.classes.enums.FormaPagamento;
import java.time.LocalDateTime;
import java.util.List;

public class VendaDTO {
    private int id_venda;
    private LocalDateTime dataHora;
    private ClienteDTO cliente;
    private double total;
    private double desconto;
    private String motivoDesconto;
    private FormaPagamento formaPagamento;
    private String adminNome;
    private List<ItemVendaDTO> itens;

    public VendaDTO() {
    }

    public VendaDTO(int id_venda, LocalDateTime dataHora, double desconto, String motivoDesconto, double total, String formaPagamentoStr, String adminNome) {
        this.id_venda = id_venda;
        this.dataHora = dataHora;
        this.desconto = desconto;
        this.motivoDesconto = motivoDesconto;
        this.total = total;
        this.adminNome = adminNome;
        // Tenta converter a string do banco para Enum
        try {
            this.formaPagamento = FormaPagamento.valueOf(formaPagamentoStr);
        } catch (IllegalArgumentException e) {
            this.formaPagamento = FormaPagamento.OUTRO;
        }
    }

    public int getId_venda() {
        return id_venda;
    }

    public void setId_venda(int id_venda) {
        this.id_venda = id_venda;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public String getMotivoDesconto() {
        return motivoDesconto;
    }

    public void setMotivoDesconto(String motivoDesconto) {
        this.motivoDesconto = motivoDesconto;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getAdminNome() {
        return adminNome;
    }

    public void setAdminNome(String adminNome) {
        this.adminNome = adminNome;
    }

    public List<ItemVendaDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemVendaDTO> itens) {
        this.itens = itens;
    }
}