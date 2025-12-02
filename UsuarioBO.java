package com.classes.BO;

import com.classes.DAO.UsuarioDAO;
import com.classes.DTO.UsuarioDTO;
import com.classes.util.SessaoUsuario;

public class UsuarioBO {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean logar(String login, String senha) {
        if (login == null || login.isEmpty() || senha == null || senha.isEmpty()) {
            return false;
        }

        UsuarioDTO usuario = usuarioDAO.autenticar(login, senha);

        if (usuario != null) {
            SessaoUsuario.getInstance().setUsuarioLogado(usuario);
            return true;
        }

        return false;
    }
}