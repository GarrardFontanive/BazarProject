package com.classes.DAO;

import com.classes.Conexao.Conexao;
import com.classes.DTO.UsuarioDTO;
import java.sql.*;

public class UsuarioDAO {

    public UsuarioDTO autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UsuarioDTO(
                        rs.getInt("id_usuario"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("nome_completo"),
                        rs.getBoolean("admin")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
        }
        return null;
    }
}