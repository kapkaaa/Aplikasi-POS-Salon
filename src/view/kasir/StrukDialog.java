package view.kasir;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Dialog Struk Pembayaran
 */
public class StrukDialog extends JDialog {
    
    public StrukDialog(Frame parent, String noTransaksi, java.util.Date tanggal, 
                        String metodePembayaran, double totalHarga,
                       String namaKasir, ArrayList<PenjualanPanel.ItemKeranjang> items) {
        super(parent, "Struk Pembayaran", true);
        setSize(450, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        
        int yPos = 20;
        
        // Header
        JLabel lblTitle = new JLabel("SALON BLUE");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, yPos, 450, 30);
        panel.add(lblTitle);
        yPos += 35;
        
        JLabel lblSubtitle = new JLabel("Salon Unisex");
        lblSubtitle.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setBounds(0, yPos, 450, 20);
        panel.add(lblSubtitle);
        yPos += 30;
        
        // Separator
        JSeparator sep1 = new JSeparator();
        sep1.setBounds(50, yPos, 350, 1);
        panel.add(sep1);
        yPos += 15;
        
        // Info Transaksi
        addLabel(panel, "No Transaksi:", noTransaksi, yPos);
        yPos += 25;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        addLabel(panel, "Tanggal:", sdf.format(tanggal), yPos);
        yPos += 25;
        
//        addLabel(panel, "Pelanggan:", namaPelanggan != null ? namaPelanggan : "-", yPos);
//        yPos += 25;
        
        addLabel(panel, "Kasir:", namaKasir, yPos);
        yPos += 30;
        
        // Separator
        JSeparator sep2 = new JSeparator();
        sep2.setBounds(50, yPos, 350, 1);
        panel.add(sep2);
        yPos += 15;
        
        // Items
        JLabel lblItemsTitle = new JLabel("Detail Pembelian:");
        lblItemsTitle.setFont(new Font("Poppins", Font.BOLD, 12));
        lblItemsTitle.setBounds(50, yPos, 150, 20);
        panel.add(lblItemsTitle);
        yPos += 25;
        
        for (PenjualanPanel.ItemKeranjang item : items) {
            JLabel lblItem = new JLabel(item.namaItem);
            lblItem.setFont(new Font("Poppins", Font.PLAIN, 11));
            lblItem.setBounds(50, yPos, 250, 20);
            panel.add(lblItem);
            
            JLabel lblQty = new JLabel(item.quantity + " x Rp " + String.format("%,.0f", item.harga));
            lblQty.setFont(new Font("Poppins", Font.PLAIN, 11));
            lblQty.setForeground(Color.GRAY);
            lblQty.setBounds(70, yPos + 18, 200, 18);
            panel.add(lblQty);
            
            JLabel lblSubtotal = new JLabel("Rp " + String.format("%,.0f", item.subtotal));
            lblSubtotal.setFont(new Font("Poppins", Font.PLAIN, 11));
            lblSubtotal.setHorizontalAlignment(SwingConstants.RIGHT);
            lblSubtotal.setBounds(300, yPos, 100, 20);
            panel.add(lblSubtotal);
            
            yPos += 40;
        }
        
        yPos += 10;
        
        // Separator
        JSeparator sep3 = new JSeparator();
        sep3.setBounds(50, yPos, 350, 1);
        panel.add(sep3);
        yPos += 15;
        
        // Total
        JLabel lblTotalLabel = new JLabel("TOTAL:");
        lblTotalLabel.setFont(new Font("Poppins", Font.BOLD, 14));
        lblTotalLabel.setBounds(50, yPos, 100, 25);
        panel.add(lblTotalLabel);
        
        JLabel lblTotal = new JLabel("Rp " + String.format("%,.0f", totalHarga));
        lblTotal.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTotal.setForeground(new Color(52, 152, 219));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setBounds(250, yPos, 150, 25);
        panel.add(lblTotal);
        yPos += 35;
        
        // Metode Pembayaran
        addLabel(panel, "Metode Pembayaran:", metodePembayaran, yPos);
        yPos += 40;
        
        // Footer
        JLabel lblFooter = new JLabel("Terima kasih atas kunjungan Anda!");
        lblFooter.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBounds(0, yPos, 450, 20);
        panel.add(lblFooter);
        yPos += 40;
        
        // Button
        JButton btnTutup = new JButton("Tutup");
        btnTutup.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnTutup.setBackground(new Color(52, 152, 219));
        btnTutup.setForeground(new Color(52, 152, 219));
        btnTutup.setBorder(BorderFactory.createEmptyBorder());
        btnTutup.setFocusPainted(false);
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.setBounds(175, yPos, 100, 35);
        btnTutup.addActionListener(e -> dispose());
        panel.add(btnTutup);
        
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }
    
    private void addLabel(JPanel panel, String label, String value, int yPos) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblLabel.setForeground(Color.GRAY);
        lblLabel.setBounds(50, yPos, 150, 20);
        panel.add(lblLabel);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Poppins", Font.BOLD, 11));
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblValue.setBounds(200, yPos, 200, 20);
        panel.add(lblValue);
    }
}