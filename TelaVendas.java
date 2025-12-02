package com.telas;

import com.classes.BO.ClienteBO;
import com.classes.BO.ProdutoBO;
import com.classes.BO.VendaBO;
import com.classes.DTO.ClienteDTO;
import com.classes.DTO.ItemVendaDTO;
import com.classes.DTO.ProdutoDTO;
import com.classes.DTO.VendaDTO;
import com.classes.enums.FormaPagamento;
import com.classes.util.ImpressaoUtil;
import com.classes.util.ValidadorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.border.TitledBorder;

public class TelaVendas extends JFrame {

    private JTextField txtCodigoBarras;
    private JLabel lblTotal;
    private JButton btnFinalizar, btnAplicarDesconto;
    private JTable tabela;
    private DefaultTableModel modelo;
    private double totalVenda = 0;

    private final ProdutoBO produtoBO = new ProdutoBO();
    private final ClienteBO clienteBO = new ClienteBO();
    private final VendaBO vendaBO = new VendaBO();

    private final List<ItemVendaDTO> itensVenda = new ArrayList<>();

    private static final Color COR_MARCA = new Color(196, 5, 5);

    public TelaVendas() {
        setTitle("PDV - Registrar Venda");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelNorte.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_MARCA, 2), "Leitor de Código de Barras",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), COR_MARCA));
        painelNorte.setBackground(Color.WHITE);

        painelNorte.add(new JLabel("Código de Barras:"));
        txtCodigoBarras = new JTextField(30);
        txtCodigoBarras.setFont(new Font("Monospaced", Font.BOLD, 18));
        painelNorte.add(txtCodigoBarras);

        txtCodigoBarras.addActionListener(e -> adicionarProdutoAoCarrinho());

        add(painelNorte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Código", "Produto", "Qtd", "Preço Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        painelSul.setBackground(new Color(250, 250, 250));

        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotal.setForeground(COR_MARCA);

        btnAplicarDesconto = new JButton("Aplicar Desconto no Item");
        btnAplicarDesconto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAplicarDesconto.setBackground(new Color(255, 140, 0));
            btnAplicarDesconto.setForeground(Color.BLACK);
        btnAplicarDesconto.setPreferredSize(new Dimension(220, 40));
        btnAplicarDesconto.addActionListener(e -> aplicarDescontoItem());

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelAcoes.setOpaque(false);
        painelAcoes.add(btnAplicarDesconto);

        btnFinalizar = new JButton("Finalizar Venda");
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnFinalizar.setBackground(new Color(60, 179, 113));
        btnFinalizar.setForeground(Color.BLACK);
        btnFinalizar.setPreferredSize(new Dimension(220, 40));
        btnFinalizar.addActionListener(e -> finalizarVenda());
        painelAcoes.add(btnFinalizar);

        painelSul.add(lblTotal, BorderLayout.WEST);
        painelSul.add(painelAcoes, BorderLayout.EAST);

        add(painelSul, BorderLayout.SOUTH);

        setVisible(true);
        txtCodigoBarras.requestFocusInWindow();
    }

    private JFormattedTextField criarCampoFormatado(String mask) throws Exception {
        MaskFormatter formatter = new MaskFormatter(mask);
        formatter.setPlaceholderCharacter('_');
        return new JFormattedTextField(formatter);
    }

    private void adicionarProdutoAoCarrinho() {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) return;

        ProdutoDTO produtoBanco = produtoBO.buscarPorCodigoBarras(codigo);

        if (produtoBanco == null) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            txtCodigoBarras.setText("");
            return;
        }

        ItemVendaDTO itemCarrinho = null;
        int qtdNoCarrinho = 0;

        for (ItemVendaDTO item : itensVenda) {
            if (item.getProduto().getCodigoBarras().equals(codigo)) {
                itemCarrinho = item;
                qtdNoCarrinho = item.getQuantidade();
                break;
            }
        }

        int qtdDesejada = qtdNoCarrinho + 1;
        int estoqueDisponivel = produtoBanco.getQuantidadeEstoque();

        if (qtdDesejada > estoqueDisponivel) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "ESTOQUE INSUFICIENTE!\nProduto: " + produtoBanco.getNome() + "\nEstoque: " + estoqueDisponivel,
                    "Bloqueio", JOptionPane.WARNING_MESSAGE);
            txtCodigoBarras.setText("");
            return;
        }

        if (itemCarrinho != null) {
            itemCarrinho.setQuantidade(qtdDesejada);
            double subtotal = itemCarrinho.getProduto().getPrecoUnitario() * itemCarrinho.getQuantidade();
            itemCarrinho.setSubtotal(subtotal);

            for(int i=0; i<modelo.getRowCount(); i++) {
                if(modelo.getValueAt(i, 0).equals(codigo)) {
                    modelo.setValueAt(itemCarrinho.getQuantidade(), i, 2);
                    modelo.setValueAt(String.format("R$ %.2f", subtotal), i, 4);
                    break;
                }
            }
        } else {
            ItemVendaDTO itemNovo = new ItemVendaDTO();
            itemNovo.setProduto(produtoBanco);
            itemNovo.setQuantidade(1);
            itemNovo.setSubtotal(produtoBanco.getPrecoUnitario());

            itensVenda.add(itemNovo);

            modelo.addRow(new Object[]{
                    produtoBanco.getCodigoBarras(),
                    produtoBanco.getNome(),
                    1,
                    String.format("R$ %.2f", produtoBanco.getPrecoUnitario()),
                    String.format("R$ %.2f", itemNovo.getSubtotal())
            });
        }

        txtCodigoBarras.setText("");
        atualizarTotal();
    }

    private void atualizarTotal() {
        totalVenda = 0.0;
        for (ItemVendaDTO item : itensVenda) {
            totalVenda += item.getSubtotal();
        }
        lblTotal.setText(String.format("Total: R$ %.2f", totalVenda));
    }

    private void aplicarDescontoItem() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ItemVendaDTO item = itensVenda.get(linha);

            String valorStr = JOptionPane.showInputDialog(this, "Desconto a aplicar (R$):", "Desconto", JOptionPane.PLAIN_MESSAGE);
            if (valorStr == null) return;
            double valorDesconto = Double.parseDouble(valorStr.replace(",", "."));

            double precoBruto = item.getProduto().getPrecoUnitario() * item.getQuantidade();
            if (valorDesconto >= precoBruto) {
                JOptionPane.showMessageDialog(this, "Desconto inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double novoSubtotal = precoBruto - valorDesconto;
            item.setSubtotal(novoSubtotal);
            modelo.setValueAt(String.format("R$ %.2f", novoSubtotal), linha, 4);
            atualizarTotal();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarVenda() {
        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Carrinho Vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFormattedTextField txtCpf = criarCampoFormatado("###.###.###-##");
            JPanel cpfPanel = new JPanel(new GridLayout(2, 1));
            cpfPanel.add(new JLabel("CPF do Cliente:"));
            cpfPanel.add(txtCpf);

            int result = JOptionPane.showConfirmDialog(this, cpfPanel, "Finalizar Venda", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String cpfLimpo = txtCpf.getText().replaceAll("[^0-9]", "");
            if (!ValidadorUtil.isValidCpf(cpfLimpo)) {
                JOptionPane.showMessageDialog(this, "CPF inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClienteDTO cliente = clienteBO.buscarPorCpf(cpfLimpo);
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JComboBox<FormaPagamento> cmbPagamento = new JComboBox<>(FormaPagamento.values());
            JOptionPane.showMessageDialog(this, cmbPagamento, "Forma de Pagamento", JOptionPane.QUESTION_MESSAGE);
            FormaPagamento formaPag = (FormaPagamento) cmbPagamento.getSelectedItem();

            double descontoTotal = 0;
            int opDesconto = JOptionPane.showConfirmDialog(this, "Aplicar desconto extra no TOTAL?", "Desconto", JOptionPane.YES_NO_OPTION);
            if (opDesconto == JOptionPane.YES_OPTION) {
                String descStr = JOptionPane.showInputDialog("Valor do desconto (R$):");
                if (descStr != null) descontoTotal = Double.parseDouble(descStr.replace(",", "."));
            }

            VendaDTO venda = new VendaDTO();
            venda.setCliente(cliente);
            venda.setFormaPagamento(formaPag);
            venda.setDesconto(descontoTotal);
            venda.setTotal(totalVenda - descontoTotal);
            venda.setItens(itensVenda);
            venda.setAdminNome("Admin");

            if (vendaBO.processarVenda(venda)) {
                JOptionPane.showMessageDialog(this, "Venda Sucesso!");
                try {
                    ImpressaoUtil.imprimirCupom(venda);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro impressão: " + ex.getMessage());
                }
                limparTelaParaNovaVenda();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao registrar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparTelaParaNovaVenda() {
        itensVenda.clear();
        modelo.setRowCount(0);
        totalVenda = 0;
        atualizarTotal();
        txtCodigoBarras.setText("");
        txtCodigoBarras.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaVendas::new);
    }
}