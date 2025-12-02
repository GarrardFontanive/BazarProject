package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.ClienteDTO;
import com.classes.DTO.VendaDTO;
import com.classes.enums.FormaPagamento;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class VendaDAO implements ICrudDAO<VendaDTO> {

    private List<VendaDTO> extrairVendasDoResultSet(ResultSet rs) throws SQLException {
        List<VendaDTO> lista = new ArrayList<>();
        while (rs.next()) {
            VendaDTO v = new VendaDTO();
            v.setId_venda(rs.getInt("id_venda"));
            v.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
            v.setDesconto(rs.getDouble("desconto"));
            v.setMotivoDesconto(rs.getString("motivo_desconto"));
            v.setTotal(rs.getDouble("total"));
            v.setAdminNome(rs.getString("admin_nome"));

            String pgtoStr = rs.getString("forma_pagamento");
            try {
                v.setFormaPagamento(FormaPagamento.valueOf(pgtoStr));
            } catch (Exception e) {
                v.setFormaPagamento(FormaPagamento.OUTRO);
            }

            ClienteDTO c = new ClienteDTO(
                    rs.getInt("id_cliente"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone")
            );
            v.setCliente(c);

            lista.add(v);
        }
        return lista;
    }

    @Override
    public boolean inserir(VendaDTO venda) {
        return inserirVendaERetornarId(venda) > 0;
    }

    public int inserirVendaERetornarId(VendaDTO venda) {
        String sql = "INSERT INTO venda (id_cliente, desconto, motivo_desconto, total, forma_pagamento, admin_nome, data_hora) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, venda.getCliente().getId_cliente());
            stmt.setDouble(2, venda.getDesconto());
            stmt.setString(3, venda.getMotivoDesconto());
            stmt.setDouble(4, venda.getTotal());
            stmt.setString(5, venda.getFormaPagamento().name());
            stmt.setString(6, venda.getAdminNome());
            stmt.setTimestamp(7, Timestamp.valueOf(venda.getDataHora()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir venda: " + e.getMessage());
        }
        return -1;
    }

    public List<VendaDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT v.*, c.nome, c.cpf, c.telefone " +
                "FROM venda v " +
                "JOIN cliente c ON v.id_cliente = c.id_cliente " +
                "WHERE v.data_hora BETWEEN ? AND ? " +
                "ORDER BY v.data_hora DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(dataInicio.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(dataFim.atTime(23, 59, 59, 999000000)));

            ResultSet rs = stmt.executeQuery();
            return extrairVendasDoResultSet(rs);

        } catch (SQLException e) {
            System.err.println("Erro ao listar vendas por período: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public VendaDTO buscarPorId(int idVenda) {
        String sql = "SELECT v.*, c.nome, c.cpf, c.telefone FROM venda v JOIN cliente c ON v.id_cliente = c.id_cliente WHERE v.id_venda = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            ResultSet rs = stmt.executeQuery();

            List<VendaDTO> vendas = extrairVendasDoResultSet(rs);
            return vendas.isEmpty() ? null : vendas.get(0);

        } catch (SQLException e) {
            System.err.println("Erro ao buscar venda por ID: " + e.getMessage());
            return null;
        }
    }

    public double getTotalVendasHoje() {
        String sql = "SELECT SUM(total) as total_dia FROM venda WHERE DATE(data_hora) = CURRENT_DATE";
        double total = 0.0;

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getDouble("total_dia");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar total do dia: " + e.getMessage());
        }
        return total;
    }

    @Override
    public boolean atualizar(VendaDTO objeto) {
        return false;
    }

    @Override
    public boolean excluir(Object id) {
        return false;
    }

    @Override
    public List<VendaDTO> listarTodos() {
        return new ArrayList<>();
    }
}