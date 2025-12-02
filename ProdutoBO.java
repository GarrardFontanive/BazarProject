package com.classes.BO;

import com.classes.DAO.ProdutoDAO;
import com.classes.DTO.ProdutoDTO;
import com.classes.enums.CategoriaProduto;

import java.util.List;

public class ProdutoBO {

    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public boolean cadastrar(ProdutoDTO produto) {
        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().isEmpty()) return false;
        if (produto.getNome() == null || produto.getNome().isEmpty()) return false;
        if (produto.getPrecoUnitario() <= 0) return false;
        return produtoDAO.inserir(produto);
    }

    public boolean atualizar(ProdutoDTO produto) {
        return produtoDAO.atualizar(produto);
    }

    public boolean excluir(String codigoBarras) {
        return produtoDAO.excluir(codigoBarras);
    }

    public ProdutoDTO buscarPorCodigoBarras(String codigoBarras) {
        return produtoDAO.procurarPorCodigo(codigoBarras);
    }

    public List<ProdutoDTO> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public List<ProdutoDTO> listarPorCategoria(CategoriaProduto categoria) {
        return produtoDAO.listarPorCategoria(categoria);
    }

    public int buscarProdutosBaixoEstoque() {
        return produtoDAO.getQuantidadeBaixoEstoque();
    }

    public List<ProdutoDTO> buscarAlertasEstoque() {
        return produtoDAO.listarProdutosComBaixoEstoque(10);
    }
}
