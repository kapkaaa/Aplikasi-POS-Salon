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
 * Panel Kelola Layanan - CRUD Layanan Salon
 * Versi Responsif, Live Search, Tampilkan ID, Validasi Angka, Tombol Lebih Besar
 */
public class KelolaLayananPanel extends JPanel {

    private JTable tableLayanan;
    private DefaultTableModel modelTable;
    private JTextField txtCari;
    private JComboBox<String> cmbFilterStatus;

    // Form components
    private JTextField txtNamaLayanan;
    private JTextField txtHarga;
    private JTextField txtDurasi;
    private JTextArea txtDeskripsi;
    private JComboBox<String> cmbStatus;

    private int idLayananEdit = -1;

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

    public KelolaLayananPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataLayanan();
    }

    private void initComponents() {
        // Title
        JLabel lblTitle = new JLabel("✂️ Kelola Layanan");
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

        JLabel lblCari = new JLabel("🔍 Cari Layanan:");
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
                loadDataLayanan();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadDataLayanan();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadDataLayanan();
            }
        });

        JLabel lblFilter = new JLabel("Status:");
        lblFilter.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblFilter.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblFilter);

        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "Tersedia", "Tidak Tersedia"});
        cmbFilterStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterStatus.addActionListener(e -> loadDataLayanan());
        headerPanel.add(cmbFilterStatus);

        panelKiri.add(headerPanel, BorderLayout.NORTH);

        // Tabel Layanan — TAMPILKAN ID
        String[] columns = {"ID", "Nama Layanan", "Harga", "Durasi (menit)", "Status"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableLayanan = new JTable(modelTable);
        tableLayanan.setFont(new Font("Poppins", Font.PLAIN, 12));
        tableLayanan.setRowHeight(30);
        tableLayanan.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        tableLayanan.getTableHeader().setBackground(new Color(155, 89, 182)); // Ungu
        tableLayanan.getTableHeader().setForeground(new Color(155, 89, 182));
        tableLayanan.setSelectionBackground(new Color(195, 155, 211));
        tableLayanan.setSelectionForeground(Color.BLACK);

        // Lebar kolom — ID ditampilkan
        tableLayanan.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        tableLayanan.getColumnModel().getColumn(1).setPreferredWidth(250); // Nama
        tableLayanan.getColumnModel().getColumn(2).setPreferredWidth(120); // Harga
        tableLayanan.getColumnModel().getColumn(3).setPreferredWidth(120); // Durasi
        tableLayanan.getColumnModel().getColumn(4).setPreferredWidth(120); // Status

        JScrollPane scrollTable = new JScrollPane(tableLayanan);
        panelKiri.add(scrollTable, BorderLayout.CENTER);

        // Buttons bawah tabel — lebih besar, warna teks = ungu tua
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("✏️ Edit");
        btnEdit.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setForeground(new Color(155, 89, 182)); // ✅ SESUAI WARNA HEADER
        btnEdit.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> editLayanan());
        btnEdit.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnEdit);

        JButton btnHapus = new JButton("🗑️ Hapus");
        btnHapus.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(new Color(155, 89, 182)); // ✅ SESUAI WARNA HEADER
        btnHapus.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnHapus.setFocusPainted(false);
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.addActionListener(e -> hapusLayanan());
        btnHapus.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnHapus);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(new Color(155, 89, 182)); // ✅ SESUAI WARNA HEADER
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            clearForm();
            loadDataLayanan();
        });
        btnRefresh.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnRefresh);

        panelKiri.add(buttonPanel, BorderLayout.SOUTH);

        // ===== PANEL KANAN (Form Input) =====
        JPanel panelKanan = new JPanel();
        panelKanan.setLayout(new BorderLayout(10, 10));
        panelKanan.setBackground(Color.WHITE);
        panelKanan.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel lblFormTitle = new JLabel("Form Layanan");
        lblFormTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(155, 89, 182));
        lblFormTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelKanan.add(lblFormTitle, BorderLayout.NORTH);

        // Form Content
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nama Layanan
        JLabel lblNama = new JLabel("Nama Layanan *");
        lblNama.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNama.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblNama, gbc);

        txtNamaLayanan = new JTextField();
        txtNamaLayanan.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtNamaLayanan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(txtNamaLayanan, gbc);

        // Harga → HANYA ANGKA
        JLabel lblHarga = new JLabel("Harga Layanan (Rp) *");
        lblHarga.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblHarga.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 2;
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
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(txtHarga, gbc);

        // Durasi → HANYA ANGKA
        JLabel lblDurasi = new JLabel("Durasi (menit) *");
        lblDurasi.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblDurasi.setForeground(new Color(60, 60, 60));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(lblDurasi, gbc);

        txtDurasi = new JTextField();
        txtDurasi.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtDurasi.setDocument(new NumberOnlyDocument()); // 🔒 HANYA ANGKA
        txtDurasi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(txtDurasi, gbc);

        // Status
        JLabel lblStatus = new JLabel("Status *");
        lblStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(lblStatus, gbc);

        cmbStatus = new JComboBox<>(new String[]{"Tersedia", "Tidak Tersedia"});
        cmbStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(cmbStatus, gbc);

        // Deskripsi
        JLabel lblDeskripsi = new JLabel("Deskripsi");
        lblDeskripsi.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblDeskripsi.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
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
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollDeskripsi, gbc);

        // Info kecil
        JLabel lblInfo = new JLabel("<html><i>* Durasi dalam satuan menit (misal: 30, 60, 90)</i></html>");
        lblInfo.setFont(new Font("Poppins", Font.PLAIN, 10));
        lblInfo.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(lblInfo, gbc);

        panelKanan.add(formPanel, BorderLayout.CENTER);

        // Buttons bawah form — warna teks = ungu tua
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        formButtonPanel.setBackground(Color.WHITE);

        JButton btnSimpan = new JButton("💾 Simpan");
        btnSimpan.setFont(new Font("Poppins", Font.BOLD, 14));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(new Color(155, 89, 182)); // ✅ SESUAI HEADER
        btnSimpan.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> simpanLayanan());
        btnSimpan.setPreferredSize(new Dimension(100, 40));
        formButtonPanel.add(btnSimpan);

        JButton btnBatal = new JButton("❌ Batal");
        btnBatal.setFont(new Font("Poppins", Font.BOLD, 14));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(new Color(155, 89, 182)); // ✅ SESUAI HEADER
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

    private void loadDataLayanan() {
        try {
            modelTable.setRowCount(0);

            Connection conn = Koneksi.getKoneksi();
            StringBuilder sql = new StringBuilder(
                "SELECT id_layanan, nama_layanan, harga_layanan, durasi_estimasi, status " +
                "FROM tb_layanan WHERE 1=1"
            );

            String search = txtCari.getText().trim();
            if (!search.isEmpty()) {
                // Cari berdasarkan ID jika input angka
                if (search.matches("\\d+")) {
                    sql.append(" AND id_layanan = ").append(search);
                } else {
                    sql.append(" AND nama_layanan LIKE '%").append(search).append("%'");
                }
            }

            String status = (String) cmbFilterStatus.getSelectedItem();
            if (status != null && !status.equals("Semua")) {
                sql.append(" AND status = '").append(status).append("'");
            }

            sql.append(" ORDER BY id_layanan");

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_layanan"),
                    rs.getString("nama_layanan"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_layanan")),
                    rs.getInt("durasi_estimasi") + " menit",
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

    private void simpanLayanan() {
        if (txtNamaLayanan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama layanan harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNamaLayanan.requestFocus();
            return;
        }

        if (txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtHarga.requestFocus();
            return;
        }

        if (txtDurasi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Durasi harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtDurasi.requestFocus();
            return;
        }

        try {
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int durasi = Integer.parseInt(txtDurasi.getText().trim());

            if (harga <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (durasi <= 0) {
                JOptionPane.showMessageDialog(this, "Durasi harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection conn = Koneksi.getKoneksi();

            if (idLayananEdit == -1) {
                String sql = "INSERT INTO tb_layanan (nama_layanan, harga_layanan, durasi_estimasi, deskripsi, status) " +
                             "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtNamaLayanan.getText().trim());
                ps.setDouble(2, harga);
                ps.setInt(3, durasi);
                ps.setString(4, txtDeskripsi.getText().trim());
                ps.setString(5, (String) cmbStatus.getSelectedItem());

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Layanan berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String sql = "UPDATE tb_layanan SET nama_layanan=?, harga_layanan=?, durasi_estimasi=?, " +
                             "deskripsi=?, status=? WHERE id_layanan=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtNamaLayanan.getText().trim());
                ps.setDouble(2, harga);
                ps.setInt(3, durasi);
                ps.setString(4, txtDeskripsi.getText().trim());
                ps.setString(5, (String) cmbStatus.getSelectedItem());
                ps.setInt(6, idLayananEdit);

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Layanan berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            loadDataLayanan();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan Durasi harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error simpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editLayanan() {
        int row = tableLayanan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih layanan yang akan diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            idLayananEdit = (int) modelTable.getValueAt(row, 0);

            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM tb_layanan WHERE id_layanan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idLayananEdit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtNamaLayanan.setText(rs.getString("nama_layanan"));
                txtHarga.setText(String.valueOf(rs.getDouble("harga_layanan")));
                txtDurasi.setText(String.valueOf(rs.getInt("durasi_estimasi")));
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

    private void hapusLayanan() {
        int row = tableLayanan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih layanan yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus layanan ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idLayanan = (int) modelTable.getValueAt(row, 0);

                Connection conn = Koneksi.getKoneksi();
                String sql = "DELETE FROM tb_layanan WHERE id_layanan = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idLayanan);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "Layanan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDataLayanan();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        idLayananEdit = -1;
        txtNamaLayanan.setText("");
        txtHarga.setText("");
        txtDurasi.setText("");
        txtDeskripsi.setText("");
        cmbStatus.setSelectedIndex(0);
        txtNamaLayanan.requestFocus();
    }
}