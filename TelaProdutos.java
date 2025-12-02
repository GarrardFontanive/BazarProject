package com.telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import com.classes.DTO.ProdutoDTO;
import com.classes.BO.ProdutoBO;
import com.classes.enums.CategoriaProduto;
import com.classes.util.GeradorCodigoBarrasUtil;
import com.classes.util.ImpressaoUtil;
import com.classes.util.EstoqueUtil;

public class TelaProdutos extends JFrame {

    private JTextField txtNome, txtPreco, txtCodigoBarras, txtEstoque;
    private JComboBox<CategoriaProduto> cmbCategoria;
    private JCheckBox chkComDefeito;
    private JButton btnCadastrar, btnListar, btnExcluir, btnAjustarEstoque, btnImprimirEtiqueta, btnEditar, btnHistorico;
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private ProdutoBO produtoBO = new ProdutoBO();

    private boolean modoEdicao = false;

    private static final Color COR_MARCA = new Color(161, 3, 3);
    private static final Color COR_FUNDO_PAINEL = new Color(250, 250, 250);

    public TelaProdutos() {
        setTitle("Gerenciar Produtos e Estoque");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COR_FUNDO_PAINEL);

        JPanel painelCadastro = new JPanel(new GridLayout(7, 2, 10, 10));

        TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_MARCA, 2),
                "Cadastro de Produto",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), COR_MARCA
        );
        painelCadastro.setBorder(borda);
        painelCadastro.setBackground(COR_FUNDO_PAINEL);

        painelCadastro.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        painelCadastro.add(txtNome);

        painelCadastro.add(new JLabel("Categoria:"));
        cmbCategoria = new JComboBox<>(CategoriaProduto.values());
        painelCadastro.add(cmbCategoria);

        painelCadastro.add(new JLabel("Preço (R$):"));
        txtPreco = new JTextField();
        painelCadastro.add(txtPreco);

        painelCadastro.add(new JLabel("Código de Barras:"));
        txtCodigoBarras = new JTextField();
        painelCadastro.add(txtCodigoBarras);

        painelCadastro.add(new JLabel("Estoque Inicial:"));
        txtEstoque = new JTextField("1");
        painelCadastro.add(txtEstoque);

        painelCadastro.add(new JLabel("Item com Defeito?"));
        chkComDefeito = new JCheckBox("Marcar se sim");
        chkComDefeito.setBackground(COR_FUNDO_PAINEL);
        painelCadastro.add(chkComDefeito);

        JPanel pnlBotoesTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBotoesTopo.setOpaque(false);

        btnCadastrar = new JButton("Cadastrar");
        estilizarBotao(btnCadastrar, new Color(60, 179, 113));
        btnCadastrar.addActionListener(e -> cadastrarProduto());
        pnlBotoesTopo.add(btnCadastrar);

        JButton btnLimpar = new JButton("Limpar");
        estilizarBotao(btnLimpar, Color.GRAY);
        btnLimpar.addActionListener(e -> limparCampos());
        pnlBotoesTopo.add(btnLimpar);

        painelCadastro.add(pnlBotoesTopo);
        painelCadastro.add(new JLabel(""));

        add(painelCadastro, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new String[]{"Código", "Nome", "Categoria", "Preço", "Qtd", "Defeito"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setRowHeight(25);

        try {
            tabelaProdutos.setDefaultRenderer(Object.class, new EstoqueUtil());
        } catch (Exception e) {
            System.err.println("Erro ao carregar renderizador: " + e.getMessage());
        }

        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(100);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(50);

        JScrollPane scroll = new JScrollPane(tabelaProdutos);
        add(scroll, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelInferior.setBackground(COR_FUNDO_PAINEL);

        btnEditar = new JButton("Editar Dados");
        estilizarBotao(btnEditar, new Color(255, 140, 0));
        btnEditar.addActionListener(e -> carregarParaEdicao());
        painelInferior.add(btnEditar);

        btnAjustarEstoque = new JButton("Ajustar Estoque");
        estilizarBotao(btnAjustarEstoque, new Color(34, 139, 34));
        btnAjustarEstoque.addActionListener(e -> ajustarEstoqueSelecionado());
        painelInferior.add(btnAjustarEstoque);

        btnHistorico = new JButton("Ver Histórico");
        estilizarBotao(btnHistorico, new Color(70, 130, 180));
        btnHistorico.addActionListener(e -> abrirHistorico());
        painelInferior.add(btnHistorico);

        btnImprimirEtiqueta = new JButton("Imprimir Etiqueta");
        estilizarBotao(btnImprimirEtiqueta, new Color(100, 100, 100));
        btnImprimirEtiqueta.addActionListener(e -> imprimirEtiqueta());
        painelInferior.add(btnImprimirEtiqueta);

        btnExcluir = new JButton("Excluir");
        estilizarBotao(btnExcluir, new Color(220, 20, 60));
        btnExcluir.addActionListener(e -> excluirProduto());
        painelInferior.add(btnExcluir);

        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
        listarProdutos();
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void cadastrarProduto() {
        try {
            ProdutoDTO produto = new ProdutoDTO();
            produto.setNome(txtNome.getText());
            produto.setCategoria((CategoriaProduto) cmbCategoria.getSelectedItem());
            String precoStr = txtPreco.getText().replace("R$", "").replace(",", ".").trim();
            produto.setPrecoUnitario(Double.parseDouble(precoStr));
            produto.setCodigoBarras(txtCodigoBarras.getText());
            produto.setComDefeito(chkComDefeito.isSelected());

            try {
                int qtd = Integer.parseInt(txtEstoque.getText());
                produto.setQuantidadeEstoque(qtd);
            } catch (NumberFormatException e) {
                produto.setQuantidadeEstoque(1);
            }

            boolean sucesso;
            if (modoEdicao) {
                sucesso = produtoBO.atualizar(produto);
            } else {
                sucesso = produtoBO.cadastrar(produto);
            }

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Sucesso!");
                listarProdutos();
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha na operação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarParaEdicao() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.");
            return;
        }

        String codigo = (String) modeloTabela.getValueAt(linha, 0);
        ProdutoDTO p = produtoBO.buscarPorCodigoBarras(codigo);

        if (p != null) {
            txtCodigoBarras.setText(p.getCodigoBarras());
            txtCodigoBarras.setEditable(false);
            txtNome.setText(p.getNome());
            txtPreco.setText(String.valueOf(p.getPrecoUnitario()));
            cmbCategoria.setSelectedItem(p.getCategoria());
            chkComDefeito.setSelected(p.isComDefeito());
            txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
            txtEstoque.setEnabled(false);

            modoEdicao = true;
            btnCadastrar.setText("Salvar Alterações");
            estilizarBotao(btnCadastrar, new Color(255, 140, 0));
        }
    }

    private void listarProdutos() {
        modeloTabela.setRowCount(0);
        List<ProdutoDTO> lista = produtoBO.listarTodos();
        for (ProdutoDTO p : lista) {
            modeloTabela.addRow(new Object[]{
                    p.getCodigoBarras(),
                    p.getNome(),
                    p.getCategoria().getDescricao(),
                    String.format("R$ %.2f", p.getPrecoUnitario()),
                    p.getQuantidadeEstoque(),
                    p.isComDefeito() ? "Sim" : "Não"
            });
        }
    }

    private void ajustarEstoqueSelecionado() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloTabela.getValueAt(linha, 0);
        ProdutoDTO p = produtoBO.buscarPorCodigoBarras(codigo);

        if (p != null) {
            new TelaEntradaEstoque(this, p, () -> listarProdutos()).setVisible(true);
        }
    }

    private void abrirHistorico() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codigo = (String) modeloTabela.getValueAt(linha, 0);
        ProdutoDTO p = produtoBO.buscarPorCodigoBarras(codigo);

        if (p != null) {
            new TelaHistoricoProduto(this, p).setVisible(true);
        }
    }

    private void imprimirEtiqueta() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloTabela.getValueAt(linha, 0);
        String nome = (String) modeloTabela.getValueAt(linha, 1);
        String precoStr = (String) modeloTabela.getValueAt(linha, 3);

        try {
            double preco = Double.parseDouble(precoStr.replace("R$", "").replace(".", "").replace(",", ".").trim());
            String arquivoTemp = "etiqueta_temp.png";

            GeradorCodigoBarrasUtil.gerarImagemEtiqueta(codigo, nome, preco, arquivoTemp);
            ImpressaoUtil.imprimirImagem(arquivoTemp);

            JOptionPane.showMessageDialog(this, "Etiqueta enviada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro impressão: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirProduto() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloTabela.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza? O produto ficará inativo.", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (produtoBO.excluir(codigo)) {
                listarProdutos();
                JOptionPane.showMessageDialog(this, "Produto inativado!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao inativar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPreco.setText("");
        txtCodigoBarras.setText("");
        txtEstoque.setText("1");
        chkComDefeito.setSelected(false);

        txtCodigoBarras.setEditable(true);
        txtEstoque.setEnabled(true);
        modoEdicao = false;
        btnCadastrar.setText("Cadastrar");
        estilizarBotao(btnCadastrar, new Color(60, 179, 113));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaProdutos());
    }
}