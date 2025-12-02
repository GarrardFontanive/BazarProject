package com.telas;

import com.classes.BO.VendaBO;
import com.classes.DTO.VendaDTO;
import com.classes.util.ImpressaoUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TelaRelatorios extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private VendaBO vendaBO = new VendaBO();
    private JTextField txtDataInicio, txtDataFim;
    private JLabel lblTotalPeriodo;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COR_MARCA = new Color(196, 5, 5);
    private static final Color COR_FUNDO_PAINEL = new Color(250, 250, 250);

    public TelaRelatorios() {
        setTitle("Relatório de Vendas");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COR_FUNDO_PAINEL);

        JPanel painelNorte = new JPanel();
        painelNorte.setLayout(new BoxLayout(painelNorte, BoxLayout.Y_AXIS));
        painelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelNorte.setBackground(COR_FUNDO_PAINEL);

        JPanel painelDatas = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelDatas.setBackground(COR_FUNDO_PAINEL);

        TitledBorder bordaDatas = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_MARCA, 2),
                "Filtros de Data",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), COR_MARCA
        );
        painelDatas.setBorder(bordaDatas);

        Font fontePadrao = new Font("Segoe UI", Font.PLAIN, 14);

        painelDatas.add(new JLabel("Data Início:"));
        txtDataInicio = new JTextField(8);
        txtDataInicio.setFont(fontePadrao);
        txtDataInicio.setText(LocalDate.now().minusDays(30).format(dtf));
        painelDatas.add(txtDataInicio);

        painelDatas.add(new JLabel("Data Fim:"));
        txtDataFim = new JTextField(8);
        txtDataFim.setFont(fontePadrao);
        txtDataFim.setText(LocalDate.now().format(dtf));
        painelDatas.add(txtDataFim);

        JButton btnFiltrar = new JButton("Filtrar");
        estilizarBotao(btnFiltrar, new Color(60, 179, 113));
        btnFiltrar.addActionListener(e -> carregarRelatorio());
        painelDatas.add(btnFiltrar);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelBotoes.setBackground(COR_FUNDO_PAINEL);

        JButton btnHoje = new JButton("Hoje");
        estilizarBotao(btnHoje, Color.GRAY);
        btnHoje.addActionListener(e -> filtrarHoje());
        painelBotoes.add(btnHoje);

        JButton btnExportarExcel = new JButton("Exportar Excel");
        estilizarBotao(btnExportarExcel, new Color(34, 139, 34));
        btnExportarExcel.addActionListener(e -> exportarParaHtmlXls());
        painelBotoes.add(btnExportarExcel);

        JButton btnImprimirRelatorio = new JButton("Imprimir Relatório");
        estilizarBotao(btnImprimirRelatorio, new Color(0, 80, 150));
        btnImprimirRelatorio.addActionListener(e -> imprimirRelatorio());
        painelBotoes.add(btnImprimirRelatorio);

        painelNorte.add(painelDatas);
        painelNorte.add(painelBotoes);
        add(painelNorte, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Data", "Cliente", "CPF", "Total (R$)", "Forma Pgto"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelInferior.setBackground(COR_FUNDO_PAINEL);

        lblTotalPeriodo = new JLabel("Total no período: R$ 0,00");
        lblTotalPeriodo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalPeriodo.setForeground(COR_MARCA);

        JButton btnReimprimir = new JButton("Reimprimir Cupom");
        estilizarBotao(btnReimprimir, new Color(255, 140, 0));
        btnReimprimir.addActionListener(e -> reimprimirCupom());

        painelInferior.add(lblTotalPeriodo, BorderLayout.WEST);
        painelInferior.add(btnReimprimir, BorderLayout.EAST);
        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
        carregarRelatorio();
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    private void carregarRelatorio() {
        try {
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText(), dtf);
            LocalDate fim = LocalDate.parse(txtDataFim.getText(), dtf);
            modeloTabela.setRowCount(0);
            double totalPeriodo = 0;

            for (VendaDTO v : vendaBO.listarPorPeriodo(inicio, fim)) {
                modeloTabela.addRow(new Object[]{
                        v.getId_venda(),
                        v.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")),
                        v.getCliente().getNome(),
                        v.getCliente().getCpf(),
                        String.format("%.2f", v.getTotal()),
                        v.getFormaPagamento().getDescricao()
                });
                totalPeriodo += v.getTotal();
            }
            lblTotalPeriodo.setText(String.format("Total no período: R$ %.2f", totalPeriodo));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarHoje() {
        txtDataInicio.setText(LocalDate.now().format(dtf));
        txtDataFim.setText(LocalDate.now().format(dtf));
        carregarRelatorio();
    }

    private void imprimirRelatorio() {
        StringBuilder sb = new StringBuilder();
        sb.append("      RELATORIO DE VENDAS      \n");
        sb.append("-------------------------------\n");
        sb.append(String.format("Periodo: %s a %s\n\n", txtDataInicio.getText(), txtDataFim.getText()));
        sb.append(String.format("%-5s %-16s %9s\n", "ID", "DATA", "TOTAL"));

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            String id = modeloTabela.getValueAt(i, 0).toString();
            String data = modeloTabela.getValueAt(i, 1).toString();
            String total = modeloTabela.getValueAt(i, 4).toString();
            sb.append(String.format("%-5s %-16s %9s\n", id, data, total));
        }
        sb.append("-------------------------------\n");
        sb.append(lblTotalPeriodo.getText());

        try {
            ImpressaoUtil.imprimirRelatorioGerencial(sb.toString());
            JOptionPane.showMessageDialog(this, "Enviado para impressora!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void reimprimirCupom() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idVenda = (int) modeloTabela.getValueAt(linha, 0);
        try {
            VendaDTO vendaCompleta = vendaBO.buscarVendaCompletaPorId(idVenda);
            if (vendaCompleta == null || vendaCompleta.getItens() == null || vendaCompleta.getItens().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ImpressaoUtil.imprimirCupom(vendaCompleta);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro impressão: " + ex.getMessage());
        }
    }

    private void exportarParaHtmlXls() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Relatorio_Vendas.xls"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
                DefaultTableModel model = (DefaultTableModel) tabela.getModel();
                bw.write("<html><table border='1'><thead><tr>");
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bw.write("<th>" + model.getColumnName(i) + "</th>");
                }
                bw.write("</tr></thead><tbody>");
                for (int i = 0; i < model.getRowCount(); i++) {
                    bw.write("<tr>");
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        bw.write("<td>" + model.getValueAt(i, j) + "</td>");
                    }
                    bw.write("</tr>");
                }
                bw.write("</tbody></table></html>");
                JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}