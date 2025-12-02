package com.classes.util;

import com.classes.DTO.UsuarioDTO;

public class SessaoUsuario {
    private static SessaoUsuario instance;
    private UsuarioDTO usuarioLogado;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    public UsuarioDTO getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(UsuarioDTO usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    // --- O MÉTODO QUE FALTAVA ---
    public void logout() {
        this.usuarioLogado = null;
    }
}