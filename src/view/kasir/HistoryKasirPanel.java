package view.kasir;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.*;

public class HistoryKasirPanel extends JPanel {
    
    private int idKasir;
    private JTable tableHistory;
    private DefaultTableModel modelTable;
    private JTextField txtCari;
    private JComboBox<String> cmbFilterPeriode;

    public HistoryKasirPanel(int idKasir) {
        this.idKasir = idKasir;
        initComponents();
        loadHistory();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JLabel lblTitle = new JLabel("📋 History Transaksi");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // ===== PANEL FILTER =====
        JPanel panelFilter = new JPanel(new GridBagLayout());
        panelFilter.setBackground(Color.WHITE);
        panelFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        panelFilter.setPreferredSize(new Dimension(0, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCari = new JLabel("🔍 Cari:");
        lblCari.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.weightx = 0;
        panelFilter.add(lblCari, gbc);

        txtCari = new JTextField();
        txtCari.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        panelFilter.add(txtCari, gbc);

        JLabel lblPeriode = new JLabel("Periode:");
        lblPeriode.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 2;
        gbc.weightx = 0;
        panelFilter.add(lblPeriode, gbc);

        cmbFilterPeriode = new JComboBox<>(new String[]{"Semua", "Hari Ini", "Minggu Ini", "Bulan Ini"});
        cmbFilterPeriode.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 3;
        gbc.weightx = 0.2;
        panelFilter.add(cmbFilterPeriode, gbc);

        JButton btnDetail = new JButton("👁️ Lihat Detail");
        btnDetail.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnDetail.setBackground(new Color(155, 89, 182));
        btnDetail.setForeground(new Color(155, 89, 182));
        btnDetail.setBorder(BorderFactory.createEmptyBorder());
        btnDetail.setFocusPainted(false);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 4;
        gbc.weightx = 0;
        panelFilter.add(btnDetail, gbc);

        add(panelFilter, BorderLayout.CENTER);

        // ===== TABEL HISTORY =====
        JPanel panelTabel = new JPanel(new BorderLayout());
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        panelTabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel lblTableTitle = new JLabel("Daftar Transaksi Anda");
        lblTableTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 0));
        panelTabel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "No Transaksi", "Tanggal", "Jenis", "Metode", "Total"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHistory = new JTable(modelTable);
        tableHistory.setFont(new Font("Poppins", Font.PLAIN, 11));
        tableHistory.setRowHeight(28);

        JTableHeader header = tableHistory.getTableHeader();
        header.setFont(new Font("Poppins", Font.BOLD, 11));
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK); // Header teks hitam tebal

        tableHistory.setSelectionBackground(new Color(174, 214, 241));

        JScrollPane scrollTable = new JScrollPane(tableHistory);
        panelTabel.add(scrollTable, BorderLayout.CENTER);

        add(panelTabel, BorderLayout.SOUTH);

        // ====== EVENT LISTENER ======
        // Live search
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadHistory(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadHistory(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadHistory(); }
        });

        // Live filter periode
        cmbFilterPeriode.addActionListener(e -> loadHistory());

        // Tombol detail
        btnDetail.addActionListener(e -> lihatDetail());
    }

    private void loadHistory() {
        try {
            modelTable.setRowCount(0);
            Connection conn = Koneksi.getKoneksi();

            StringBuilder sql = new StringBuilder(
                "SELECT id_transaksi, no_transaksi, tgl_transaksi, " +
                "jenis_transaksi, metode_pembayaran, total_harga " +
                "FROM tb_transaksi WHERE id_kasir = ?"
            );

            // Filter search
            String search = txtCari.getText().trim();
            if (!search.isEmpty()) {
                sql.append(" AND (no_transaksi LIKE '%").append(search).append("%' ")
                   .append("OR id_transaksi LIKE '%").append(search).append("%'")
                   .append("OR jenis_transaksi LIKE '%").append(search).append("%'")
                   .append("OR metode_pembayaran LIKE '%").append(search).append("%'")
                   .append("OR total_harga LIKE '%").append(search).append("%')");
            }

            // Filter periode
            String periode = (String) cmbFilterPeriode.getSelectedItem();
            switch (periode) {
                case "Hari Ini":
                    sql.append(" AND DATE(tgl_transaksi) = CURDATE()");
                    break;
                case "Minggu Ini":
                    sql.append(" AND YEARWEEK(tgl_transaksi, 1) = YEARWEEK(CURDATE(), 1)");
                    break;
                case "Bulan Ini":
                    sql.append(" AND MONTH(tgl_transaksi) = MONTH(CURDATE()) AND YEAR(tgl_transaksi) = YEAR(CURDATE())");
                    break;
            }

            sql.append(" ORDER BY tgl_transaksi DESC");
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setInt(1, idKasir);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            while (rs.next()) {
//                String pelanggan = rs.getString("nama_pelanggan");
//                if (pelanggan == null || pelanggan.trim().isEmpty()) pelanggan = "-";

                Object[] row = {
                    rs.getInt("id_transaksi"),
                    rs.getString("no_transaksi"),
                    sdf.format(rs.getTimestamp("tgl_transaksi")),
//                    pelanggan,
                    rs.getString("jenis_transaksi"),
                    rs.getString("metode_pembayaran"),
                    "Rp " + String.format("%,.0f", rs.getDouble("total_harga"))
                };
                modelTable.addRow(row);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
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
            
            String sqlHeader = "SELECT * FROM tb_transaksi WHERE id_transaksi = ?";
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
                addDetailLabel(panel, "Jenis:", rsHeader.getString("jenis_transaksi"), yPos);
                yPos += 30;
                addDetailLabel(panel, "Metode:", rsHeader.getString("metode_pembayaran"), yPos);
                yPos += 40;
                
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
                
                // Barang
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
                
                // Layanan
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
                
                JLabel lblTotal = new JLabel("TOTAL: Rp " + String.format("%,.0f", rsHeader.getDouble("total_harga")));
                lblTotal.setFont(new Font("Poppins", Font.BOLD, 16));
                lblTotal.setForeground(new Color(52, 152, 219));
                lblTotal.setBounds(20, yPos, 300, 30);
                panel.add(lblTotal);
            }
            
            rsHeader.close();
            psHeader.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage());
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