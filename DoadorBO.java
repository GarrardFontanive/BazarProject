package com.classes.BO;

import com.classes.DAO.DoadorDAO;
import com.classes.DTO.DoadorDTO;
import com.classes.util.ValidadorUtil;
import java.util.List;

public class DoadorBO {
    private DoadorDAO doadorDAO = new DoadorDAO();

    public boolean cadastrar(DoadorDTO doador) {
        if (!ValidadorUtil.isValidNome(doador.getNome())) return false;

        String docLimpo = doador.getDocumento().replaceAll("[^0-9]", "");
        if (docLimpo.isEmpty()) return false;

        if (doadorDAO.buscarPorDocumento(docLimpo) != null) return false;

        doador.setDocumento(docLimpo);
        doador.setTelefone(doador.getTelefone().replaceAll("[^0-9]", ""));

        return doadorDAO.inserir(doador);
    }

    public boolean atualizar(DoadorDTO doador) {
        if (!ValidadorUtil.isValidNome(doador.getNome())) return false;

        String docLimpo = doador.getDocumento().replaceAll("[^0-9]", "");
        if (docLimpo.isEmpty()) return false;

        doador.setDocumento(docLimpo);
        doador.setTelefone(doador.getTelefone().replaceAll("[^0-9]", ""));

        return doadorDAO.atualizar(doador);
    }

    public boolean excluir(int id) {
        return doadorDAO.excluir(id);
    }

    public List<DoadorDTO> listarTodos() {
        return doadorDAO.listarTodos();
    }
}