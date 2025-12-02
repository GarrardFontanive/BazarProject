package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.ClienteDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements ICrudDAO<ClienteDTO> {

    public ClienteDTO buscarPorCpf(String cpf) {
        String sql = "SELECT id_cliente, nome, cpf, telefone FROM cliente WHERE cpf=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ClienteDTO(
                        rs.getInt("id_cliente"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por CPF: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean inserir(ClienteDTO cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, ativo) VALUES (?, ?, ?, 1)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(ClienteDTO cliente) {
        String sql = "UPDATE cliente SET nome=?, cpf=?, telefone=? WHERE id_cliente=?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getTelefone());
            stmt.setInt(4, cliente.getId_cliente());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Object id) {
        int idCliente = (int) id;
        String sql = "UPDATE cliente SET ativo = 0 WHERE id_cliente = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ClienteDTO> listarTodos() {
        List<ClienteDTO> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nome, cpf, telefone FROM cliente WHERE ativo = 1";
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ClienteDTO c = new ClienteDTO(
                        rs.getInt("id_cliente"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("ERRO SQL AO LISTAR: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}