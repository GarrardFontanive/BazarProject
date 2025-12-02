package com.classes.BO;

import com.classes.DAO.ClienteDAO;
import com.classes.DTO.ClienteDTO;
import java.util.List;
import com.classes.util.ValidadorUtil;

public class ClienteBO {
    private ClienteDAO clienteDAO = new ClienteDAO();

    public boolean cadastrar(ClienteDTO cliente) {
        String cpfLimpo = cliente.getCpf().replaceAll("[^0-9]", "");
        String telLimpo = cliente.getTelefone().replaceAll("[^0-9]", "");

        if (!ValidadorUtil.isValidNome(cliente.getNome())) return false;
        if (!ValidadorUtil.isValidCpf(cpfLimpo)) return false;
        if (!ValidadorUtil.isValidTelefone(telLimpo)) return false;

        if (clienteDAO.buscarPorCpf(cpfLimpo) != null) return false;

        cliente.setCpf(cpfLimpo);
        cliente.setTelefone(telLimpo);

        return clienteDAO.inserir(cliente);
    }

    public boolean atualizar(ClienteDTO cliente) {
        String cpfLimpo = cliente.getCpf().replaceAll("[^0-9]", "");
        String telLimpo = cliente.getTelefone().replaceAll("[^0-9]", "");

        if (!ValidadorUtil.isValidNome(cliente.getNome())) return false;
        if (!ValidadorUtil.isValidCpf(cpfLimpo)) return false;
        if (!ValidadorUtil.isValidTelefone(telLimpo)) return false;

        cliente.setCpf(cpfLimpo);
        cliente.setTelefone(telLimpo);

        return clienteDAO.atualizar(cliente);
    }

    public boolean excluir(int id) {
        return clienteDAO.excluir(id);
    }

    public List<ClienteDTO> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public ClienteDTO buscarPorCpf(String cpf) {
        return clienteDAO.buscarPorCpf(cpf.replaceAll("[^0-9]", ""));
    }
}