package com.telas;

import com.classes.BO.DoadorBO;
import com.classes.BO.EstoqueBO;
import com.classes.DTO.DoadorDTO;
import com.classes.DTO.ProdutoDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class TelaEntradaEstoque extends JDialog {

    private JComboBox<DoadorDTO> cmbDoador;
    private JTextField txtQuantidade;
    private JRadioButton rdoDoacao, rdoAjuste;
    private JButton btnConfirmar, btnCancelar;
    private JLabel lblDoador, lblQtd;

    private ProdutoDTO produtoSelecionado;
    private EstoqueBO estoqueBO = new EstoqueBO();
    private DoadorBO doadorBO = new DoadorBO();
    private Runnable callbackAtualizacao;

    private static final Color COR_MARCA = new Color(161, 3, 3);
    private static final Color COR_FUNDO = new Color(250, 250, 250);

    public TelaEntradaEstoque(Frame parent, ProdutoDTO produto, Runnable onSuccess) {
        super(parent, "Gerenciar Estoque", true);
        this.produtoSelecionado = produto;
        this.callbackAtualizacao = onSuccess;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO);

        JPanel pnlTopo = new JPanel(new GridLayout(2, 1));
        pnlTopo.setBackground(COR_MARCA);
        pnlTopo.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblTitulo = new JLabel("Movimentação de Estoque");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblProd = new JLabel(produto.getNome() + " (Atual: " + produto.getQuantidadeEstoque() + ")");
        lblProd.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblProd.setForeground(Color.WHITE);
        lblProd.setHorizontalAlignment(SwingConstants.CENTER);

        pnlTopo.add(lblTitulo);
        pnlTopo.add(lblProd);
        add(pnlTopo, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setBackground(COR_FUNDO);
        pnlCentro.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel pnlTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTipo.setBackground(COR_FUNDO);
        pnlTipo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Tipo de Operação"));

        ButtonGroup grupo = new ButtonGroup();
        rdoDoacao = new JRadioButton("Entrada de Doação (Soma)");
        rdoAjuste = new JRadioButton("Ajuste Manual (Define Total)");
        rdoDoacao.setBackground(COR_FUNDO);
        rdoAjuste.setBackground(COR_FUNDO);
        rdoDoacao.setSelected(true);

        rdoDoacao.addActionListener(e -> atualizarCampos());
        rdoAjuste.addActionListener(e -> atualizarCampos());

        grupo.add(rdoDoacao);
        grupo.add(rdoAjuste);
        pnlTipo.add(rdoDoacao);
        pnlTipo.add(rdoAjuste);

        pnlCentro.add(pnlTipo);
        pnlCentro.add(Box.createVerticalStrut(20));

        lblDoador = new JLabel("Selecione o Doador:");
        lblDoador.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCentro.add(lblDoador);

        cmbDoador = new JComboBox<>();
        cmbDoador.setMaximumSize(new Dimension(400, 35));
        carregarDoadores();
        pnlCentro.add(cmbDoador);

        pnlCentro.add(Box.createVerticalStrut(15));

        lblQtd = new JLabel("Quantidade a Adicionar (+):");
        lblQtd.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCentro.add(lblQtd);

        txtQuantidade = new JTextField();
        txtQuantidade.setMaximumSize(new Dimension(400, 35));
        pnlCentro.add(txtQuantidade);

        add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlSul = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlSul.setBackground(COR_FUNDO);

        btnCancelar = new JButton("Cancelar");
        estilizarBotao(btnCancelar, new Color(105, 105, 105));
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar = new JButton("Confirmar");
        estilizarBotao(btnConfirmar, COR_MARCA);
        btnConfirmar.addActionListener(e -> confirmarOperacao());

        pnlSul.add(btnCancelar);
        pnlSul.add(btnConfirmar);
        add(pnlSul, BorderLayout.SOUTH);
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
    }

    private void atualizarCampos() {
        if (rdoDoacao.isSelected()) {
            lblDoador.setVisible(true);
            cmbDoador.setVisible(true);
            lblQtd.setText("Quantidade a Adicionar (+):");
        } else {
            lblDoador.setVisible(false);
            cmbDoador.setVisible(false);
            lblQtd.setText("Novo Estoque Total (=):");
        }
    }

    private void carregarDoadores() {
        List<DoadorDTO> lista = doadorBO.listarTodos();
        cmbDoador.removeAllItems();
        for (DoadorDTO d : lista) {
            cmbDoador.addItem(d);
        }
    }

    private void confirmarOperacao() {
        try {
            if (txtQuantidade.getText().isEmpty()) return;
            int valorDigitado = Integer.parseInt(txtQuantidade.getText());

            if (rdoDoacao.isSelected()) {
                DoadorDTO doador = (DoadorDTO) cmbDoador.getSelectedItem();
                if (doador == null) {
                    JOptionPane.showMessageDialog(this, "Selecione um doador.");
                    return;
                }
                if (valorDigitado <= 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade deve ser positiva.");
                    return;
                }

                if (estoqueBO.darEntrada(produtoSelecionado, doador, valorDigitado)) {
                    JOptionPane.showMessageDialog(this, "Doação registrada!");
                    fecharComSucesso();
                }

            } else {
                int estoqueAtual = produtoSelecionado.getQuantidadeEstoque();
                int diferenca = valorDigitado - estoqueAtual;

                if (diferenca == 0) {
                    dispose();
                    return;
                }

                if (estoqueBO.ajustarSaldo(produtoSelecionado, valorDigitado, diferenca)) {
                    JOptionPane.showMessageDialog(this, "Estoque ajustado para " + valorDigitado);
                    fecharComSucesso();
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um número válido.");
        }
    }

    private void fecharComSucesso() {
        if (callbackAtualizacao != null) callbackAtualizacao.run();
        dispose();
    }
}