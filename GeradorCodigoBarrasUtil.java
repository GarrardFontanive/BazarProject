package com.classes.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class GeradorCodigoBarrasUtil {

    public static String gerarNovoEan13() {
        Random rand = new Random();
        StringBuilder codigo = new StringBuilder("789");
        for (int i = 0; i < 9; i++) {
            codigo.append(rand.nextInt(10));
        }
        int digito = calcularDigitoVerificador(codigo.toString());
        return codigo.append(digito).toString();
    }

    private static int calcularDigitoVerificador(String base) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            int digito = Character.getNumericValue(base.charAt(i));
            soma += (i % 2 == 0) ? digito : digito * 3;
        }
        int resto = soma % 10;
        return (resto == 0) ? 0 : 10 - resto;
    }

    public static void gerarImagemEtiqueta(String codigo, String nomeProduto, double preco, String caminhoArquivo) throws Exception {

        int larguraEtiqueta = 250;
        int alturaEtiqueta = 130;
        int larguraCodigo = 200;
        int alturaCodigo = 50;

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                codigo,
                BarcodeFormat.CODE_128,
                larguraCodigo,
                alturaCodigo
        );

        BufferedImage imagemCodigo = MatrixToImageWriter.toBufferedImage(bitMatrix);

        BufferedImage etiqueta = new BufferedImage(larguraEtiqueta, alturaEtiqueta, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = etiqueta.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, larguraEtiqueta, alturaEtiqueta);
        g.setColor(Color.BLACK);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fmNome = g.getFontMetrics();
        int xNome = (larguraEtiqueta - fmNome.stringWidth(nomeProduto)) / 2;
        g.drawString(nomeProduto, xNome, 25);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String textoPreco = "R$ " + String.format("%.2f", preco);
        FontMetrics fmPreco = g.getFontMetrics();
        int xPreco = (larguraEtiqueta - fmPreco.stringWidth(textoPreco)) / 2;
        g.drawString(textoPreco, xPreco, 50);

        int xCodigo = (larguraEtiqueta - larguraCodigo) / 2;
        g.drawImage(imagemCodigo, xCodigo, 60, null);

        g.dispose();

        ImageIO.write(etiqueta, "png", new File(caminhoArquivo));
    }
}