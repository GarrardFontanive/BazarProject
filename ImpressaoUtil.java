package com.classes.util;

import com.classes.DTO.ItemVendaDTO;
import com.classes.DTO.VendaDTO;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

public class ImpressaoUtil implements Printable {

    private static final String NOME_IMPRESSORA = "EPSON TM-T20";
    private VendaDTO vendaParaImprimir;
    private BufferedImage logo;

    public ImpressaoUtil(VendaDTO venda) {
        this.vendaParaImprimir = venda;
        try {
            BufferedImage original = ImageIO.read(getClass().getResource("/imagens/logo4.png"));
            this.logo = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g = this.logo.createGraphics();
            g.drawImage(original, 0, 0, Color.WHITE, null);
            g.dispose();
        } catch (Exception e) {}
    }

    public static void imprimirRelatorioGerencial(String textoRelatorio) throws Exception {
        PrintService service = encontrarImpressora(NOME_IMPRESSORA);
        if (service == null) throw new Exception("Impressora não encontrada.");

        DocPrintJob job = service.createPrintJob();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(textoRelatorio.getBytes(StandardCharsets.UTF_8));
        buffer.write("\n\n\n".getBytes());
        buffer.write(new byte[]{0x1D, 0x56, 0x42, 0x00});

        Doc doc = new SimpleDoc(new ByteArrayInputStream(buffer.toByteArray()), flavor, null);
        job.print(doc, null);
        buffer.close();
    }

    public static void imprimirCupom(VendaDTO venda) throws Exception {
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService service = encontrarImpressora(NOME_IMPRESSORA);
        if (service != null) job.setPrintService(service);

        PageFormat pf = job.defaultPage();
        Paper paper = pf.getPaper();
        double width = 230;
        double height = 3000;
        paper.setSize(width, height);
        paper.setImageableArea(2, 0, width - 4, height);
        pf.setPaper(paper);

        job.setPrintable(new ImpressaoUtil(venda), pf);

        try {
            job.print();
            cortarPapelRaw(service);
        } catch (PrinterException e) {
            throw new Exception(e.getMessage());
        }
    }

    public static void imprimirImagem(String caminhoArquivo) throws Exception {
        PrintService service = encontrarImpressora(NOME_IMPRESSORA);
        if (service == null) throw new Exception("Impressora não encontrada.");

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(service);

        BufferedImage image = ImageIO.read(new File(caminhoArquivo));
        PageFormat pf = job.defaultPage();
        Paper paper = pf.getPaper();
        double width = 226;
        double height = image.getHeight() + 20;
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        pf.setPaper(paper);
        pf.setOrientation(PageFormat.PORTRAIT);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) return NO_SUCH_PAGE;
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double imageWidth = image.getWidth();
                double scale = 1.0;
                if (imageWidth > pageFormat.getImageableWidth()) {
                    scale = pageFormat.getImageableWidth() / imageWidth;
                }
                g2d.scale(scale, scale);
                g2d.drawImage(image, 0, 0, null);
                return PAGE_EXISTS;
            }
        }, pf);

        job.print();
        cortarPapelRaw(service);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        int y = 10;
        int larguraPagina = (int) pf.getImageableWidth();

        Font fontRegular = new Font("SansSerif", Font.PLAIN, 10);
        Font fontBold = new Font("SansSerif", Font.BOLD, 10);
        Font fontTitulo = new Font("SansSerif", Font.BOLD, 14);
        Font fontGrande = new Font("SansSerif", Font.BOLD, 16);
        int alturaLinha = 14;

        if (logo != null) {
            int larguraLogo = 140;
            int alturaLogo = 140;
            int xLogo = (larguraPagina - larguraLogo) / 2;
            g2d.drawImage(logo, xLogo, y, larguraLogo, alturaLogo, null);
            y += alturaLogo + 15;
        }

        y = desenharTextoCentralizado(g2d, "BAZAR CARITAS", fontTitulo, larguraPagina, y);
        y += 15;
        y = desenharTextoCentralizado(g2d, "COMPROVANTE NAO FISCAL", fontBold, larguraPagina, y);
        y += 10;

        g2d.setFont(fontRegular);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if(vendaParaImprimir.getDataHora() != null) {
            g2d.drawString("Data: " + vendaParaImprimir.getDataHora().format(dtf), 0, y);
            y += alturaLinha;
        }

        if (vendaParaImprimir.getCliente() != null) {
            String clienteNome = vendaParaImprimir.getCliente().getNome();
            if (clienteNome.length() > 22) clienteNome = clienteNome.substring(0, 22) + "...";
            g2d.drawString("Cliente: " + clienteNome, 0, y);
            y += alturaLinha + 5;
        }

        g2d.drawLine(0, y, larguraPagina, y);
        y += 15;

        g2d.setFont(fontBold);
        g2d.drawString("ITEM", 0, y);
        g2d.drawString("QTD", 130, y);
        g2d.drawString("TOTAL", 170, y);
        y += alturaLinha + 5;

        g2d.setFont(fontRegular);
        for (ItemVendaDTO item : vendaParaImprimir.getItens()) {
            String nome = item.getProduto().getNome();
            if (nome.length() > 18) nome = nome.substring(0, 18) + ".";
            g2d.drawString(nome, 0, y);
            g2d.drawString(String.valueOf(item.getQuantidade()), 135, y);
            g2d.drawString(String.format("%.2f", item.getSubtotal()), 170, y);
            y += alturaLinha;
        }

        y += 10;
        g2d.drawLine(0, y, larguraPagina, y);
        y += 20;

        double subtotal = vendaParaImprimir.getTotal() + vendaParaImprimir.getDesconto();
        y = desenharLinhaValor(g2d, fontBold, "SUBTOTAL:", subtotal, larguraPagina, y);
        y += 5;

        if (vendaParaImprimir.getDesconto() > 0) {
            y = desenharLinhaValor(g2d, fontBold, "DESCONTO (-):", vendaParaImprimir.getDesconto(), larguraPagina, y);
            y += 5;
        }

        y += 5;
        y = desenharLinhaValor(g2d, fontGrande, "TOTAL:", vendaParaImprimir.getTotal(), larguraPagina, y);

        y += 30;
        g2d.setFont(fontRegular);
        if (vendaParaImprimir.getFormaPagamento() != null) {
            y = desenharTextoCentralizado(g2d, "Forma Pgto: " + vendaParaImprimir.getFormaPagamento().getDescricao(), fontRegular, larguraPagina, y);
        }
        y += 15;
        y = desenharTextoCentralizado(g2d, "Obrigado pela colaboracao!", fontRegular, larguraPagina, y);

        return PAGE_EXISTS;
    }

    private int desenharTextoCentralizado(Graphics2D g, String texto, Font font, int larguraPagina, int y) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (larguraPagina - fm.stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
        return y + fm.getHeight();
    }

    private int desenharLinhaValor(Graphics2D g, Font font, String label, double valor, int larguraPagina, int y) {
        g.setFont(font);
        String valorStr = String.format("R$ %.2f", valor);
        g.drawString(label, 0, y);
        FontMetrics fm = g.getFontMetrics();
        int xValor = larguraPagina - fm.stringWidth(valorStr);
        g.drawString(valorStr, xValor, y);
        return y + fm.getHeight();
    }

    private static PrintService encontrarImpressora(String nome) {
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (PrintService s : services) {
            if (s.getName().toLowerCase().contains(nome.toLowerCase())) {
                return s;
            }
        }
        return null;
    }

    private static void cortarPapelRaw(PrintService service) {
        if (service == null) return;
        try {
            DocPrintJob job = service.createPrintJob();
            byte[] corte = new byte[]{0x1D, 0x56, 0x42, 0x00};
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc doc = new SimpleDoc(new ByteArrayInputStream(corte), flavor, null);
            job.print(doc, null);
        } catch (Exception e) { }
    }
}