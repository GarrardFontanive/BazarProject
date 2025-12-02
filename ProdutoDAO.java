package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.ProdutoDTO;
import com.classes.enums.CategoriaProduto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements ICrudDAO<ProdutoDTO> {

    @Override
    public boolean inserir(ProdutoDTO produto) {
        String sql = "INSERT INTO produto (codigo_barras, nome, categoria, preco_unitario, quantidade_estoque, com_defeito, ativo) VALUES (?, ?, ?, ?, ?, ?, 1)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getCodigoBarras());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getCategoria().name());
            stmt.setDouble(4, produto.getPrecoUnitario());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setBoolean(6, produto.isComDefeito());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir produto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(ProdutoDTO produto) {
        String sql = "UPDATE produto SET nome = ?, categoria = ?, preco_unitario = ?, quantidade_estoque = ?, com_defeito = ?, ativo = 1 WHERE codigo_barras = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCategoria().name());
            stmt.setDouble(3, produto.getPrecoUnitario());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setBoolean(5, produto.isComDefeito());
            stmt.setString(6, produto.getCodigoBarras());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Object id) {
        String codigo = (String) id;
        String sql = "UPDATE produto SET ativo = 0 WHERE codigo_barras = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inativar produto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ProdutoDTO> listarTodos() {
        List<ProdutoDTO> lista = new ArrayList<>();
        String sql = "SELECT codigo_barras, nome, categoria, preco_unitario, quantidade_estoque, com_defeito, ativo FROM produto WHERE ativo = 1";
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ProdutoDTO p = new ProdutoDTO();
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNome(rs.getString("nome"));
                p.setPrecoUnitario(rs.getDouble("preco_unitario"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                p.setComDefeito(rs.getBoolean("com_defeito"));
                p.setAtivo(rs.getBoolean("ativo"));

                try {
                    String catStr = rs.getString("categoria");
                    p.setCategoria(CategoriaProduto.valueOf(catStr.toUpperCase()));
                } catch (Exception e) {
                    p.setCategoria(CategoriaProduto.OUTROS);
                }

                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return lista;
    }

    public ProdutoDTO procurarPorCodigo(String codigo) {
        String sql = "SELECT * FROM produto WHERE codigo_barras = ? AND ativo = 1";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ProdutoDTO p = new ProdutoDTO();
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNome(rs.getString("nome"));
                p.setPrecoUnitario(rs.getDouble("preco_unitario"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                p.setComDefeito(rs.getBoolean("com_defeito"));
                p.setAtivo(rs.getBoolean("ativo"));

                try {
                    p.setCategoria(CategoriaProduto.valueOf(rs.getString("categoria").toUpperCase()));
                } catch (Exception e) {
                    p.setCategoria(CategoriaProduto.OUTROS);
                }
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto: " + e.getMessage());
        }
        return null;
    }

    public int consultarEstoqueAtual(String codigoBarras) {
        String sql = "SELECT quantidade_estoque FROM produto WHERE codigo_barras = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantidade_estoque");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean baixarEstoque(String codigoBarras, int quantidadeVendida) {
        String sql = "UPDATE produto SET quantidade_estoque = quantidade_estoque - ? WHERE codigo_barras = ? AND quantidade_estoque >= ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidadeVendida);
            stmt.setString(2, codigoBarras);
            stmt.setInt(3, quantidadeVendida);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao baixar estoque: " + e.getMessage());
            return false;
        }
    }

    public List<ProdutoDTO> listarPorCategoria(CategoriaProduto categoria) {
        List<ProdutoDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE categoria = ? AND ativo = 1";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.name());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProdutoDTO p = new ProdutoDTO();
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNome(rs.getString("nome"));
                p.setPrecoUnitario(rs.getDouble("preco_unitario"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                p.setComDefeito(rs.getBoolean("com_defeito"));
                p.setAtivo(rs.getBoolean("ativo"));

                try {
                    p.setCategoria(CategoriaProduto.valueOf(rs.getString("categoria").toUpperCase()));
                } catch (Exception e) {
                    p.setCategoria(CategoriaProduto.OUTROS);
                }
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao filtrar produtos: " + e.getMessage());
        }
        return lista;
    }

    public int getQuantidadeBaixoEstoque() {
        String sql = "SELECT COUNT(*) as qtd FROM produto WHERE quantidade_estoque <= 3 AND ativo = 1";
        int quantidade = 0;

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                quantidade = rs.getInt("qtd");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar estoque baixo: " + e.getMessage());
        }
        return quantidade;
    }

    public List<ProdutoDTO> listarProdutosComBaixoEstoque(int limite) {
        List<ProdutoDTO> lista = new ArrayList<>();
        String sql = "SELECT nome, quantidade_estoque, categoria FROM produto WHERE quantidade_estoque <= ? AND ativo = 1";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoDTO p = new ProdutoDTO();
                p.setNome(rs.getString("nome"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));

                try {
                    String catStr = rs.getString("categoria");
                    p.setCategoria(CategoriaProduto.valueOf(catStr.toUpperCase()));
                } catch (Exception e) {
                    p.setCategoria(CategoriaProduto.OUTROS);
                }

                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}