package view.admin;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Panel Laporan Pendapatan - Rekap berdasarkan metode pembayaran
 */
public class PendapatanPanel extends JPanel {

    private JTable tablePendapatan;
    private DefaultTableModel modelTable;
    private JComboBox<String> cmbFilter;
    private JComboBox<String> cmbFilterKasir;
    private JTextField txtSearch;

    private JLabel lblTotalCash;
    private JLabel lblTotalQRIS;
    private JLabel lblTotalSemua;
    private JLabel lblJumlahTransaksi;

    public PendapatanPanel() {
        initComponents();
        loadKasirList();
        loadLaporanHariIni();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(new Color(240, 242, 245));

        // Title
        JLabel lblTitle = new JLabel("💰 Laporan Pendapatan");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBounds(30, 20, 400, 35);
        add(lblTitle);

        // ===== PANEL FILTER =====
        int panelWidth = 1090; // 1120 - 30px padding kanan
        JPanel panelFilter = new JPanel();
        panelFilter.setLayout(null);
        panelFilter.setBackground(Color.WHITE);
        panelFilter.setBounds(30, 70, panelWidth, 80); // lebar dikurangi 30px
        panelFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Search Field (Label di atas, TextField di bawah)
//        JLabel lblSearch = new JLabel("Cari:");
//        lblSearch.setFont(new Font("Poppins", Font.BOLD, 13));
//        lblSearch.setBounds(20, 15, 60, 20);
//        panelFilter.add(lblSearch);

//        txtSearch = new JTextField();
//        txtSearch.setFont(new Font("Poppins", Font.PLAIN, 12));
//        txtSearch.setBounds(20, 40, 150, 25);
//        txtSearch.addActionListener(e -> loadLaporan());
//        panelFilter.add(txtSearch);

        // Filter By
        JLabel lblFilterBy = new JLabel("Filter Laporan:");
        lblFilterBy.setFont(new Font("Poppins", Font.BOLD, 13));
        lblFilterBy.setBounds(190, 15, 120, 20);
        panelFilter.add(lblFilterBy);

        cmbFilter = new JComboBox<>(new String[]{"Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini", "Semua Waktu"});
        cmbFilter.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilter.setBounds(190, 40, 150, 30);
        panelFilter.add(cmbFilter);

        // Kasir
        JLabel lblKasir = new JLabel("Kasir:");
        lblKasir.setFont(new Font("Poppins", Font.BOLD, 13));
        lblKasir.setBounds(360, 15, 60, 20);
        panelFilter.add(lblKasir);

        cmbFilterKasir = new JComboBox<>();
        cmbFilterKasir.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterKasir.setBounds(360, 40, 200, 30);
        panelFilter.add(cmbFilterKasir);
        
        cmbFilter.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadLaporan();
            }
        });

        cmbFilterKasir.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadLaporan();
            }
        });


        // Tombol Tampilkan
//        JButton btnTampilkan = new JButton("📊 Tampilkan");
//        styleButtonAsTextBlue(btnTampilkan);
//        btnTampilkan.setBounds(580, 40, 120, 30);
//        btnTampilkan.addActionListener(e -> loadLaporan());
//        panelFilter.add(btnTampilkan);

        // Tombol Export
        JButton btnExport = new JButton("📄 Export");
        styleButtonAsTextBlue(btnExport);
        btnExport.setBounds(720, 40, 100, 30);
        btnExport.addActionListener(e -> exportLaporan());
        panelFilter.add(btnExport);

        add(panelFilter);

        // ===== PANEL STATISTIK =====
        JPanel panelStats = new JPanel();
        panelStats.setLayout(null);
        panelStats.setBackground(Color.WHITE);
        panelStats.setBounds(30, 165, panelWidth, 100); // lebar dikurangi 30px
        panelStats.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Card 1: Total Cash
        lblTotalCash = createStatCard(panelStats, "💵 Total Cash", "Rp 0", new Color(46, 204, 113), 20, 15, 260);

        // Card 2: Total QRIS
        lblTotalQRIS = createStatCard(panelStats, "📱 Total QRIS", "Rp 0", new Color(52, 152, 219), 300, 15, 260);


        // Card 3: Total Semua
        lblTotalSemua = createStatCard(panelStats, "💰 Total Pendapatan", "Rp 0", new Color(155, 89, 182), 580, 15, 260);


        // Card 4: Jumlah Transaksi
        lblJumlahTransaksi = createStatCard(panelStats, "📊 Jumlah Transaksi", "0 Transaksi", new Color(241, 196, 15), 860, 15, 240);


        add(panelStats);

        // ===== TABEL DETAIL =====
        JPanel panelTabel = new JPanel();
        panelTabel.setLayout(null);
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBounds(30, 280, panelWidth, 310); // lebar dikurangi 30px
        panelTabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel lblDetailTitle = new JLabel("Detail Transaksi");
        lblDetailTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        lblDetailTitle.setBounds(20, 10, 200, 25);
        panelTabel.add(lblDetailTitle);

        String[] columns = {"No Transaksi", "Tanggal", "Jenis", "Metode Pembayaran", "Total", "Kasir"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePendapatan = new JTable(modelTable);
        tablePendapatan.setFont(new Font("Poppins", Font.PLAIN, 11));
        tablePendapatan.setRowHeight(28);
        JTableHeader header = tablePendapatan.getTableHeader();
        header.setFont(new Font("Poppins", Font.BOLD, 11));
        header.setBackground(new Color(52, 152, 219)); // Biru
        header.setForeground(Color.blue); // Teks putih
        header.setOpaque(true);
        tablePendapatan.setSelectionBackground(new Color(135, 206, 250));

        // Set column widths
        tablePendapatan.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablePendapatan.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablePendapatan.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablePendapatan.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablePendapatan.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablePendapatan.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane scrollTable = new JScrollPane(tablePendapatan);
        scrollTable.setBounds(20, 45, panelWidth - 40, 250); // kurangi 40px untuk padding kiri & kanan
        panelTabel.add(scrollTable);

        add(panelTabel);
        
        // === Tambahkan listener di sini ===
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = getWidth() - 60; // padding 30 kiri + 30 kanan

                panelFilter.setBounds(30, 70, panelWidth, 80);
                panelStats.setBounds(30, 165, panelWidth, 100);
                panelTabel.setBounds(30, 280, panelWidth, 310);

                // Sesuaikan juga scroll table di dalam panelTabel
                Component[] comps = panelTabel.getComponents();
                for (Component c : comps) {
                    if (c instanceof JScrollPane) {
                        c.setBounds(20, 45, panelWidth - 40, 250); // biar padding dalam tetap sama
                    }
                }
            }
        });
    }

    private void styleButtonAsTextBlue(JButton btn) {
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setForeground(new Color(52, 152, 219)); // Biru
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color, int x, int y, int width) {
    JPanel card = new JPanel();
    card.setLayout(null);
    card.setBackground(color);
    card.setBounds(x, y, width, 70);
    card.setBorder(BorderFactory.createLineBorder(color.darker(), 1));

    JLabel lblTitle = new JLabel(title);
    lblTitle.setFont(new Font("Poppins", Font.PLAIN, 12));
    lblTitle.setForeground(Color.WHITE);
    lblTitle.setBounds(15, 10, width - 30, 20);
    card.add(lblTitle);

    JLabel lblValue = new JLabel(value);
    lblValue.setFont(new Font("Poppins", Font.BOLD, 18));
    lblValue.setForeground(Color.WHITE);
    lblValue.setBounds(15, 35, width - 30, 25);
    card.add(lblValue);

    parent.add(card);

    // ⚠️ INI BAGIAN PENTING: return label value-nya, bukan panel
    return lblValue;
}

    private void loadKasirList() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT id_user, nama_lengkap FROM tb_user WHERE role = 'Kasir' and status = 'Aktif' ORDER BY nama_lengkap";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            cmbFilterKasir.removeAllItems();
            cmbFilterKasir.addItem("Semua Kasir");

            while (rs.next()) {
                cmbFilterKasir.addItem(rs.getString("nama_lengkap"));
            }

            rs.close();
            st.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load kasir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadLaporanHariIni() {
        cmbFilter.setSelectedItem("Hari Ini");
        loadLaporan();
    }

    private void loadLaporan() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String dateFilter = getDateFilter();

            String kasirFilter = "";
            String selectedKasir = (String) cmbFilterKasir.getSelectedItem();
            if (selectedKasir != null && !selectedKasir.equals("Semua Kasir")) {
                kasirFilter = " AND u.nama_lengkap = ?";
            }

            String searchFilter = "";

            // Query Statistik
            String sqlStats = "SELECT " +
                "COALESCE(SUM(CASE WHEN t.metode_pembayaran = 'Cash' THEN t.total_harga ELSE 0 END), 0) as total_cash, " +
                "COALESCE(SUM(CASE WHEN t.metode_pembayaran = 'QRIS' THEN t.total_harga ELSE 0 END), 0) as total_qris, " +
                "COALESCE(SUM(t.total_harga), 0) as total_semua, " +
                "COUNT(*) as jumlah_transaksi " +
                "FROM tb_transaksi t " +
                "JOIN tb_user u ON t.id_kasir = u.id_user " +
                dateFilter + kasirFilter + searchFilter;

            PreparedStatement pstStats = conn.prepareStatement(sqlStats);
            int paramIndex = 1;
            if (!selectedKasir.equals("Semua Kasir")) {
                pstStats.setString(paramIndex++, selectedKasir);
            }

            ResultSet rsStats = pstStats.executeQuery();
            if (rsStats.next()) {
                double totalCash = rsStats.getDouble("total_cash");
                double totalQRIS = rsStats.getDouble("total_qris");
                double totalSemua = rsStats.getDouble("total_semua");
                int jumlahTransaksi = rsStats.getInt("jumlah_transaksi");

                lblTotalCash.setText("Rp " + String.format("%,.0f", totalCash));
                lblTotalQRIS.setText("Rp " + String.format("%,.0f", totalQRIS));
                lblTotalSemua.setText("Rp " + String.format("%,.0f", totalSemua));
                lblJumlahTransaksi.setText(jumlahTransaksi + " Transaksi");
            }
            rsStats.close();
            pstStats.close();

            // Query Detail
            String sqlDetail = "SELECT t.no_transaksi, t.tgl_transaksi, t.jenis_transaksi, " +
                "t.metode_pembayaran, t.total_harga, u.nama_lengkap " +
                "FROM tb_transaksi t " +
                "JOIN tb_user u ON t.id_kasir = u.id_user " +
                dateFilter + kasirFilter + searchFilter + " ORDER BY t.tgl_transaksi DESC";

            PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
            paramIndex = 1;
            if (!selectedKasir.equals("Semua Kasir")) {
                pstDetail.setString(paramIndex++, selectedKasir);
            }

            ResultSet rsDetail = pstDetail.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            modelTable.setRowCount(0);

            while (rsDetail.next()) {
                Object[] row = {
                    rsDetail.getString("no_transaksi"),
                    sdf.format(rsDetail.getTimestamp("tgl_transaksi")),
                    rsDetail.getString("jenis_transaksi"),
                    rsDetail.getString("metode_pembayaran"),
                    "Rp " + String.format("%,.0f", rsDetail.getDouble("total_harga")),
                    rsDetail.getString("nama_lengkap")
                };
                modelTable.addRow(row);
            }

            rsDetail.close();
            pstDetail.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getDateFilter() {
        String filter = (String) cmbFilter.getSelectedItem();
        switch (filter) {
            case "Hari Ini":
                return " WHERE DATE(t.tgl_transaksi) = CURDATE()";
            case "Kemarin":
                return " WHERE DATE(t.tgl_transaksi) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
            case "Minggu Ini":
                return " WHERE YEARWEEK(t.tgl_transaksi, 1) = YEARWEEK(CURDATE(), 1)";
            case "Bulan Ini":
                return " WHERE MONTH(t.tgl_transaksi) = MONTH(CURDATE()) AND YEAR(t.tgl_transaksi) = YEAR(CURDATE())";
            case "Semua Waktu":
                return " WHERE 1=1";
            default:
                return " WHERE DATE(t.tgl_transaksi) = CURDATE()";
        }
    }

    private void exportLaporan() {
        if (modelTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk di-export!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setSelectedFile(new java.io.File("laporan_pendapatan.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        java.io.File fileToSave = fileChooser.getSelectedFile();

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Laporan Pendapatan");

            // Header
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            for (int i = 0; i < modelTable.getColumnCount(); i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(modelTable.getColumnName(i));
            }

            // Data rows
            for (int i = 0; i < modelTable.getRowCount(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                for (int j = 0; j < modelTable.getColumnCount(); j++) {
                    Object value = modelTable.getValueAt(i, j);
                    row.createCell(j).setCellValue(value != null ? value.toString() : "");
                }
            }

            // Auto-size columns
            for (int i = 0; i < modelTable.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream out = new java.io.FileOutputStream(fileToSave)) {
                workbook.write(out);
            }

            JOptionPane.showMessageDialog(this, "Laporan berhasil diexport ke:\n" + fileToSave.getAbsolutePath(),
                    "Export Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal export: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
