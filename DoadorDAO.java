package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.DoadorDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoadorDAO implements ICrudDAO<DoadorDTO> {

    @Override
    public boolean inserir(DoadorDTO doador) {
        String sql = "INSERT INTO doador (nome, telefone, documento, tipo_documento) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doador.getNome());
            stmt.setString(2, doador.getTelefone());
            stmt.setString(3, doador.getDocumento());
            stmt.setString(4, doador.getTipoDocumento());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir doador: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(DoadorDTO doador) {
        String sql = "UPDATE doador SET nome=?, telefone=?, documento=?, tipo_documento=? WHERE id_doador=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doador.getNome());
            stmt.setString(2, doador.getTelefone());
            stmt.setString(3, doador.getDocumento());
            stmt.setString(4, doador.getTipoDocumento());
            stmt.setInt(5, doador.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar doador: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Object id) {
        String sql = "DELETE FROM doador WHERE id_doador=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (int) id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir doador (possui vínculos?): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<DoadorDTO> listarTodos() {
        List<DoadorDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM doador ORDER BY nome";
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DoadorDTO d = new DoadorDTO();
                d.setId(rs.getInt("id_doador"));
                d.setNome(rs.getString("nome"));
                d.setTelefone(rs.getString("telefone"));
                d.setDocumento(rs.getString("documento"));
                d.setTipoDocumento(rs.getString("tipo_documento"));
                d.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
                lista.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar doadores: " + e.getMessage());
        }
        return lista;
    }

    public DoadorDTO buscarPorDocumento(String documento) {
        String sql = "SELECT * FROM doador WHERE documento=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DoadorDTO d = new DoadorDTO();
                d.setId(rs.getInt("id_doador"));
                d.setNome(rs.getString("nome"));
                d.setTelefone(rs.getString("telefone"));
                d.setDocumento(rs.getString("documento"));
                d.setTipoDocumento(rs.getString("tipo_documento"));
                return d;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar doador: " + e.getMessage());
        }
        return null;
    }
}