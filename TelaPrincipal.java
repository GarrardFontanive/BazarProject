package com.telas;

import com.classes.BO.ProdutoBO;
import com.classes.BO.VendaBO;
import com.classes.DTO.ProdutoDTO;
import com.classes.DTO.UsuarioDTO;
import com.classes.util.SessaoUsuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private static final Color COR_MARCA = new Color(161, 3, 3);
    private static final Color COR_FUNDO = new Color(250, 250, 250);
    private static final Color COR_OK = new Color(46, 204, 113);
    private static final Color COR_ESGOTADO = Color.RED;
    private static final Color COR_BAIXO = new Color(255, 140, 0);

    private JLabel lblVendasHoje;
    private JPanel cardEstoque;
    private JLabel lblTituloEstoque;
    private JLabel lblDetalheEstoque;
    private Timer timerEstoque;
    private List<ProdutoDTO> listaAlertas;
    private int indiceAlerta = 0;

    private VendaBO vendaBO = new VendaBO();
    private ProdutoBO produtoBO = new ProdutoBO();

    public TelaPrincipal() {
        setTitle("Sistema Bazar Cáritas");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO);

        JPanel containerNorte = new JPanel();
        containerNorte.setLayout(new BoxLayout(containerNorte, BoxLayout.Y_AXIS));
        containerNorte.add(criarTopo());
        containerNorte.add(criarPainelDashboard());
        add(containerNorte, BorderLayout.NORTH);

        add(criarPainelMenuInteligente(), BorderLayout.CENTER);

        add(criarRodape(), BorderLayout.SOUTH);

        iniciarCarrosselEstoque();

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                atualizarDados();
            }
        });

        setVisible(true);
        atualizarDados();
    }

    private JPanel criarPainelMenuInteligente() {
        JPanel painelMenu = new JPanel(new GridBagLayout());
        painelMenu.setBackground(COR_FUNDO);
        painelMenu.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        UsuarioDTO usuario = SessaoUsuario.getInstance().getUsuarioLogado();
        boolean isAdmin = (usuario != null && usuario.isAdmin());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JButton btnVendas = criarBotaoMenu("Vendas", "Registrar venda", "/imagens/icon_venda.png");
        btnVendas.addActionListener(e -> new TelaVendas());

        JButton btnClientes = criarBotaoMenu("Clientes", "Gerenciar cadastros", "/imagens/icon_cliente.png");
        btnClientes.addActionListener(e -> new TelaClientes());

        if (!isAdmin) {
            gbc.gridx = 0; gbc.gridy = 0;
            painelMenu.add(btnVendas, gbc);

            gbc.gridx = 1; gbc.gridy = 0;
            painelMenu.add(btnClientes, gbc);

        } else {
            JButton btnRelatorios = criarBotaoMenu("Relatórios", "Histórico", "/imagens/icon_relatorio.png");
            btnRelatorios.addActionListener(e -> new TelaRelatorios());

            JButton btnProdutos = criarBotaoMenu("Produtos", "Estoque", "/imagens/icon_produto.png");
            btnProdutos.addActionListener(e -> new TelaProdutos());

            JButton btnDoadores = criarBotaoMenu("Doadores", "Gerenciar origens", "/imagens/icon_doador.png");
            btnDoadores.addActionListener(e -> new TelaDoadores());

            gbc.gridx = 0; gbc.gridy = 0;
            painelMenu.add(btnVendas, gbc);

            gbc.gridx = 1; gbc.gridy = 0;
            painelMenu.add(btnRelatorios, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            painelMenu.add(btnClientes, gbc);

            gbc.gridx = 1; gbc.gridy = 1;
            painelMenu.add(btnProdutos, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            painelMenu.add(btnDoadores, gbc);
        }

        return painelMenu;
    }


    private JPanel criarTopo() {
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(COR_MARCA);
        painelTopo.setPreferredSize(new Dimension(1024, 110));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Bazar Cáritas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/imagens/logo3.png"));
            Image img = originalIcon.getImage();
            Image newImg = img.getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH);
            lblTitulo.setIcon(new ImageIcon(newImg));
            lblTitulo.setIconTextGap(20);
        } catch (Exception e) { }

        painelTopo.add(lblTitulo, BorderLayout.WEST);

        JPanel painelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelUsuario.setOpaque(false);

        String nomeUsuario = "Usuário";
        if (SessaoUsuario.getInstance().getUsuarioLogado() != null) {
            nomeUsuario = SessaoUsuario.getInstance().getUsuarioLogado().getNomeCompleto();
        }

        JLabel lblUsuario = new JLabel("Olá, " + nomeUsuario + "  ");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUsuario.setForeground(Color.WHITE);

        JButton btnSair = new JButton("Sair");
        styleButtonDanger(btnSair);
        btnSair.addActionListener(e -> {
            if (timerEstoque != null) timerEstoque.stop();
            SessaoUsuario.getInstance().logout();
            new TelaLogin();
            dispose();
        });

        painelUsuario.add(lblUsuario);
        painelUsuario.add(btnSair);
        painelTopo.add(painelUsuario, BorderLayout.EAST);

        return painelTopo;
    }

    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 20, 0));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(new EmptyBorder(20, 50, 0, 50));
        painel.setPreferredSize(new Dimension(1024, 110));

        JPanel cardVendas = criarCardBase(COR_OK);
        JLabel lblTituloVendas = new JLabel("Vendas Hoje");
        lblTituloVendas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTituloVendas.setForeground(Color.WHITE);
        lblVendasHoje = new JLabel("R$ 0,00");
        lblVendasHoje.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblVendasHoje.setForeground(Color.WHITE);
        cardVendas.add(lblTituloVendas, BorderLayout.NORTH);
        cardVendas.add(lblVendasHoje, BorderLayout.CENTER);

        cardEstoque = criarCardBase(COR_OK);
        lblTituloEstoque = new JLabel("Status do Estoque");
        lblTituloEstoque.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloEstoque.setForeground(Color.WHITE);
        lblDetalheEstoque = new JLabel("Verificando...");
        lblDetalheEstoque.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDetalheEstoque.setForeground(Color.WHITE);
        cardEstoque.add(lblTituloEstoque, BorderLayout.NORTH);
        cardEstoque.add(lblDetalheEstoque, BorderLayout.CENTER);

        painel.add(cardVendas);
        painel.add(cardEstoque);

        return painel;
    }

    private JPanel criarCardBase(Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(new EmptyBorder(10, 20, 10, 20));
        return card;
    }

    private void iniciarCarrosselEstoque() {
        timerEstoque = new Timer(3000, e -> rodarAnimacaoEstoque());
        timerEstoque.start();
    }

    private void atualizarDados() {
        double totalHoje = vendaBO.buscarTotalVendasHoje();
        lblVendasHoje.setText(String.format("R$ %.2f", totalHoje));
        listaAlertas = produtoBO.buscarAlertasEstoque();
        if (listaAlertas.isEmpty()) {
            cardEstoque.setBackground(COR_OK);
            lblTituloEstoque.setText("Situação do Estoque");
            lblDetalheEstoque.setText("Tudo Normal ✅");
        }
    }

    private void rodarAnimacaoEstoque() {
        if (listaAlertas == null || listaAlertas.isEmpty()) {
            cardEstoque.setBackground(COR_OK);
            lblTituloEstoque.setText("Situação do Estoque");
            lblDetalheEstoque.setText("Tudo Normal ✅");
            return;
        }
        indiceAlerta++;
        if (indiceAlerta >= listaAlertas.size()) indiceAlerta = 0;
        ProdutoDTO p = listaAlertas.get(indiceAlerta);

        if (p.getQuantidadeEstoque() <= 0) {
            cardEstoque.setBackground(COR_ESGOTADO);
            lblTituloEstoque.setText("🚨 PRODUTO ESGOTADO!");
            lblDetalheEstoque.setText(p.getCategoria().getDescricao() + " - " + p.getNome());
        } else {
            cardEstoque.setBackground(COR_BAIXO);
            lblTituloEstoque.setText("⚠️ Estoque Baixo (" + p.getQuantidadeEstoque() + " unid.)");
            lblDetalheEstoque.setText(p.getCategoria().getDescricao() + " - " + p.getNome());
        }
    }

    private JButton criarBotaoMenu(String titulo, String subtitulo, String caminhoIcone) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(COR_MARCA);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));

        JLabel lblSubtitulo = new JLabel(subtitulo, SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(240, 240, 240));
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(caminhoIcone));
        } catch (Exception e) { }

        btn.add(lblTitulo, BorderLayout.CENTER);
        btn.add(lblSubtitulo, BorderLayout.SOUTH);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COR_MARCA.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COR_MARCA);
            }
        });

        return btn;
    }

    private void styleButtonDanger(JButton btn) {
        btn.setBackground(new Color(105, 105, 105));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private JPanel criarRodape() {
        JPanel pnl = new JPanel();
        pnl.setBackground(COR_FUNDO);
        JLabel lblRodape = new JLabel("Sistema Bazar Cáritas - Versão 1.0", SwingConstants.CENTER);
        lblRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        pnl.add(lblRodape);
        return pnl;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }
}