package view.admin;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.text.*;

/**
 * Panel Kelola Barang - CRUD dan Manage Stok
 * Versi Responsif, Live Search, Tampilkan ID, Validasi Angka, Satuan Lebar
 */
public class KelolaBarangPanel extends JPanel {

    private JTable tableBarang;
    private DefaultTableModel modelTable;
    private JTextField txtCari;
    private JComboBox<String> cmbKategori;
    private JComboBox<String> cmbFilterStatus;

    // Form components
    private JTextField txtNamaBarang;
    private JTextField txtHarga;
    private JTextField txtStok;
    private JTextField txtSatuan;
    private JTextArea txtDeskripsi;
    private JComboBox<String> cmbKategoriForm;
    private JComboBox<String> cmbStatus;

    private int idBarangEdit = -1;

    // === Dokumen khusus: hanya angka ===
    private static class NumberOnlyDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) 
                throws BadLocationException {
            if (str == null) return;
            if (str.matches("\\d*")) {
                super.insertString(offs, str, a);
            }
        }
    }

    public KelolaBarangPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadKategori();
        loadDataBarang();
    }

    private void initComponents() {
        // Title
        JLabel lblTitle = new JLabel("📦 Kelola Barang");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // ===== PANEL UTAMA (KIRI + KANAN) =====
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(new Color(240, 242, 245));

        // ===== PANEL KIRI (Tabel & Filter) =====
        JPanel panelKiri = new JPanel();
        panelKiri.setLayout(new BorderLayout(10, 10));
        panelKiri.setBackground(Color.WHITE);
        panelKiri.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblCari = new JLabel("🔍 Cari Barang:");
        lblCari.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblCari.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblCari);

        txtCari = new JTextField(20);
        txtCari.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        headerPanel.add(txtCari);

        // Live Search
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadDataBarang();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadDataBarang();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadDataBarang();
            }
        });

        JLabel lblKategori = new JLabel("Kategori:");
        lblKategori.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblKategori.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblKategori);

        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbKategori.addActionListener(e -> loadDataBarang());
        headerPanel.add(cmbKategori);

        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "Tersedia", "Habis"});
        cmbFilterStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterStatus.addActionListener(e -> loadDataBarang());
        headerPanel.add(cmbFilterStatus);

        panelKiri.add(headerPanel, BorderLayout.NORTH);

        // Tabel Barang
        String[] columns = {"ID", "Nama Barang", "Kategori", "Harga", "Stok", "Satuan", "Status"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBarang = new JTable(modelTable);
        tableBarang.setFont(new Font("Poppins", Font.PLAIN, 12));
        tableBarang.setRowHeight(30);
        tableBarang.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        tableBarang.getTableHeader().setBackground(new Color(70, 130, 180));
        tableBarang.getTableHeader().setForeground(new Color(70, 130, 180));
        tableBarang.setSelectionBackground(new Color(135, 206, 250));
        tableBarang.setSelectionForeground(Color.BLACK);

        // Lebar kolom — ID ditampilkan
        tableBarang.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        tableBarang.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama
        tableBarang.getColumnModel().getColumn(2).setPreferredWidth(120); // Kategori
        tableBarang.getColumnModel().getColumn(3).setPreferredWidth(100); // Harga
        tableBarang.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stok
        tableBarang.getColumnModel().getColumn(5).setPreferredWidth(90);  // Satuan (sedikit lebih lebar)
        tableBarang.getColumnModel().getColumn(6).setPreferredWidth(100); // Status

        JScrollPane scrollTable = new JScrollPane(tableBarang);
        panelKiri.add(scrollTable, BorderLayout.CENTER);

        // Buttons bawah tabel — lebih besar, warna teks tetap biru tua
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("✏️ Edit");
        btnEdit.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setForeground(new Color(70, 130, 180)); // ✅ tetap biru tua
        btnEdit.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> editBarang());
        btnEdit.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnEdit);

        JButton btnHapus = new JButton("🗑️ Hapus");
        btnHapus.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(new Color(70, 130, 180)); // ✅ tetap biru tua
        btnHapus.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnHapus.setFocusPainted(false);
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.addActionListener(e -> hapusBarang());
        btnHapus.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnHapus);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(new Color(70, 130, 180)); // ✅ tetap biru tua
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            clearForm();
            loadDataBarang();
        });
        btnRefresh.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnRefresh);

        panelKiri.add(buttonPanel, BorderLayout.SOUTH);

        // ===== PANEL KANAN (Form Input) =====
        JPanel panelKanan = new JPanel();
        panelKanan.setLayout(new BorderLayout(10, 10));
        panelKanan.setBackground(Color.WHITE);
        panelKanan.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel lblFormTitle = new JLabel("Form Barang");
        lblFormTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(70, 130, 180));
        lblFormTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelKanan.add(lblFormTitle, BorderLayout.NORTH);

        // Form Content
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nama Barang
        JLabel lblNama = new JLabel("Nama Barang *");
        lblNama.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNama.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(lblNama, gbc);

        txtNamaBarang = new JTextField();
        txtNamaBarang.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtNamaBarang.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(txtNamaBarang, gbc);

        // Kategori
        JLabel lblKategoriForm = new JLabel("Kategori *");
        lblKategoriForm.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblKategoriForm.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(lblKategoriForm, gbc);

        cmbKategoriForm = new JComboBox<>();
        cmbKategoriForm.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(cmbKategoriForm, gbc);

        // Harga → HANYA ANGKA
        JLabel lblHarga = new JLabel("Harga Jual (Rp) *");
        lblHarga.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblHarga.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(lblHarga, gbc);

        txtHarga = new JTextField();
        txtHarga.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtHarga.setDocument(new NumberOnlyDocument()); // 🔒 HANYA ANGKA
        txtHarga.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(txtHarga, gbc);

        // Stok → HANYA ANGKA
        JLabel lblStok = new JLabel("Stok *");
        lblStok.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblStok.setForeground(new Color(60, 60, 60));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(lblStok, gbc);

        txtStok = new JTextField();
        txtStok.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtStok.setDocument(new NumberOnlyDocument()); // 🔒 HANYA ANGKA
        txtStok.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(txtStok, gbc);

        // Satuan → LEBAR CUKUP
        JLabel lblSatuan = new JLabel("Satuan *");
        lblSatuan.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSatuan.setForeground(new Color(60, 60, 60));
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(lblSatuan, gbc);

        txtSatuan = new JTextField("pcs");
        txtSatuan.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtSatuan.setColumns(8); // 🔑 Lebar cukup untuk "buah", "kg", dll
        txtSatuan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(txtSatuan, gbc);

        // Status
        JLabel lblStatus = new JLabel("Status *");
        lblStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        formPanel.add(lblStatus, gbc);

        cmbStatus = new JComboBox<>(new String[]{"Tersedia", "Habis"});
        cmbStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(cmbStatus, gbc);

        // Deskripsi
        JLabel lblDeskripsi = new JLabel("Deskripsi");
        lblDeskripsi.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblDeskripsi.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        formPanel.add(lblDeskripsi, gbc);

        txtDeskripsi = new JTextArea(3, 20);
        txtDeskripsi.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JScrollPane scrollDeskripsi = new JScrollPane(txtDeskripsi);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollDeskripsi, gbc);

        panelKanan.add(formPanel, BorderLayout.CENTER);

        // Buttons bawah form — warna teks tetap biru tua
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        formButtonPanel.setBackground(Color.WHITE);

        JButton btnSimpan = new JButton("💾 Simpan");
        btnSimpan.setFont(new Font("Poppins", Font.BOLD, 14));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(new Color(70, 130, 180)); // ✅ tetap biru tua
        btnSimpan.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> simpanBarang());
        btnSimpan.setPreferredSize(new Dimension(100, 40));
        formButtonPanel.add(btnSimpan);

        JButton btnBatal = new JButton("❌ Batal");
        btnBatal.setFont(new Font("Poppins", Font.BOLD, 14));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(new Color(70, 130, 180)); // ✅ tetap biru tua
        btnBatal.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnBatal.setFocusPainted(false);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> clearForm());
        btnBatal.setPreferredSize(new Dimension(100, 40));
        formButtonPanel.add(btnBatal);

        panelKanan.add(formButtonPanel, BorderLayout.SOUTH);

        // Gabungkan
        mainPanel.add(panelKiri);
        mainPanel.add(panelKanan);

        add(lblTitle, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadKategori() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM tb_kategori_barang ORDER BY nama_kategori";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            cmbKategori.removeAllItems();
            cmbKategori.addItem("Semua");

            cmbKategoriForm.removeAllItems();
            cmbKategoriForm.addItem("- Pilih Kategori -");

            while (rs.next()) {
                String kategori = rs.getString("nama_kategori");
                cmbKategori.addItem(kategori);
                cmbKategoriForm.addItem(kategori);
            }

            rs.close();
            st.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load kategori: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDataBarang() {
        try {
            modelTable.setRowCount(0);

            Connection conn = Koneksi.getKoneksi();
            StringBuilder sql = new StringBuilder(
                "SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.harga_jual, " +
                "b.stok, b.satuan, b.status " +
                "FROM tb_barang b " +
                "LEFT JOIN tb_kategori_barang k ON b.id_kategori = k.id_kategori " +
                "WHERE 1=1"
            );

            String search = txtCari.getText().trim();
            if (!search.isEmpty()) {
                if (search.matches("\\d+")) {
                    sql.append(" AND b.id_barang = ").append(search);
                } else {
                    sql.append(" AND b.nama_barang LIKE '%").append(search).append("%'");
                }
            }

            String kategori = (String) cmbKategori.getSelectedItem();
            if (kategori != null && !kategori.equals("Semua")) {
                sql.append(" AND k.nama_kategori = '").append(kategori).append("'");
            }

            String status = (String) cmbFilterStatus.getSelectedItem();
            if (status != null && !status.equals("Semua")) {
                sql.append(" AND b.status = '").append(status).append("'");
            }

            sql.append(" ORDER BY b.id_barang");

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("nama_kategori"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_jual")),
                    rs.getInt("stok"),
                    rs.getString("satuan"),
                    rs.getString("status")
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

    private void simpanBarang() {
        if (txtNamaBarang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama barang harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNamaBarang.requestFocus();
            return;
        }

        if (cmbKategoriForm.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Kategori harus dipilih!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtHarga.requestFocus();
            return;
        }

        if (txtStok.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stok harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtStok.requestFocus();
            return;
        }

        try {
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());

            if (harga <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (stok < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection conn = Koneksi.getKoneksi();

            String sqlKategori = "SELECT id_kategori FROM tb_kategori_barang WHERE nama_kategori = ?";
            PreparedStatement psKategori = conn.prepareStatement(sqlKategori);
            psKategori.setString(1, (String) cmbKategoriForm.getSelectedItem());
            ResultSet rsKategori = psKategori.executeQuery();

            int idKategori = 0;
            if (rsKategori.next()) {
                idKategori = rsKategori.getInt("id_kategori");
            }
            rsKategori.close();
            psKategori.close();

            if (idBarangEdit == -1) {
                String sql = "INSERT INTO tb_barang (id_kategori, nama_barang, harga_jual, stok, satuan, deskripsi, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idKategori);
                ps.setString(2, txtNamaBarang.getText().trim());
                ps.setDouble(3, harga);
                ps.setInt(4, stok);
                ps.setString(5, txtSatuan.getText().trim());
                ps.setString(6, txtDeskripsi.getText().trim());
                ps.setString(7, (String) cmbStatus.getSelectedItem());

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String sql = "UPDATE tb_barang SET id_kategori=?, nama_barang=?, harga_jual=?, stok=?, " +
                             "satuan=?, deskripsi=?, status=? WHERE id_barang=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idKategori);
                ps.setString(2, txtNamaBarang.getText().trim());
                ps.setDouble(3, harga);
                ps.setInt(4, stok);
                ps.setString(5, txtSatuan.getText().trim());
                ps.setString(6, txtDeskripsi.getText().trim());
                ps.setString(7, (String) cmbStatus.getSelectedItem());
                ps.setInt(8, idBarangEdit);

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Barang berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            loadDataBarang();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error simpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editBarang() {
        int row = tableBarang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            idBarangEdit = (int) modelTable.getValueAt(row, 0);

            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT b.*, k.nama_kategori FROM tb_barang b " +
                         "LEFT JOIN tb_kategori_barang k ON b.id_kategori = k.id_kategori " +
                         "WHERE b.id_barang = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idBarangEdit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtNamaBarang.setText(rs.getString("nama_barang"));
                cmbKategoriForm.setSelectedItem(rs.getString("nama_kategori"));
                double harga = rs.getDouble("harga_jual");
                txtHarga.setText(String.valueOf((int) harga));
                txtStok.setText(String.valueOf(rs.getInt("stok")));
                txtSatuan.setText(rs.getString("satuan"));
                txtDeskripsi.setText(rs.getString("deskripsi"));
                cmbStatus.setSelectedItem(rs.getString("status"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hapusBarang() {
        int row = tableBarang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus barang ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idBarang = (int) modelTable.getValueAt(row, 0);

                Connection conn = Koneksi.getKoneksi();
                String sql = "DELETE FROM tb_barang WHERE id_barang = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idBarang);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDataBarang();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        idBarangEdit = -1;
        txtNamaBarang.setText("");
        cmbKategoriForm.setSelectedIndex(0);
        txtHarga.setText("");
        txtStok.setText("");
        txtSatuan.setText("pcs");
        txtDeskripsi.setText("");
        cmbStatus.setSelectedIndex(0);
        txtNamaBarang.requestFocus();
        txtCari.setText("");
    }
}