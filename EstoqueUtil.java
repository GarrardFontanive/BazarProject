package com.classes.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EstoqueUtil extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Se a linha estiver selecionada, mantém a cor padrão de seleção (azul)
        if (isSelected) {
            return c;
        }

        try {
            // Pega o valor da coluna de Quantidade (Coluna 4)
            Object qtdObj = table.getModel().getValueAt(row, 4);

            if (qtdObj != null) {
                int qtd = Integer.parseInt(qtdObj.toString());

                if (qtd <= 0) {
                    // ESTOQUE ZERO: Fundo Vermelho Claro, Texto Vermelho
                    c.setBackground(new Color(255, 200, 200));
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (qtd <= 5) {
                    // ALERTA (Baixo): Fundo Amarelo
                    c.setBackground(new Color(255, 250, 205));
                    c.setForeground(Color.BLACK);
                } else {
                    // NORMAL: Branco
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }

            // Alinha números à direita (Preço e Qtd)
            if (column == 3 || column == 4) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

        } catch (Exception e) {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}