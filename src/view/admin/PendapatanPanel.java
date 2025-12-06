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
 * + TOTAL KEUNTUNGAN (Barang & Layanan terpisah)
 */
public class PendapatanPanel extends JPanel {

    private JTable tablePendapatan;
    private DefaultTableModel modelTable;
    private JComboBox<String> cmbFilter;
    private JComboBox<String> cmbFilterKasir;

    private JLabel lblTotalCash;
    private JLabel lblTotalQRIS;
    private JLabel lblTotalSemua;
    private JLabel lblJumlahTransaksi;
    private JLabel lblTotalKeuntungan;

    // Referensi komponen untuk resize
    private JPanel panelFilter;
    private JPanel panelStats;
    private JPanel panelTabel;
    private JScrollPane scrollTable;
    private JButton btnExport;

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
        panelFilter = new JPanel();
        panelFilter.setLayout(null);
        panelFilter.setBackground(Color.WHITE);
        panelFilter.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        add(panelFilter);

        JLabel lblFilterBy = new JLabel("Filter Laporan:");
        lblFilterBy.setFont(new Font("Poppins", Font.BOLD, 13));
        lblFilterBy.setBounds(20, 15, 120, 20);
        panelFilter.add(lblFilterBy);

        cmbFilter = new JComboBox<>(new String[]{"Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini", "Semua Waktu"});
        cmbFilter.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilter.setBounds(20, 40, 150, 30);
        panelFilter.add(cmbFilter);

        JLabel lblKasir = new JLabel("Kasir:");
        lblKasir.setFont(new Font("Poppins", Font.BOLD, 13));
        lblKasir.setBounds(190, 15, 60, 20);
        panelFilter.add(lblKasir);

        cmbFilterKasir = new JComboBox<>();
        cmbFilterKasir.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterKasir.setBounds(190, 40, 200, 30);
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

        btnExport = new JButton("📄 Export");
        styleButtonAsTextBlue(btnExport);
        btnExport.addActionListener(e -> exportLaporan());
        panelFilter.add(btnExport);

        // ===== PANEL STATISTIK =====
        panelStats = new JPanel();
        panelStats.setLayout(null);
        panelStats.setBackground(Color.WHITE);
        panelStats.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        add(panelStats);

        lblTotalCash = createStatCard("💵 Total Cash", "Rp 0", new Color(46, 204, 113));
        lblTotalQRIS = createStatCard("📱 Total QRIS", "Rp 0", new Color(52, 152, 219));
        lblTotalSemua = createStatCard("💰 Total Pendapatan", "Rp 0", new Color(155, 89, 182));
        lblTotalKeuntungan = createStatCard("📈 Total Keuntungan", "Rp 0", new Color(230, 126, 34));
        lblJumlahTransaksi = createStatCard("📊 Jumlah Transaksi", "0 Transaksi", new Color(241, 196, 15));

        // ===== TABEL DETAIL =====
        panelTabel = new JPanel();
        panelTabel.setLayout(null);
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        add(panelTabel);

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
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(new Color(52, 152, 219));
        header.setOpaque(true);
        tablePendapatan.setSelectionBackground(new Color(135, 206, 250));

        scrollTable = new JScrollPane(tablePendapatan);
        panelTabel.add(scrollTable);

        // ===== RESIZE LISTENER =====
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutComponents();
            }
        });
    }

    private void layoutComponents() {
        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) return;

        // Title tetap di (30, 20)
        // Panel Filter
        panelFilter.setBounds(30, 70, width - 60, 80);
        btnExport.setBounds(width - 150, 40, 100, 30);

        // Panel Stats
        panelStats.setBounds(30, 165, width - 60, 100);
        layoutStatCards();

        // Panel Tabel
        int tableHeight = Math.max(200, height - 350);
        panelTabel.setBounds(30, 280, width - 60, tableHeight);
        scrollTable.setBounds(20, 45, width - 100, tableHeight - 60);

        // Atur lebar kolom tabel
        int tableWidth = width - 100;
        if (tableWidth > 0 && tablePendapatan.getColumnModel().getColumnCount() == 6) {
            tablePendapatan.getColumnModel().getColumn(0).setPreferredWidth(Math.max(100, (int)(tableWidth * 0.15)));
            tablePendapatan.getColumnModel().getColumn(1).setPreferredWidth(Math.max(100, (int)(tableWidth * 0.15)));
            tablePendapatan.getColumnModel().getColumn(2).setPreferredWidth(Math.max(80, (int)(tableWidth * 0.12)));
            tablePendapatan.getColumnModel().getColumn(3).setPreferredWidth(Math.max(100, (int)(tableWidth * 0.15)));
            tablePendapatan.getColumnModel().getColumn(4).setPreferredWidth(Math.max(100, (int)(tableWidth * 0.15)));
            tablePendapatan.getColumnModel().getColumn(5).setPreferredWidth(Math.max(150, (int)(tableWidth * 0.20)));
        }
    }

    private void layoutStatCards() {
        int width = panelStats.getWidth();
        if (width <= 0) return;

        int cardCount = 5;
        int padding = 10;
        int totalPadding = padding * (cardCount - 1);
        int cardWidth = (width - 40 - totalPadding) / cardCount;
        cardWidth = Math.max(120, cardWidth); // minimal lebar

        int startX = 20;
        int startY = 15;
        int cardHeight = 70;

        placeStatCard(lblTotalCash, startX, startY, cardWidth, cardHeight);
        placeStatCard(lblTotalQRIS, startX + cardWidth + padding, startY, cardWidth, cardHeight);
        placeStatCard(lblTotalSemua, startX + 2 * (cardWidth + padding), startY, cardWidth, cardHeight);
        placeStatCard(lblTotalKeuntungan, startX + 3 * (cardWidth + padding), startY, cardWidth, cardHeight);
        placeStatCard(lblJumlahTransaksi, startX + 4 * (cardWidth + padding), startY, cardWidth, cardHeight);
    }

    private JLabel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(color.darker(), 1));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(15, 10, 200, 20);
        card.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Poppins", Font.BOLD, 14));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBounds(15, 35, 200, 25);
        card.add(lblValue);

        panelStats.add(card);
        return lblValue;
    }

    private void placeStatCard(JLabel label, int x, int y, int width, int height) {
        if (label != null && label.getParent() != null) {
            label.getParent().setBounds(x, y, width, height);
            // Update label value width
            label.setBounds(15, 35, width - 30, 25);
            ((JLabel) label.getParent().getComponent(0)).setBounds(15, 10, width - 30, 20);
        }
    }

    private void styleButtonAsTextBlue(JButton btn) {
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setForeground(new Color(52, 152, 219));
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
    }

    private void loadKasirList() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT id_user, nama_lengkap FROM tb_user WHERE role = 'Kasir' AND status = 'Aktif' ORDER BY nama_lengkap";
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
            boolean filterKasir = !selectedKasir.equals("Semua Kasir");
            if (filterKasir) {
                kasirFilter = " AND u.nama_lengkap = ?";
            }

            // === QUERY KEUNTUNGAN: GABUNGKAN BARANG & LAYANAN ===
            String sqlKeuntungan = 
                "SELECT " +
                "   COALESCE(SUM(CASE WHEN t.metode_pembayaran = 'Cash' THEN t.total_harga ELSE 0 END), 0) AS total_cash, " +
                "   COALESCE(SUM(CASE WHEN t.metode_pembayaran = 'QRIS' THEN t.total_harga ELSE 0 END), 0) AS total_qris, " +
                "   COALESCE(SUM(t.total_harga), 0) AS total_semua, " +
                "   COUNT(*) AS jumlah_transaksi, " +
                "   COALESCE(SUM(keuntungan.keuntungan_transaksi), 0) AS total_keuntungan " +
                "FROM tb_transaksi t " +
                "JOIN tb_user u ON t.id_kasir = u.id_user " +
                "LEFT JOIN ( " +
                "   SELECT id_transaksi, SUM((harga_satuan - COALESCE(harga_beli, 0)) * quantity) AS keuntungan_transaksi " +
                "   FROM tb_detail_transaksi_barang " +
                "   GROUP BY id_transaksi " +
                "   UNION ALL " +
                "   SELECT id_transaksi, SUM(harga_satuan * quantity) AS keuntungan_transaksi " +
                "   FROM tb_detail_transaksi_layanan " +
                "   GROUP BY id_transaksi " +
                ") keuntungan ON t.id_transaksi = keuntungan.id_transaksi " +
                dateFilter + kasirFilter;

            PreparedStatement pst = conn.prepareStatement(sqlKeuntungan);
            if (filterKasir) {
                pst.setString(1, selectedKasir);
            }

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                double totalCash = rs.getDouble("total_cash");
                double totalQRIS = rs.getDouble("total_qris");
                double totalSemua = rs.getDouble("total_semua");
                int jumlahTransaksi = rs.getInt("jumlah_transaksi");
                double totalKeuntungan = rs.getDouble("total_keuntungan");

                lblTotalCash.setText("Rp " + String.format("%,.0f", totalCash));
                lblTotalQRIS.setText("Rp " + String.format("%,.0f", totalQRIS));
                lblTotalSemua.setText("Rp " + String.format("%,.0f", totalSemua));
                lblJumlahTransaksi.setText(jumlahTransaksi + " Transaksi");
                lblTotalKeuntungan.setText("Rp " + String.format("%,.0f", totalKeuntungan));
            }

            rs.close();
            pst.close();

            // === QUERY DETAIL TRANSAKSI ===
            String sqlDetail = 
                "SELECT t.no_transaksi, t.tgl_transaksi, t.jenis_transaksi, " +
                "       t.metode_pembayaran, t.total_harga, u.nama_lengkap " +
                "FROM tb_transaksi t " +
                "JOIN tb_user u ON t.id_kasir = u.id_user " +
                dateFilter + kasirFilter +
                " ORDER BY t.tgl_transaksi DESC";

            PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
            if (filterKasir) {
                pstDetail.setString(1, selectedKasir);
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

            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            for (int i = 0; i < modelTable.getColumnCount(); i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(modelTable.getColumnName(i));
            }

            for (int i = 0; i < modelTable.getRowCount(); i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                for (int j = 0; j < modelTable.getColumnCount(); j++) {
                    Object value = modelTable.getValueAt(i, j);
                    row.createCell(j).setCellValue(value != null ? value.toString() : "");
                }
            }

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