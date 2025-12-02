package com.classes.BO;

import com.classes.DAO.MovimentacaoEstoqueDAO;
import com.classes.DAO.ProdutoDAO;
import com.classes.DTO.DoadorDTO;
import com.classes.DTO.MovimentacaoEstoqueDTO;
import com.classes.DTO.ProdutoDTO;
import java.time.LocalDateTime;
import java.util.List;

public class EstoqueBO {

    private MovimentacaoEstoqueDAO movDAO = new MovimentacaoEstoqueDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public boolean darEntrada(ProdutoDTO produto, DoadorDTO doador, int quantidade) {
        if (quantidade <= 0) return false;

        MovimentacaoEstoqueDTO mov = new MovimentacaoEstoqueDTO();
        mov.setProduto(produto);
        mov.setDoador(doador);
        mov.setTipoMovimento("ENTRADA");
        mov.setQuantidade(quantidade);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setObservacao("Doação recebida");

        if (movDAO.registrarMovimentacao(mov)) {
            int novoSaldo = produto.getQuantidadeEstoque() + quantidade;
            produto.setQuantidadeEstoque(novoSaldo);
            return produtoDAO.atualizar(produto);
        }
        return false;
    }

    // --- NOVO MÉTODO PARA O AJUSTE MANUAL ---
    public boolean ajustarSaldo(ProdutoDTO produto, int novoSaldoTotal, int diferenca) {
        MovimentacaoEstoqueDTO mov = new MovimentacaoEstoqueDTO();
        mov.setProduto(produto);
        mov.setDoador(null); // Sem doador
        mov.setTipoMovimento("AJUSTE"); // Tipo especial
        mov.setQuantidade(diferenca); // Pode ser positivo ou negativo
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setObservacao("Ajuste manual de estoque");

        if (movDAO.registrarMovimentacao(mov)) {
            produto.setQuantidadeEstoque(novoSaldoTotal);
            return produtoDAO.atualizar(produto);
        }
        return false;
    }

    public List<MovimentacaoEstoqueDTO> buscarHistorico(String codigoBarras) {
        return movDAO.listarHistoricoPorProduto(codigoBarras);
    }
}