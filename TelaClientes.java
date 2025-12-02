package com.telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.util.List;
import com.classes.DTO.ClienteDTO;
import com.classes.BO.ClienteBO;

public class TelaClientes extends JFrame {

    private JTextField txtNome;
    private JFormattedTextField txtCpf, txtTelefone;
    private JButton btnSalvar, btnListar, btnExcluir, btnLimpar, btnEditar;
    private JTable tabela;
    private DefaultTableModel modelo;
    private ClienteBO clienteBO = new ClienteBO();

    private int idEmEdicao = -1;

    private static final Color COR_MARCA = new Color(161, 3, 3);
    private static final Color COR_FUNDO_PAINEL = new Color(250, 250, 250);

    public TelaClientes() {
        setTitle("Gerenciar Clientes");
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
        painelTopo.add(new JLabel("CPF:"), gbc);
        try { txtCpf = new JFormattedTextField(new MaskFormatter("###.###.###-##")); } catch (Exception e) {}
        gbc.gridx = 1;
        painelTopo.add(txtCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelTopo.add(new JLabel("Telefone:"), gbc);
        try { txtTelefone = new JFormattedTextField(new MaskFormatter("(##) # ####-####")); } catch (Exception e) {}
        gbc.gridx = 1;
        painelTopo.add(txtTelefone, gbc);

        JPanel pnlAcoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAcoesTopo.setOpaque(false);

        btnSalvar = new JButton("Salvar");
        estilizarBotao(btnSalvar, new Color(60, 179, 113));
        btnSalvar.addActionListener(e -> salvarCliente());
        pnlAcoesTopo.add(btnSalvar);

        btnListar = new JButton("Listar Todos");
        estilizarBotao(btnListar, new Color(100, 149, 237));
        btnListar.addActionListener(e -> listarClientes());
        pnlAcoesTopo.add(btnListar);

        btnLimpar = new JButton("Novo / Limpar");
        estilizarBotao(btnLimpar, Color.GRAY);
        btnLimpar.addActionListener(e -> limparCampos());
        pnlAcoesTopo.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painelTopo.add(pnlAcoesTopo, gbc);

        add(painelTopo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Telefone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
        btnExcluir.addActionListener(e -> excluirCliente());
        painelInferior.add(btnExcluir);

        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
        listarClientes();
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    private void salvarCliente() {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setNome(txtNome.getText());
        cliente.setCpf(txtCpf.getText().replaceAll("[^0-9]", ""));
        cliente.setTelefone(txtTelefone.getText().replaceAll("[^0-9]", ""));

        boolean sucesso;
        if (idEmEdicao == -1) {
            sucesso = clienteBO.cadastrar(cliente);
        } else {
            cliente.setId_cliente(idEmEdicao);
            sucesso = clienteBO.atualizar(cliente);
        }

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
            listarClientes();
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar. Verifique os dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarParaEdicao() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }

        idEmEdicao = (int) modelo.getValueAt(linha, 0);
        txtNome.setText((String) modelo.getValueAt(linha, 1));
        txtCpf.setText((String) modelo.getValueAt(linha, 2));
        txtTelefone.setText((String) modelo.getValueAt(linha, 3));

        btnSalvar.setText("Atualizar");
        estilizarBotao(btnSalvar, new Color(255, 140, 0));
    }

    private void listarClientes() {
        modelo.setRowCount(0);
        List<ClienteDTO> lista = clienteBO.listarTodos();
        for (ClienteDTO c : lista) {
            modelo.addRow(new Object[]{c.getId_cliente(), c.getNome(), c.getCpf(), c.getTelefone()});
        }
    }

    private void excluirCliente() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            int id = (int) modelo.getValueAt(linha, 0);
            if (clienteBO.excluir(id)) {
                listarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtCpf.setValue(null);
        txtTelefone.setValue(null);
        idEmEdicao = -1;
        btnSalvar.setText("Salvar");
        estilizarBotao(btnSalvar, new Color(60, 179, 113));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaClientes::new);
    }
}