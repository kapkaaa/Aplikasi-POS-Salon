package view.admin;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.text.*;

/**
 * Panel Kelola User - CRUD Admin & Kasir
 * Versi Responsif, Live Search, Tampilkan ID, Validasi Input, Tombol Lebih Besar
 */
public class KelolaUserPanel extends JPanel {

    private JTable tableUser;
    private DefaultTableModel modelTable;
    private JTextField txtCari;
    private JComboBox<String> cmbFilterRole;
    private JComboBox<String> cmbFilterStatus;

    // Form components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtKonfirmasiPassword;
    private JTextField txtNamaLengkap;
    private JTextArea txtAlamat;
    private JTextField txtNoTelepon;
    private JComboBox<String> cmbRole;
    private JComboBox<String> cmbStatus;
    private JCheckBox chkShowPassword;

    private int idUserEdit = -1;

    // === Dokumen khusus: hanya angka dan simbol telepon ===
    private static class PhoneDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) 
                throws BadLocationException {
            if (str == null) return;
            // Izinkan angka, +, -, (, ), spasi
            if (str.matches("[0-9+\\-() ]*")) {
                super.insertString(offs, str, a);
            }
        }
    }

    public KelolaUserPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataUser();
    }

    private void initComponents() {
        // Title
        JLabel lblTitle = new JLabel("👥 Kelola User");
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

        JLabel lblCari = new JLabel("🔍 Cari User:");
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
                loadDataUser();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadDataUser();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadDataUser();
            }
        });

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblRole.setForeground(new Color(60, 60, 60));
        headerPanel.add(lblRole);

        cmbFilterRole = new JComboBox<>(new String[]{"Semua", "Admin", "Kasir"});
        cmbFilterRole.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterRole.addActionListener(e -> loadDataUser());
        headerPanel.add(cmbFilterRole);

        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "Aktif", "Nonaktif"});
        cmbFilterStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        cmbFilterStatus.addActionListener(e -> loadDataUser());
        headerPanel.add(cmbFilterStatus);

        panelKiri.add(headerPanel, BorderLayout.NORTH);

        // Tabel User — TAMPILKAN ID
        String[] columns = {"ID", "Username", "Nama Lengkap", "Role", "No Telepon", "Status"};
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableUser = new JTable(modelTable);
        tableUser.setFont(new Font("Poppins", Font.PLAIN, 12));
        tableUser.setRowHeight(30);
        tableUser.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        tableUser.getTableHeader().setBackground(new Color(46, 204, 113)); // Hijau
        tableUser.getTableHeader().setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
        tableUser.setSelectionBackground(new Color(125, 206, 160));
        tableUser.setSelectionForeground(Color.BLACK);

        // Lebar kolom — ID ditampilkan
        tableUser.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        tableUser.getColumnModel().getColumn(1).setPreferredWidth(130); // Username
        tableUser.getColumnModel().getColumn(2).setPreferredWidth(200); // Nama
        tableUser.getColumnModel().getColumn(3).setPreferredWidth(80);  // Role
        tableUser.getColumnModel().getColumn(4).setPreferredWidth(130); // No Telepon
        tableUser.getColumnModel().getColumn(5).setPreferredWidth(100); // Status

        JScrollPane scrollTable = new JScrollPane(tableUser);
        panelKiri.add(scrollTable, BorderLayout.CENTER);

        // Buttons bawah tabel — lebih besar, warna teks = hijau tua
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("✏️ Edit");
        btnEdit.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
        btnEdit.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnEdit.setFocusPainted(false);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEdit.addActionListener(e -> editUser());
        btnEdit.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnEdit);

        JButton btnHapus = new JButton("🗑️ Hapus");
        btnHapus.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
        btnHapus.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnHapus.setFocusPainted(false);
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.addActionListener(e -> hapusUser());
        btnHapus.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(btnHapus);

        JButton btnResetPassword = new JButton("🔑 Reset Password");
        btnResetPassword.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnResetPassword.setBackground(new Color(230, 126, 34));
        btnResetPassword.setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
        btnResetPassword.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnResetPassword.setFocusPainted(false);
        btnResetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetPassword.addActionListener(e -> resetPassword());
        btnResetPassword.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(btnResetPassword);

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            clearForm();
            loadDataUser();
        });
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(btnRefresh);

        panelKiri.add(buttonPanel, BorderLayout.SOUTH);

        // ===== PANEL KANAN (Form Input) =====
        JPanel panelKanan = new JPanel();
        panelKanan.setLayout(new BorderLayout(10, 10));
        panelKanan.setBackground(Color.WHITE);
        panelKanan.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel lblFormTitle = new JLabel("Form User");
        lblFormTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(46, 204, 113));
        lblFormTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelKanan.add(lblFormTitle, BorderLayout.NORTH);

        // Form Content
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel lblUsername = new JLabel("Username *");
        lblUsername.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblUsername.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password *");
        lblPassword.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblPassword.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtPassword.setEchoChar('•');
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(txtPassword, gbc);

        // Konfirmasi Password
        JLabel lblKonfirmasi = new JLabel("Konfirmasi *");
        lblKonfirmasi.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblKonfirmasi.setForeground(new Color(60, 60, 60));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(lblKonfirmasi, gbc);

        txtKonfirmasiPassword = new JPasswordField();
        txtKonfirmasiPassword.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtKonfirmasiPassword.setEchoChar('•');
        txtKonfirmasiPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(txtKonfirmasiPassword, gbc);

        // Show Password
        chkShowPassword = new JCheckBox("Tampilkan Password");
        chkShowPassword.setFont(new Font("Poppins", Font.PLAIN, 11));
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.addActionListener(e -> {
            char echo = chkShowPassword.isSelected() ? (char) 0 : '•';
            txtPassword.setEchoChar(echo);
            txtKonfirmasiPassword.setEchoChar(echo);
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(chkShowPassword, gbc);

        // Nama Lengkap
        JLabel lblNama = new JLabel("Nama Lengkap *");
        lblNama.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblNama.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(lblNama, gbc);

        txtNamaLengkap = new JTextField();
        txtNamaLengkap.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtNamaLengkap.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(txtNamaLengkap, gbc);

        // Alamat
        JLabel lblAlamat = new JLabel("Alamat");
        lblAlamat.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblAlamat.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(lblAlamat, gbc);

        txtAlamat = new JTextArea(3, 20);
        txtAlamat.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollAlamat, gbc);

        // No Telepon & Role
        JLabel lblTelepon = new JLabel("No Telepon");
        lblTelepon.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblTelepon.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        formPanel.add(lblTelepon, gbc);

        txtNoTelepon = new JTextField();
        txtNoTelepon.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtNoTelepon.setDocument(new PhoneDocument()); // 🔒 HANYA ANGKA & SIMBOL TELEPON
        txtNoTelepon.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        formPanel.add(txtNoTelepon, gbc);

        JLabel lblRole2 = new JLabel("Role *");
        lblRole2.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblRole2.setForeground(new Color(60, 60, 60));
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        formPanel.add(lblRole2, gbc);

        cmbRole = new JComboBox<>(new String[]{"Admin", "Kasir"});
        cmbRole.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        formPanel.add(cmbRole, gbc);

        // Status
        JLabel lblStatus = new JLabel("Status *");
        lblStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        formPanel.add(lblStatus, gbc);

        cmbStatus = new JComboBox<>(new String[]{"Aktif", "Nonaktif"});
        cmbStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        formPanel.add(cmbStatus, gbc);

        panelKanan.add(formPanel, BorderLayout.CENTER);

        // Buttons bawah form — warna teks = hijau tua
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        formButtonPanel.setBackground(Color.WHITE);

        JButton btnSimpan = new JButton("💾 Simpan");
        btnSimpan.setFont(new Font("Poppins", Font.BOLD, 14));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
        btnSimpan.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> simpanUser());
        btnSimpan.setPreferredSize(new Dimension(100, 40));
        formButtonPanel.add(btnSimpan);

        JButton btnBatal = new JButton("❌ Batal");
        btnBatal.setFont(new Font("Poppins", Font.BOLD, 14));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(new Color(46, 204, 113)); // ✅ SESUAI HEADER
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

    private void loadDataUser() {
        try {
            modelTable.setRowCount(0);

            Connection conn = Koneksi.getKoneksi();
            StringBuilder sql = new StringBuilder(
                "SELECT id_user, username, nama_lengkap, role, no_telepon, status " +
                "FROM tb_user WHERE 1=1"
            );

            String search = txtCari.getText().trim();
            if (!search.isEmpty()) {
                if (search.matches("\\d+")) {
                    sql.append(" AND id_user = ").append(search);
                } else {
                    sql.append(" AND (username LIKE '%").append(search).append("%' ")
                       .append("OR nama_lengkap LIKE '%").append(search).append("%')");
                }
            }

            String role = (String) cmbFilterRole.getSelectedItem();
            if (role != null && !role.equals("Semua")) {
                sql.append(" AND role = '").append(role).append("'");
            }

            String status = (String) cmbFilterStatus.getSelectedItem();
            if (status != null && !status.equals("Semua")) {
                sql.append(" AND status = '").append(status).append("'");
            }

            sql.append(" ORDER BY id_user");

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("role"),
                    rs.getString("no_telepon"),
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

    private void simpanUser() {
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (idUserEdit == -1) {
            if (new String(txtPassword.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtPassword.requestFocus();
                return;
            }
            if (!new String(txtPassword.getPassword()).equals(new String(txtKonfirmasiPassword.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Password dan konfirmasi tidak sama!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtKonfirmasiPassword.requestFocus();
                return;
            }
        }

        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNamaLengkap.requestFocus();
            return;
        }

        try {
            Connection conn = Koneksi.getKoneksi();

            if (idUserEdit == -1) {
                String sqlCheck = "SELECT COUNT(*) as total FROM tb_user WHERE username = ?";
                PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                psCheck.setString(1, txtUsername.getText().trim());
                ResultSet rsCheck = psCheck.executeQuery();

                if (rsCheck.next() && rsCheck.getInt("total") > 0) {
                    JOptionPane.showMessageDialog(this, "Username sudah digunakan!", "Error", JOptionPane.ERROR_MESSAGE);
                    rsCheck.close();
                    psCheck.close();
                    return;
                }
                rsCheck.close();
                psCheck.close();
            }

            if (idUserEdit == -1) {
                String sql = "INSERT INTO tb_user (username, password, nama_lengkap, alamat, role, no_telepon, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtUsername.getText().trim());
                ps.setString(2, md5(new String(txtPassword.getPassword())));
                ps.setString(3, txtNamaLengkap.getText().trim());
                ps.setString(4, txtAlamat.getText().trim());
                ps.setString(5, (String) cmbRole.getSelectedItem());
                ps.setString(6, txtNoTelepon.getText().trim());
                ps.setString(7, (String) cmbStatus.getSelectedItem());

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String sql;
                PreparedStatement ps;

                String passwordStr = new String(txtPassword.getPassword());
                if (!passwordStr.isEmpty()) {
                    if (!passwordStr.equals(new String(txtKonfirmasiPassword.getPassword()))) {
                        JOptionPane.showMessageDialog(this, "Password dan konfirmasi tidak sama!", "Validasi", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    sql = "UPDATE tb_user SET username=?, password=?, nama_lengkap=?, alamat=?, " +
                          "role=?, no_telepon=?, status=? WHERE id_user=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, txtUsername.getText().trim());
                    ps.setString(2, md5(passwordStr));
                    ps.setString(3, txtNamaLengkap.getText().trim());
                    ps.setString(4, txtAlamat.getText().trim());
                    ps.setString(5, (String) cmbRole.getSelectedItem());
                    ps.setString(6, txtNoTelepon.getText().trim());
                    ps.setString(7, (String) cmbStatus.getSelectedItem());
                    ps.setInt(8, idUserEdit);
                } else {
                    sql = "UPDATE tb_user SET username=?, nama_lengkap=?, alamat=?, " +
                          "role=?, no_telepon=?, status=? WHERE id_user=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, txtUsername.getText().trim());
                    ps.setString(2, txtNamaLengkap.getText().trim());
                    ps.setString(3, txtAlamat.getText().trim());
                    ps.setString(4, (String) cmbRole.getSelectedItem());
                    ps.setString(5, txtNoTelepon.getText().trim());
                    ps.setString(6, (String) cmbStatus.getSelectedItem());
                    ps.setInt(7, idUserEdit);
                }

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "User berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            loadDataUser();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error simpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void editUser() {
        int row = tableUser.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            idUserEdit = (int) modelTable.getValueAt(row, 0);

            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM tb_user WHERE id_user = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUserEdit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                txtUsername.setText(rs.getString("username"));
                txtPassword.setText("");
                txtKonfirmasiPassword.setText("");
                txtNamaLengkap.setText(rs.getString("nama_lengkap"));
                txtAlamat.setText(rs.getString("alamat"));
                txtNoTelepon.setText(rs.getString("no_telepon"));
                cmbRole.setSelectedItem(rs.getString("role"));
                cmbStatus.setSelectedItem(rs.getString("status"));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hapusUser() {
        int row = tableUser.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus user ini?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idUser = (int) modelTable.getValueAt(row, 0);

                Connection conn = Koneksi.getKoneksi();
                String sql = "DELETE FROM tb_user WHERE id_user = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idUser);
                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(this, "User berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadDataUser();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void resetPassword() {
        int row = tableUser.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan direset passwordnya!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newPassword = JOptionPane.showInputDialog(this,
            "Masukkan password baru:",
            "Reset Password",
            JOptionPane.PLAIN_MESSAGE);

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin reset password user ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int idUser = (int) modelTable.getValueAt(row, 0);

                    Connection conn = Koneksi.getKoneksi();
                    String sql = "UPDATE tb_user SET password = ? WHERE id_user = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, md5(newPassword));
                    ps.setInt(2, idUser);
                    ps.executeUpdate();
                    ps.close();

                    JOptionPane.showMessageDialog(this, "Password berhasil direset!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error reset password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearForm() {
        idUserEdit = -1;
        txtUsername.setText("");
        txtPassword.setText("");
        txtKonfirmasiPassword.setText("");
        txtNamaLengkap.setText("");
        txtAlamat.setText("");
        txtNoTelepon.setText("");
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar('•');
        txtKonfirmasiPassword.setEchoChar('•');
        txtUsername.requestFocus();
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}