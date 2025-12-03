package view.admin;

import config.Koneksi;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Panel History Penjualan - Riwayat semua transaksi
 */
public class HistoryPanel extends JPanel {
    
    private JTable tableHistory;
    private DefaultTableModel modelTable;
    private JTextField txtCari;
//    private JDateChooser dateFrom;
//    private JDateChooser dateTo;
    private JComboBox<String> cmbFilterJenis;
    private JComboBox<String> cmbFilterMetode;
    private JComboBox<String> cmbFilterPeriode;
    
    public HistoryPanel() {
        initComponents();
        loadHistory();
    }
    
    private void initComponents() {
        setLayout(null);
        setBackground(new Color(240, 242, 245));
        
        // Title
        JLabel lblTitle = new JLabel("📊 History Penjualan");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBounds(30, 20, 400, 35);
        add(lblTitle);
        
        // ===== PANEL FILTER =====
        JPanel panelFilter = new JPanel();
        panelFilter.setLayout(null);
        panelFilter.setBackground(Color.WHITE);
        panelFilter.setBounds(40, 70, 1040, 120);
        panelFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Baris 1: Search & Periode
        JLabel lblCari = new JLabel("🔍 Cari:");
        lblCari.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblCari.setBounds(20, 15, 60, 25);
        panelFilter.add(lblCari);
        
        txtCari = new JTextField();
        txtCari.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtCari.setBounds(75, 15, 220, 30);
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFilter.add(txtCari);
        
        JLabel lblPeriode = new JLabel("Periode:");
        lblPeriode.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblPeriode.setBounds(315, 15, 70, 25);
        panelFilter.add(lblPeriode);
        
        cmbFilterPeriode = new JComboBox<>(new String[]{"Semua", "Hari Ini", "Minggu Ini", "Bulan Ini", "Custom"});
        cmbFilterPeriode.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterPeriode.setBounds(385, 15, 130, 30);
        cmbFilterPeriode.addActionListener(e -> handlePeriodeChange());
        panelFilter.add(cmbFilterPeriode);
        
        JLabel lblJenis = new JLabel("Jenis:");
        lblJenis.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblJenis.setBounds(535, 15, 60, 25);
        panelFilter.add(lblJenis);
        
        cmbFilterJenis = new JComboBox<>(new String[]{"Semua", "Barang", "Layanan", "Keduanya"});
        cmbFilterJenis.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterJenis.setBounds(590, 15, 130, 30);
        panelFilter.add(cmbFilterJenis);
        
        JLabel lblMetode = new JLabel("Metode:");
        lblMetode.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblMetode.setBounds(740, 15, 70, 25);
        panelFilter.add(lblMetode);
        
        cmbFilterMetode = new JComboBox<>(new String[]{"Semua", "Cash", "QRIS"});
        cmbFilterMetode.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterMetode.setBounds(805, 15, 110, 30);
        panelFilter.add(cmbFilterMetode);
        
//        // Baris 2: Date Range
//        JLabel lblDari = new JLabel("Dari Tanggal:");
//        lblDari.setFont(new Font("Poppins", Font.PLAIN, 12));
//        lblDari.setBounds(20, 60, 100, 25);
//        panelFilter.add(lblDari);
        
//        dateFrom = new JDateChooser();
//        dateFrom.setDateFormatString("dd-MM-yyyy");
//        dateFrom.setFont(new Font("Poppins", Font.PLAIN, 12));
//        dateFrom.setBounds(120, 60, 150, 30);
//        dateFrom.setEnabled(true);
//        panelFilter.add(dateFrom);
        
//        JLabel lblSampai = new JLabel("Sampai:");
//        lblSampai.setFont(new Font("Poppins", Font.PLAIN, 12));
//        lblSampai.setBounds(290, 60, 70, 25);
//        panelFilter.add(lblSampai);
        
//        dateTo = new JDateChooser();
//        dateTo.setDateFormatString("dd-MM-yyyy");
//        dateTo.setFont(new Font("Poppins", Font.PLAIN, 12));
//        dateTo.setBounds(350, 60, 150, 30);
//        dateTo.setEnabled(true);
//        panelFilter.add(dateTo);
//        
//        dateFrom.getDateEditor().addPropertyChangeListener("date", evt -> {
//            if (dateTo.getDate() != null && dateFrom.getDate() != null) {
//                if (dateFrom.getDate().after(dateTo.getDate())) {
//                    JOptionPane.showMessageDialog(this, 
//                        "Tanggal 'Dari' tidak boleh melebihi 'Sampai'!",
//                        "Peringatan", JOptionPane.WARNING_MESSAGE);
//                    dateFrom.setDate(null);
//                }
//            }
//        });
//
//        dateTo.getDateEditor().addPropertyChangeListener("date", evt -> {
//            if (dateFrom.getDate() != null && dateTo.getDate() != null) {
//                if (dateTo.getDate().before(dateFrom.getDate())) {
//                    JOptionPane.showMessageDialog(this, 
//                        "Tanggal 'Sampai' tidak boleh kurang dari 'Dari'!",
//                        "Peringatan", JOptionPane.WARNING_MESSAGE);
//                    dateTo.setDate(null);
//                }
//            }
//        });
        
        // ===== EVENT FILTER LANGSUNG =====
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadHistory(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadHistory(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadHistory(); }
        });

        cmbFilterPeriode.addActionListener(e -> {
            handlePeriodeChange();
            loadHistory();
        });

        cmbFilterJenis.addActionListener(e -> loadHistory());
        cmbFilterMetode.addActionListener(e -> loadHistory());

//        // Untuk date chooser (langsung update)
//        dateFrom.getDateEditor().addPropertyChangeListener("date", evt -> {
//            if (dateTo.getDate() != null && dateFrom.getDate() != null) {
//                if (dateFrom.getDate().after(dateTo.getDate())) {
//                    JOptionPane.showMessageDialog(this, 
//                        "Tanggal 'Dari' tidak boleh melebihi 'Sampai'!",
//                        "Peringatan", JOptionPane.WARNING_MESSAGE);
//                    dateFrom.setDate(null);
//                } else {
//                    loadHistory();
//                }
//            } else {
//                loadHistory();
//            }
//        });
//
//        dateTo.getDateEditor().addPropertyChangeListener("date", evt -> {
//            if (dateFrom.getDate() != null && dateTo.getDate() != null) {
//                if (dateTo.getDate().before(dateFrom.getDate())) {
//                    JOptionPane.showMessageDialog(this, 
//                        "Tanggal 'Sampai' tidak boleh kurang dari 'Dari'!",
//                        "Peringatan", JOptionPane.WARNING_MESSAGE);
//                    dateTo.setDate(null);
//                } else {
//                    loadHistory();
//                }
//            } else {
//                loadHistory();
//            }
//        });


        
        // Buttons
//        JButton btnFilter = new JButton("🔍 Filter");
//        btnFilter.setFont(new Font("Poppins", Font.BOLD, 12));
//        btnFilter.setBackground(new Color(52, 152, 219));
//        btnFilter.setForeground(new Color(155, 89, 182)); // ✅ UNGU
//        btnFilter.setBorder(BorderFactory.createEmptyBorder());
//        btnFilter.setFocusPainted(false);
//        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btnFilter.setBounds(520, 60, 100, 30);
//        btnFilter.addActionListener(e -> loadHistory());
//        panelFilter.add(btnFilter);
        
        JButton btnReset = new JButton("🔄 Reset");
        btnReset.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnReset.setBackground(new Color(149, 165, 166));
        btnReset.setForeground(new Color(155, 89, 182)); // ✅ UNGU
//        btnFilter.setBorder(BorderFactory.createEmptyBorder());
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.setBounds(630, 60, 100, 30);
        btnReset.addActionListener(e -> resetFilter());
        panelFilter.add(btnReset);
        
        JButton btnDetail = new JButton("👁️ Lihat Detail");
        btnDetail.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnDetail.setBackground(new Color(155, 89, 182));
        btnDetail.setForeground(new Color(155, 89, 182)); // ✅ UNGU
        btnDetail.setBorder(BorderFactory.createEmptyBorder());
        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetail.setBounds(750, 60, 120, 30);
        btnDetail.addActionListener(e -> lihatDetail());
        panelFilter.add(btnDetail);
        
        add(panelFilter);
        
        
        // ===== TABEL HISTORY =====
        JPanel panelTabel = new JPanel();
        panelTabel.setLayout(null);
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBounds(40, 205, 1040, 385);
        panelTabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JLabel lblTableTitle = new JLabel("Daftar Transaksi");
        lblTableTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        lblTableTitle.setBounds(20, 10, 200, 25);
        panelTabel.add(lblTableTitle);
        
        String[] columns = {"ID", "No Transaksi", "Tanggal", "Jenis", "Metode", "Total", "Kasir"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableHistory = new JTable(modelTable);
        tableHistory.setFont(new Font("Poppins", Font.PLAIN, 11));
        tableHistory.setRowHeight(28);
        tableHistory.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 11));
        tableHistory.getTableHeader().setBackground(new Color(52, 73, 94));
        tableHistory.getTableHeader().setForeground(new Color(155, 89, 182)); // ✅ UNGU
        tableHistory.setSelectionBackground(new Color(189, 195, 199));
        
        // Hide ID column
        tableHistory.getColumnModel().getColumn(0).setMinWidth(0);
        tableHistory.getColumnModel().getColumn(0).setMaxWidth(0);
        tableHistory.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        tableHistory.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableHistory.getColumnModel().getColumn(2).setPreferredWidth(130);
        tableHistory.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableHistory.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableHistory.getColumnModel().getColumn(5).setPreferredWidth(80);
        tableHistory.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        JScrollPane scrollTable = new JScrollPane(tableHistory);
        scrollTable.setBounds(20, 45, 1080, 325);
        panelTabel.add(scrollTable);
        
        add(panelTabel);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            int panelWidth = getWidth() - 60; // jaga jarak kanan-kiri 30px
            panelFilter.setBounds(30, 70, panelWidth, 120);
            panelTabel.setBounds(30, 205, panelWidth, getHeight() - 240);
            
            // atur ulang scroll table agar menyesuaikan panel
            scrollTable.setBounds(20, 45, panelTabel.getWidth() - 40, panelTabel.getHeight() - 70);
            }
        });
    }
    
    private void handlePeriodeChange() {
        String periode = (String) cmbFilterPeriode.getSelectedItem();
    }

    
    private void loadHistory() {
        try {
            modelTable.setRowCount(0);
            
            Connection conn = Koneksi.getKoneksi();
            StringBuilder sql = new StringBuilder(
                "SELECT t.id_transaksi, t.no_transaksi, t.tgl_transaksi, " +
                "t.jenis_transaksi, t.metode_pembayaran, t.total_harga, u.nama_lengkap " +
                "FROM tb_transaksi t " +
                "JOIN tb_user u ON t.id_kasir = u.id_user " +
                "WHERE 1=1"
            );
            
            // Filter search
            String search = txtCari.getText().trim();
            if (!search.isEmpty()) {
                sql.append(" AND (t.no_transaksi LIKE '%").append(search).append("%' ")
                   .append("OR u.nama_lengkap LIKE '%").append(search).append("%'")
                   .append("OR id_transaksi LIKE '%").append(search).append("%'")
                   .append("OR jenis_transaksi LIKE '%").append(search).append("%'")
                   .append("OR metode_pembayaran LIKE '%").append(search).append("%'")
                   .append("OR total_harga LIKE '%").append(search).append("%')");
            }
            
            // Filter jenis
            String jenis = (String) cmbFilterJenis.getSelectedItem();
            if (jenis != null && !jenis.equals("Semua")) {
                sql.append(" AND t.jenis_transaksi = '").append(jenis).append("'");
            }
            
            // Filter metode
            String metode = (String) cmbFilterMetode.getSelectedItem();
            if (metode != null && !metode.equals("Semua")) {
                sql.append(" AND t.metode_pembayaran = '").append(metode).append("'");
            }
            
            // Filter periode
            String periode = (String) cmbFilterPeriode.getSelectedItem();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            switch (periode) {
                case "Hari Ini":
                    sql.append(" AND DATE(t.tgl_transaksi) = CURDATE()");
                    break;
                case "Minggu Ini":
                    sql.append(" AND YEARWEEK(t.tgl_transaksi, 1) = YEARWEEK(CURDATE(), 1)");
                    break;
                case "Bulan Ini":
                    sql.append(" AND MONTH(t.tgl_transaksi) = MONTH(CURDATE()) AND YEAR(t.tgl_transaksi) = YEAR(CURDATE())");
                    break;
            }
            
            sql.append(" ORDER BY t.tgl_transaksi DESC");
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());
            
            SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            
            while (rs.next()) {
//                String pelanggan = rs.getString("nama_pelanggan");
//                if (pelanggan == null || pelanggan.trim().isEmpty()) {
//                    pelanggan = "-";
//                }
                
                Object[] row = {
                    rs.getInt("id_transaksi"),
                    rs.getString("no_transaksi"),
                    sdfDisplay.format(rs.getTimestamp("tgl_transaksi")),
//                    pelanggan,
                    rs.getString("jenis_transaksi"),
                    rs.getString("metode_pembayaran"),
                    "Rp " + String.format("%,.0f", rs.getDouble("total_harga")),
                    rs.getString("nama_lengkap")
                };
                modelTable.addRow(row);
            }
            
            rs.close();
            st.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void resetFilter() {
        txtCari.setText("");
        cmbFilterPeriode.setSelectedIndex(0);
        cmbFilterJenis.setSelectedIndex(0);
        cmbFilterMetode.setSelectedIndex(0);
        loadHistory();
    }
    
    private void lihatDetail() {
        int row = tableHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi yang akan dilihat!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int idTransaksi = (int) modelTable.getValueAt(row, 0);
            showDetailDialog(idTransaksi);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showDetailDialog(int idTransaksi) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Transaksi", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBounds(0, 0, 600, 500);
        
        try {
            Connection conn = Koneksi.getKoneksi();
            
            // Get header transaksi
            String sqlHeader = "SELECT t.*, u.nama_lengkap FROM tb_transaksi t " +
                               "JOIN tb_user u ON t.id_kasir = u.id_user WHERE t.id_transaksi = ?";
            PreparedStatement psHeader = conn.prepareStatement(sqlHeader);
            psHeader.setInt(1, idTransaksi);
            ResultSet rsHeader = psHeader.executeQuery();
            
            int yPos = 20;
            
            if (rsHeader.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                
                addDetailLabel(panel, "No Transaksi:", rsHeader.getString("no_transaksi"), yPos);
                yPos += 30;
                addDetailLabel(panel, "Tanggal:", sdf.format(rsHeader.getTimestamp("tgl_transaksi")), yPos);
                yPos += 30;
//                addDetailLabel(panel, "Pelanggan:", rsHeader.getString("nama_pelanggan") != null ? rsHeader.getString("nama_pelanggan") : "-", yPos);
                yPos += 30;
                addDetailLabel(panel, "Jenis Transaksi:", rsHeader.getString("jenis_transaksi"), yPos);
                yPos += 30;
                addDetailLabel(panel, "Metode Pembayaran:", rsHeader.getString("metode_pembayaran"), yPos);
                yPos += 30;
                addDetailLabel(panel, "Kasir:", rsHeader.getString("nama_lengkap"), yPos);
                yPos += 40;
                
                // Detail Items
                JLabel lblItems = new JLabel("Detail Item:");
                lblItems.setFont(new Font("Poppins", Font.BOLD, 13));
                lblItems.setBounds(20, yPos, 150, 25);
                panel.add(lblItems);
                yPos += 35;
                
                JTextArea txtItems = new JTextArea();
                txtItems.setFont(new Font("Poppins", Font.PLAIN, 12));
                txtItems.setEditable(false);
                txtItems.setLineWrap(true);
                txtItems.setWrapStyleWord(true);
                
                StringBuilder items = new StringBuilder();
                
                // Get detail barang
                String sqlBarang = "SELECT b.nama_barang, d.quantity, d.harga_satuan, d.subtotal " +
                                   "FROM tb_detail_transaksi_barang d " +
                                   "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                                   "WHERE d.id_transaksi = ?";
                PreparedStatement psBarang = conn.prepareStatement(sqlBarang);
                psBarang.setInt(1, idTransaksi);
                ResultSet rsBarang = psBarang.executeQuery();
                
                if (rsBarang.isBeforeFirst()) {
                    items.append("BARANG:\n");
                    while (rsBarang.next()) {
                        items.append("• ").append(rsBarang.getString("nama_barang"))
                             .append(" (").append(rsBarang.getInt("quantity")).append("x)")
                             .append(" @ Rp ").append(String.format("%,.0f", rsBarang.getDouble("harga_satuan")))
                             .append(" = Rp ").append(String.format("%,.0f", rsBarang.getDouble("subtotal")))
                             .append("\n");
                    }
                    items.append("\n");
                }
                rsBarang.close();
                psBarang.close();
                
                // Get detail layanan
                String sqlLayanan = "SELECT l.nama_layanan, d.quantity, d.harga_satuan, d.subtotal " +
                                    "FROM tb_detail_transaksi_layanan d " +
                                    "JOIN tb_layanan l ON d.id_layanan = l.id_layanan " +
                                    "WHERE d.id_transaksi = ?";
                PreparedStatement psLayanan = conn.prepareStatement(sqlLayanan);
                psLayanan.setInt(1, idTransaksi);
                ResultSet rsLayanan = psLayanan.executeQuery();
                
                if (rsLayanan.isBeforeFirst()) {
                    items.append("LAYANAN:\n");
                    while (rsLayanan.next()) {
                        items.append("• ").append(rsLayanan.getString("nama_layanan"))
                             .append(" (").append(rsLayanan.getInt("quantity")).append("x)")
                             .append(" @ Rp ").append(String.format("%,.0f", rsLayanan.getDouble("harga_satuan")))
                             .append(" = Rp ").append(String.format("%,.0f", rsLayanan.getDouble("subtotal")))
                             .append("\n");
                    }
                }
                rsLayanan.close();
                psLayanan.close();
                
                txtItems.setText(items.toString());
                
                JScrollPane scrollItems = new JScrollPane(txtItems);
                scrollItems.setBounds(20, yPos, 550, 150);
                panel.add(scrollItems);
                yPos += 160;
                
                // Total
                JLabel lblTotal = new JLabel("TOTAL: Rp " + String.format("%,.0f", rsHeader.getDouble("total_harga")));
                lblTotal.setFont(new Font("Poppins", Font.BOLD, 16));
                lblTotal.setForeground(new Color(52, 152, 219));
                lblTotal.setBounds(20, yPos, 300, 30);
                panel.add(lblTotal);
            }
            
            rsHeader.close();
            psHeader.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error load detail: " + e.getMessage());
            e.printStackTrace();
        }
        
        JButton btnTutup = new JButton("Tutup");
        btnTutup.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnTutup.setBackground(new Color(149, 165, 166));
        btnTutup.setForeground(new Color(149, 165, 166));
        btnTutup.setBorder(BorderFactory.createEmptyBorder());
        btnTutup.setFocusPainted(false);
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.setBounds(470, 420, 100, 35);
        btnTutup.addActionListener(e -> dialog.dispose());
        panel.add(btnTutup);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void addDetailLabel(JPanel panel, String label, String value, int y) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblLabel.setForeground(Color.GRAY);
        lblLabel.setBounds(20, y, 150, 25);
        panel.add(lblLabel);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Poppins", Font.BOLD, 12));
        lblValue.setBounds(180, y, 400, 25);
        panel.add(lblValue);
    }
}