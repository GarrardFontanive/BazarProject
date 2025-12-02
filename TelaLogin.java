package com.telas;

import com.classes.BO.UsuarioBO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TelaLogin extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar, btnSair;
    private UsuarioBO usuarioBO = new UsuarioBO();

    private static final Color COR_MARCA = new Color(196, 5, 5);
    private static final Color COR_BOTAO = new Color(130, 0, 0);

    public TelaLogin() {
        com.classes.util.UiUtil.configurarIconeJanela(this);
        setTitle("Login - Bazar Cáritas");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel pnlTopo = new JPanel(new BorderLayout());
        pnlTopo.setBackground(COR_MARCA);
        pnlTopo.setPreferredSize(new Dimension(400, 150));
        pnlTopo.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/imagens/logo3.png"));
            Image img = originalIcon.getImage();
            Image newImg = img.getScaledInstance(130, 130, java.awt.Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(newImg));
        } catch (Exception e) {
            lblLogo.setText("BAZAR CÁRITAS");
            lblLogo.setForeground(Color.WHITE);
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        }

        pnlTopo.add(lblLogo, BorderLayout.CENTER);
        add(pnlTopo, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel(new GridBagLayout());
        pnlCentro.setBackground(COR_MARCA);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuário:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(Color.WHITE);
        pnlCentro.add(lblUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtLogin = new JTextField(15);
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLogin.setPreferredSize(new Dimension(200, 35));
        pnlCentro.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPass = new JLabel("Senha:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(Color.WHITE);
        pnlCentro.add(lblPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        txtSenha = new JPasswordField(15);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setPreferredSize(new Dimension(200, 35));

        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) realizarLogin();
            }
        });
        pnlCentro.add(txtSenha, gbc);

        add(pnlCentro, BorderLayout.CENTER);

        JPanel pnlSul = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlSul.setBackground(COR_MARCA);

        btnSair = new JButton("SAIR");
        btnSair.setBackground(COR_BOTAO);
        btnSair.setForeground(Color.WHITE);
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSair.setPreferredSize(new Dimension(100, 40));
        btnSair.setOpaque(true);
        btnSair.setBorderPainted(false);
        btnSair.addActionListener(e -> System.exit(0));

        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setBackground(COR_BOTAO);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setPreferredSize(new Dimension(120, 40));
        btnEntrar.setOpaque(true);
        btnEntrar.setBorderPainted(false);
        btnEntrar.addActionListener(e -> realizarLogin());

        pnlSul.add(btnSair);
        pnlSul.add(btnEntrar);

        add(pnlSul, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void realizarLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        if (usuarioBO.logar(login, senha)) {
            new TelaPrincipal();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos!\nVerifique se o usuário 'admin' existe no banco.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaLogin::new);
    }
}