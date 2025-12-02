package com.telas;

import com.classes.BO.EstoqueBO;
import com.classes.DTO.MovimentacaoEstoqueDTO;
import com.classes.DTO.ProdutoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaHistoricoProduto extends JDialog {

    private JTable tabela;
    private DefaultTableModel modelo;
    private EstoqueBO estoqueBO = new EstoqueBO();
    private ProdutoDTO produto;

    private static final Color COR_MARCA = new Color(196, 5, 5);

    public TelaHistoricoProduto(Frame parent, ProdutoDTO produto) {
        super(parent, "Histórico de Movimentação", true);
        this.produto = produto;

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel pnlTopo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlTopo.setBackground(COR_MARCA);
        JLabel lblTitulo = new JLabel("Histórico: " + produto.getNome());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        pnlTopo.add(lblTitulo);
        add(pnlTopo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Data", "Tipo", "Qtd", "Origem / Obs"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(25);

        carregarDados();

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel pnlSul = new JPanel();
        pnlSul.add(btnFechar);
        add(pnlSul, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        List<MovimentacaoEstoqueDTO> lista = estoqueBO.buscarHistorico(produto.getCodigoBarras());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

        for (MovimentacaoEstoqueDTO mov : lista) {
            String origem = mov.getObservacao();
            if ("ENTRADA".equals(mov.getTipoMovimento()) && mov.getDoador() != null) {
                origem = "Doador: " + mov.getDoador().getNome();
            } else if ("SAIDA".equals(mov.getTipoMovimento())) {
                origem = "Venda";
            }

            modelo.addRow(new Object[]{
                    mov.getDataMovimentacao().format(dtf),
                    mov.getTipoMovimento(),
                    mov.getQuantidade(),
                    origem
            });
        }
    }
}