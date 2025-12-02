package com.telas;

import com.classes.BO.DoadorBO;
import com.classes.DTO.DoadorDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.util.List;

public class TelaDoadores extends JFrame {

    private JTextField txtNome;
    private JFormattedTextField txtDocumento;
    private JFormattedTextField txtTelefone;
    private JComboBox<String> cmbTipoDoc;
    private JButton btnSalvar, btnListar, btnExcluir, btnLimpar, btnEditar;
    private JTable tabela;
    private DefaultTableModel modelo;
    private DoadorBO doadorBO = new DoadorBO();

    private int idEmEdicao = -1;

    private static final Color COR_MARCA = new Color(196, 5, 5);
    private static final Color COR_FUNDO_PAINEL = new Color(250, 250, 250);

    public TelaDoadores() {
        setTitle("Gerenciar Doadores");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COR_FUNDO_PAINEL);

        JPanel painelTopo = new JPanel(new GridBagLayout());
        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_MARCA, 2),
                "Cadastro / Edição",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), COR_MARCA
        );
        painelTopo.setBorder(borda);
        painelTopo.setBackground(COR_FUNDO_PAINEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painelTopo.add(new JLabel("Nome:"), gbc);
        txtNome = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 1.0;
        painelTopo.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelTopo.add(new JLabel("Tipo:"), gbc);

        cmbTipoDoc = new JComboBox<>(new String[]{"CPF", "CNPJ"});
        txtDocumento = new JFormattedTextField();
        cmbTipoDoc.addActionListener(e -> atualizarMascara());

        gbc.gridx = 1;
        JPanel pnlDoc = new JPanel(new BorderLayout(5, 0));
        pnlDoc.setOpaque(false);
        pnlDoc.add(cmbTipoDoc, BorderLayout.WEST);
        pnlDoc.add(txtDocumento, BorderLayout.CENTER);
        painelTopo.add(pnlDoc, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelTopo.add(new JLabel("Telefone:"), gbc);
        try {
            MaskFormatter mask = new MaskFormatter("(##) # ####-####");
            mask.setPlaceholderCharacter('_');
            txtTelefone = new JFormattedTextField(mask);
        } catch (Exception e) {
            txtTelefone = new JFormattedTextField();
        }
        gbc.gridx = 1;
        painelTopo.add(txtTelefone, gbc);

        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBotoes.setOpaque(false);

        btnSalvar = new JButton("Salvar");
        estilizarBotao(btnSalvar, new Color(60, 179, 113));
        btnSalvar.addActionListener(e -> salvar());
        pnlBotoes.add(btnSalvar);

        btnLimpar = new JButton("Novo / Limpar");
        estilizarBotao(btnLimpar, Color.GRAY);
        btnLimpar.addActionListener(e -> limpar());
        pnlBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painelTopo.add(pnlBotoes, gbc);

        add(painelTopo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Tipo", "Documento", "Telefone"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelInferior.setBackground(COR_FUNDO_PAINEL);

        btnEditar = new JButton("Editar Selecionado");
        estilizarBotao(btnEditar, new Color(255, 140, 0));
        btnEditar.addActionListener(e -> carregarParaEdicao());
        painelInferior.add(btnEditar);

        btnExcluir = new JButton("Excluir");
        estilizarBotao(btnExcluir, new Color(220, 20, 60));
        btnExcluir.addActionListener(e -> excluir());
        painelInferior.add(btnExcluir);

        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
        atualizarMascara();
        listar();
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    private void atualizarMascara() {
        txtDocumento.setValue(null);
        String tipo = (String) cmbTipoDoc.getSelectedItem();
        try {
            MaskFormatter mask;
            if ("CNPJ".equals(tipo)) {
                mask = new MaskFormatter("##.###.###/####-##");
            } else {
                mask = new MaskFormatter("###.###.###-##");
            }
            mask.setPlaceholderCharacter('_');
            txtDocumento.setFormatterFactory(new DefaultFormatterFactory(mask));
        } catch (Exception e) {}
    }

    private void salvar() {
        DoadorDTO d = new DoadorDTO();
        d.setNome(txtNome.getText());
        d.setTipoDocumento((String) cmbTipoDoc.getSelectedItem());
        d.setDocumento(txtDocumento.getText());
        d.setTelefone(txtTelefone.getText());

        boolean sucesso;
        if (idEmEdicao == -1) {
            sucesso = doadorBO.cadastrar(d);
        } else {
            d.setId(idEmEdicao);
            sucesso = doadorBO.atualizar(d);
        }

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
            listar();
            limpar();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarParaEdicao() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um doador.");
            return;
        }

        idEmEdicao = (int) modelo.getValueAt(linha, 0);
        txtNome.setText((String) modelo.getValueAt(linha, 1));
        String tipo = (String) modelo.getValueAt(linha, 2);
        cmbTipoDoc.setSelectedItem(tipo);
        atualizarMascara();
        txtDocumento.setText((String) modelo.getValueAt(linha, 3));
        txtTelefone.setText((String) modelo.getValueAt(linha, 4));

        btnSalvar.setText("Atualizar");
        estilizarBotao(btnSalvar, new Color(255, 140, 0));
    }

    private void listar() {
        modelo.setRowCount(0);
        List<DoadorDTO> lista = doadorBO.listarTodos();
        for (DoadorDTO d : lista) {
            modelo.addRow(new Object[]{d.getId(), d.getNome(), d.getTipoDocumento(), d.getDocumento(), d.getTelefone()});
        }
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            int id = (int) modelo.getValueAt(linha, 0);
            if (doadorBO.excluir(id)) {
                listar();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpar() {
        txtNome.setText("");
        txtDocumento.setValue(null);
        txtTelefone.setValue(null);
        cmbTipoDoc.setSelectedIndex(0);
        idEmEdicao = -1;
        btnSalvar.setText("Salvar");
        estilizarBotao(btnSalvar, new Color(60, 179, 113));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaDoadores::new);
    }
}