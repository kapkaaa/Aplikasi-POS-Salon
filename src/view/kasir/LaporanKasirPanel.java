package view.kasir;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * Panel Laporan Pendapatan Kasir - Untuk Setor Tunai
 */
public class LaporanKasirPanel extends JPanel {
    
    private int idKasir;
    private String namaKasir;
    private JComboBox<String> cmbFilter;
    
    private JLabel lblTotalCash;
    private JLabel lblTotalQRIS;
    private JLabel lblTotalSemua;
    private JLabel lblJumlahTransaksi;
    
    public LaporanKasirPanel(int idKasir, String namaKasir) {
        this.idKasir = idKasir;
        this.namaKasir = namaKasir;
        initComponents();
        loadLaporan();
    }
    
    private void initComponents() {
        setLayout(null);
        setBackground(new Color(240, 242, 245));
        
        JLabel lblTitle = new JLabel("📊 Laporan Pendapatan");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBounds(30, 20, 400, 35);
        add(lblTitle);
        
        JLabel lblInfo = new JLabel("Laporan ini digunakan untuk setor tunai");
        lblInfo.setFont(new Font("Poppins", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(30, 55, 400, 20);
        add(lblInfo);
        
        // ===== PANEL FILTER =====
        JPanel panelFilter = new JPanel();
        panelFilter.setLayout(null);
        panelFilter.setBackground(Color.WHITE);
        panelFilter.setBounds(30, 90, 1120, 80);
        panelFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JLabel lblFilterBy = new JLabel("Filter Periode:");
        lblFilterBy.setFont(new Font("Poppins", Font.BOLD, 13));
        lblFilterBy.setBounds(20, 15, 120, 25);
        panelFilter.add(lblFilterBy);
        
        cmbFilter = new JComboBox<>(new String[]{"Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini"});
        cmbFilter.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilter.setBounds(20, 40, 150, 30);
        panelFilter.add(cmbFilter);
        
        JButton btnTampilkan = new JButton("📊 Tampilkan");
        btnTampilkan.setFont(new Font("Poppins", Font.BOLD, 12));
        btnTampilkan.setBackground(new Color(52, 152, 219));
        btnTampilkan.setForeground(new Color(52, 152, 219));
        btnTampilkan.setBorder(BorderFactory.createEmptyBorder());
        btnTampilkan.setFocusPainted(false);
        btnTampilkan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTampilkan.setBounds(190, 40, 120, 30);
        btnTampilkan.addActionListener(e -> loadLaporan());
        panelFilter.add(btnTampilkan);
        
        add(panelFilter);
        
        // ===== PANEL STATISTIK =====
        JPanel panelStats = new JPanel();
        panelStats.setLayout(null);
        panelStats.setBackground(Color.WHITE);
        panelStats.setBounds(30, 185, 1120, 380);
        panelStats.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JLabel lblStatsTitle = new JLabel("Ringkasan Pendapatan");
        lblStatsTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblStatsTitle.setBounds(20, 15, 200, 25);
        panelStats.add(lblStatsTitle);
        
        // Card 1: Total Cash
        lblTotalCash = createStatCard(panelStats, "💵 Total Cash", "Rp 0", new Color(46, 204, 113), 20, 60, 520);

        
        // Card 2: Total QRIS
        lblTotalQRIS = createStatCard(panelStats, "📱 Total QRIS", "Rp 0", new Color(52, 152, 219), 560, 60, 520);

        
        // Card 3: Total Semua
        lblTotalSemua = createStatCard(panelStats, "💰 Total Pendapatan", "Rp 0", new Color(155, 89, 182), 20, 170, 520);

        
        // Card 4: Jumlah Transaksi
        lblJumlahTransaksi = createStatCard(panelStats, "📊 Jumlah Transaksi", "0 Transaksi", new Color(241, 196, 15), 560, 170, 520);

        
        // Info Setoran
        JLabel lblInfoSetor = new JLabel("💡 Yang harus disetor (Cash):");
        lblInfoSetor.setFont(new Font("Poppins", Font.BOLD, 14));
        lblInfoSetor.setBounds(20, 290, 250, 25);
        panelStats.add(lblInfoSetor);
        
        JLabel lblJumlahSetor = new JLabel("Rp 0");
        lblJumlahSetor.setFont(new Font("Poppins", Font.BOLD, 28));
        lblJumlahSetor.setForeground(new Color(46, 204, 113));
        lblJumlahSetor.setBounds(20, 320, 500, 35);
        panelStats.add(lblJumlahSetor);
        
        add(panelStats);
    }
    
    private JLabel createStatCard(JPanel parent, String title, String value, Color color, int x, int y, int width) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(color);
        card.setBounds(x, y, width, 90);
        card.setBorder(BorderFactory.createLineBorder(color.darker(), 1));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(20, 15, 300, 25);
        card.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Poppins", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBounds(20, 45, 480, 35);
        card.add(lblValue);

        parent.add(card);
        return lblValue; // ✅ kembalikan label agar bisa disimpan
    }

    
    private void loadLaporan() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sqlWhere = getDateFilter();
            
            String sqlStats = "SELECT " +
                "COALESCE(SUM(CASE WHEN metode_pembayaran = 'Cash' THEN total_harga ELSE 0 END), 0) as total_cash, " +
                "COALESCE(SUM(CASE WHEN metode_pembayaran = 'QRIS' THEN total_harga ELSE 0 END), 0) as total_qris, " +
                "COALESCE(SUM(total_harga), 0) as total_semua, " +
                "COUNT(*) as jumlah_transaksi " +
                "FROM tb_transaksi WHERE id_kasir = ? AND " + sqlWhere;
            
            PreparedStatement ps = conn.prepareStatement(sqlStats);
            ps.setInt(1, idKasir);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double totalCash = rs.getDouble("total_cash");
                double totalQRIS = rs.getDouble("total_qris");
                double totalSemua = rs.getDouble("total_semua");
                int jumlahTransaksi = rs.getInt("jumlah_transaksi");
                
                lblTotalCash.setText("Rp " + String.format("%,.0f", totalCash));
                lblTotalQRIS.setText("Rp " + String.format("%,.0f", totalQRIS));
                lblTotalSemua.setText("Rp " + String.format("%,.0f", totalSemua));
                lblJumlahTransaksi.setText(jumlahTransaksi + " Transaksi");
                
                // Update jumlah setor
                Component[] components = ((JPanel) getComponent(3)).getComponents();
                for (Component comp : components) {
                    if (comp instanceof JLabel && ((JLabel) comp).getFont().getSize() == 28) {
                        ((JLabel) comp).setText("Rp " + String.format("%,.0f", totalCash));
                        break;
                    }
                }
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getDateFilter() {
        String filter = (String) cmbFilter.getSelectedItem();
        
        switch (filter) {
            case "Hari Ini":
                return "DATE(tgl_transaksi) = CURDATE()";
                
            case "Kemarin":
                return "DATE(tgl_transaksi) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
                
            case "Minggu Ini":
                return "YEARWEEK(tgl_transaksi, 1) = YEARWEEK(CURDATE(), 1)";
                
            case "Bulan Ini":
                return "MONTH(tgl_transaksi) = MONTH(CURDATE()) AND YEAR(tgl_transaksi) = YEAR(CURDATE())";
                
            default:
                return "DATE(tgl_transaksi) = CURDATE()";
        }
    }
}