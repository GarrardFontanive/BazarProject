package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.DoadorDTO;
import com.classes.DTO.MovimentacaoEstoqueDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueDAO {

    public boolean registrarMovimentacao(MovimentacaoEstoqueDTO mov) {
        String sql = "INSERT INTO movimentacao_estoque (codigo_barras, id_doador, tipo_movimento, quantidade, data_movimentacao, observacao) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mov.getProduto().getCodigoBarras());

            if (mov.getDoador() != null) {
                stmt.setInt(2, mov.getDoador().getId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setString(3, mov.getTipoMovimento());
            stmt.setInt(4, mov.getQuantidade());
            stmt.setTimestamp(5, Timestamp.valueOf(mov.getDataMovimentacao()));
            stmt.setString(6, mov.getObservacao());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao registrar movimentação: " + e.getMessage());
            return false;
        }
    }

    // --- NOVO MÉTODO: LER HISTÓRICO ---
    public List<MovimentacaoEstoqueDTO> listarHistoricoPorProduto(String codigoBarras) {
        List<MovimentacaoEstoqueDTO> lista = new ArrayList<>();
        // Faz um LEFT JOIN para trazer o nome do doador, se houver
        String sql = "SELECT m.*, d.nome as nome_doador " +
                "FROM movimentacao_estoque m " +
                "LEFT JOIN doador d ON m.id_doador = d.id_doador " +
                "WHERE m.codigo_barras = ? " +
                "ORDER BY m.data_movimentacao DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                MovimentacaoEstoqueDTO mov = new MovimentacaoEstoqueDTO();
                mov.setTipoMovimento(rs.getString("tipo_movimento"));
                mov.setQuantidade(rs.getInt("quantidade"));
                mov.setDataMovimentacao(rs.getTimestamp("data_movimentacao").toLocalDateTime());
                mov.setObservacao(rs.getString("observacao"));

                // Se tiver doador, cria o DTO apenas com o nome para exibir
                String nomeDoador = rs.getString("nome_doador");
                if (nomeDoador != null) {
                    DoadorDTO d = new DoadorDTO();
                    d.setNome(nomeDoador);
                    mov.setDoador(d);
                }

                lista.add(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}