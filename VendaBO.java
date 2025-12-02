package com.classes.BO;

import com.classes.DAO.ItemVendaDAO;
import com.classes.DAO.ProdutoDAO;
import com.classes.DAO.VendaDAO;
import com.classes.DTO.ItemVendaDTO;
import com.classes.DTO.VendaDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VendaBO {

    private VendaDAO vendaDAO = new VendaDAO();
    private ItemVendaDAO itemVendaDAO = new ItemVendaDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public boolean processarVenda(VendaDTO venda) {
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            System.err.println("Carrinho vazio.");
            return false;
        }
        if (venda.getCliente() == null) {
            System.err.println("Cliente não identificado.");
            return false;
        }

        for (ItemVendaDTO item : venda.getItens()) {
            int estoqueAtual = produtoDAO.consultarEstoqueAtual(item.getProduto().getCodigoBarras());
            if (estoqueAtual < item.getQuantidade()) {
                System.err.println("Estoque insuficiente para: " + item.getProduto().getNome());
                return false;
            }
        }

        double total = calcularTotal(venda);
        venda.setTotal(total);

        if (venda.getDataHora() == null) {
            venda.setDataHora(LocalDateTime.now());
        }

        int idVenda = vendaDAO.inserirVendaERetornarId(venda);

        if (idVenda > 0) {
            venda.setId_venda(idVenda);

            for (ItemVendaDTO item : venda.getItens()) {
                item.setVenda(venda);
                itemVendaDAO.inserir(item);

                produtoDAO.baixarEstoque(item.getProduto().getCodigoBarras(), item.getQuantidade());
            }
            return true;
        } else {
            return false;
        }
    }

    public double calcularTotal(VendaDTO venda) {
        double total = 0.0;
        for (ItemVendaDTO item : venda.getItens()) {
            total += item.getSubtotal();
        }
        if (venda.getDesconto() > 0) {
            total -= venda.getDesconto();
        }
        return total;
    }

    public boolean cancelarVenda(int idVenda) {
        itemVendaDAO.excluirPorVenda(idVenda);
        return vendaDAO.excluir(idVenda);
    }

    public List<VendaDTO> listarTodas() {
        return vendaDAO.listarTodos();
    }

    public List<VendaDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            return new ArrayList<>();
        }
        return vendaDAO.listarPorPeriodo(dataInicio, dataFim);
    }

    public VendaDTO buscarVendaCompletaPorId(int idVenda) {
        VendaDTO venda = vendaDAO.buscarPorId(idVenda);

        if (venda != null) {
            List<ItemVendaDTO> itens = itemVendaDAO.listarPorVenda(idVenda);
            venda.setItens(itens);
        }
        return venda;
    }

    public double buscarTotalVendasHoje() {
        return vendaDAO.getTotalVendasHoje();
    }
}