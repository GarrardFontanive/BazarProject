package com.classes.DTO;

public class UsuarioDTO {
    private int id;
    private String login;
    private String senha;
    private String nomeCompleto;
    private boolean admin;

    public UsuarioDTO() {

    }

    public UsuarioDTO(int id, String login, String senha, String nomeCompleto, boolean admin) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nomeCompleto = nomeCompleto;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UsuarioDTO{");
        sb.append("id=").append(id);
        sb.append(", login='").append(login).append('\'');
        sb.append(", senha='").append(senha).append('\'');
        sb.append(", nomeCompleto='").append(nomeCompleto).append('\'');
        sb.append(", admin=").append(admin);
        sb.append('}');
        return sb.toString();
    }
}