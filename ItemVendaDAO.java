package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.ItemVendaDTO;
import com.classes.DTO.ProdutoDTO;
import com.classes.enums.CategoriaProduto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemVendaDAO {

    public boolean inserir(ItemVendaDTO item) {
        String sql = "INSERT INTO item_venda (id_venda, codigo_barras, quantidade, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getVenda().getId_venda());
            stmt.setString(2, item.getProduto().getCodigoBarras());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getSubtotal());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir item de venda: " + e.getMessage());
            return false;
        }
    }

    public List<ItemVendaDTO> listarPorVenda(int idVenda) {
        List<ItemVendaDTO> lista = new ArrayList<>();
        String sql = "SELECT iv.*, p.nome, p.preco_unitario, p.categoria, p.com_defeito, p.ativo, p.codigo_barras " +
                "FROM item_venda iv " +
                "JOIN produto p ON iv.codigo_barras = p.codigo_barras " +
                "WHERE iv.id_venda = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProdutoDTO p = new ProdutoDTO();
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNome(rs.getString("nome"));
                p.setPrecoUnitario(rs.getDouble("preco_unitario"));
                p.setComDefeito(rs.getBoolean("com_defeito"));
                p.setAtivo(rs.getBoolean("ativo"));

                try {
                    String catStr = rs.getString("categoria");
                    p.setCategoria(CategoriaProduto.valueOf(catStr));
                } catch (IllegalArgumentException e) {
                    p.setCategoria(CategoriaProduto.OUTROS);
                }

                ItemVendaDTO item = new ItemVendaDTO();
                item.setId_item(rs.getInt("id_item"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setSubtotal(rs.getDouble("subtotal"));
                item.setProduto(p);
                lista.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens da venda: " + e.getMessage());
        }
        return lista;
    }

    public boolean excluirPorVenda(int idVenda) {
        String sql = "DELETE FROM item_venda WHERE id_venda=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir itens da venda: " + e.getMessage());
            return false;
        }
    }
}