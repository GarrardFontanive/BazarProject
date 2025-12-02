package com.classes.util;

import javax.swing.*;
import java.awt.*;

public class UiUtil {

    // Caminho da sua logo
    private static final String CAMINHO_LOGO = "/imagens/logo3.png";

    public static void configurarIconeJanela(JFrame frame) {
        try {
            // Carrega a imagem
            ImageIcon icon = new ImageIcon(UiUtil.class.getResource(CAMINHO_LOGO));
            Image img = icon.getImage();

            // Define o ícone da janela (Funciona em Windows/Linux na barra de tarefas)
            frame.setIconImage(img);

            // Tenta definir o ícone no Dock do Mac (Java 9+)
            try {
                Taskbar taskbar = Taskbar.getTaskbar();
                taskbar.setIconImage(img);
            } catch (UnsupportedOperationException e) {
                // O sistema não suporta (não é Mac ou erro de versão), ignora silenciosamente
            }

        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone: " + e.getMessage());
        }
    }
}