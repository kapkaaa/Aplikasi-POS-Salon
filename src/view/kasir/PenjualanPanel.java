package view.kasir;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

/**
 * Panel Penjualan - Versi Final + Fitur Edit Qty via Dialog Input
 */
public class PenjualanPanel extends JPanel {

    private int idKasir;
    private String namaKasir;

    private JComboBox<String> cmbJenisTransaksi;
    private JComboBox<String> cmbItem;
    private JTextField txtQuantity;
    private JTextField txtHarga;
    private JComboBox<String> cmbMetodePembayaran;
    private JTextField txtUangDibayar;
    private JLabel lblKembalian;

    private JTable tableKeranjang;
    private DefaultTableModel modelKeranjang;
    private JLabel lblTotalHarga;

    private ArrayList<ItemKeranjang> keranjang = new ArrayList<>();

    public PenjualanPanel(int idKasir, String namaKasir) {
        this.idKasir = idKasir;
        this.namaKasir = namaKasir;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // === HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("🛒 Form Penjualan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(52, 73, 94));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // === MAIN CONTENT: 2 Kolom ===
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // === LEFT PANEL - Input Transaksi ===
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel lblInputTitle = new JLabel("Input Transaksi");
        lblInputTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInputTitle.setForeground(new Color(52, 152, 219));
        leftPanel.add(lblInputTitle, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);

        leftPanel.add(createLabel("Jenis Transaksi *"), gbc);
        gbc.gridy++;
        cmbJenisTransaksi = createComboBox(new String[]{"- Pilih -", "Barang", "Layanan"});
        cmbJenisTransaksi.addActionListener(e -> loadItems());
        leftPanel.add(cmbJenisTransaksi, gbc);
        gbc.gridy++;

        leftPanel.add(createLabel("Pilih Item *"), gbc);
        gbc.gridy++;
        cmbItem = createComboBox(new String[]{});
        cmbItem.addActionListener(e -> loadHarga());
        leftPanel.add(cmbItem, gbc);
        gbc.gridy++;

        JPanel qtyHargaPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        qtyHargaPanel.setOpaque(false);

        JPanel qtyPanel = new JPanel(new GridBagLayout());
        qtyPanel.setOpaque(false);
        GridBagConstraints gbcInner = new GridBagConstraints();
        gbcInner.gridx = 0; gbcInner.gridy = 0; gbcInner.anchor = GridBagConstraints.WEST;
        qtyPanel.add(createLabel("Jumlah *"), gbcInner);
        gbcInner.gridy++;
        txtQuantity = createTextField();
        txtQuantity.setText("1");
        qtyPanel.add(txtQuantity, gbcInner);

        JPanel hargaPanel = new JPanel(new GridBagLayout());
        hargaPanel.setOpaque(false);
        gbcInner.gridy = 0;
        hargaPanel.add(createLabel("Harga Satuan *"), gbcInner);
        gbcInner.gridy++;
        txtHarga = createTextField();
        txtHarga.setEditable(false);
        txtHarga.setBackground(new Color(245, 245, 245));
        hargaPanel.add(txtHarga, gbcInner);

        qtyHargaPanel.add(qtyPanel);
        qtyHargaPanel.add(hargaPanel);
        leftPanel.add(qtyHargaPanel, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(15, 0, 15, 0);
        JButton btnTambah = new JButton("➕ Tambah ke Keranjang");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setBackground(new Color(46, 204, 113));
        btnTambah.setForeground(new Color(46, 204, 113));
        btnTambah.setFocusPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.addActionListener(e -> tambahKeKeranjang());
        leftPanel.add(btnTambah, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);

        JSeparator sep1 = new JSeparator();
        leftPanel.add(sep1, gbc);
        gbc.gridy++;

        leftPanel.add(createLabel("Metode Pembayaran *"), gbc);
        gbc.gridy++;
        cmbMetodePembayaran = createComboBox(new String[]{"Cash", "QRIS"});
        cmbMetodePembayaran.addActionListener(e -> {
            boolean isCash = "Cash".equals(cmbMetodePembayaran.getSelectedItem());
            txtUangDibayar.setEnabled(isCash);
            txtUangDibayar.setBackground(isCash ? Color.WHITE : new Color(245, 245, 245));
            lblKembalian.setText("Rp 0");
            if (!isCash) txtUangDibayar.setText("");
        });
        leftPanel.add(cmbMetodePembayaran, gbc);
        gbc.gridy++;

        leftPanel.add(createLabel("Uang Dibayar"), gbc);
        gbc.gridy++;
        txtUangDibayar = createTextField();
        txtUangDibayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungKembalian();
            }
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '\b') e.consume();
            }
        });
        leftPanel.add(txtUangDibayar, gbc);
        gbc.gridy++;

        leftPanel.add(createLabel("Kembalian"), gbc);
        gbc.gridy++;
        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembalian.setForeground(new Color(46, 204, 113));
        leftPanel.add(lblKembalian, gbc);
        gbc.gridy++;

        gbc.insets = new Insets(15, 0, 0, 0);
        JButton btnProses = new JButton("💾 Proses Transaksi");
        btnProses.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnProses.setBackground(new Color(52, 152, 219));
        btnProses.setForeground(new Color(52, 152, 219));
        btnProses.setFocusPainted(false);
        btnProses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProses.addActionListener(e -> prosesTransaksi());
        leftPanel.add(btnProses, gbc);

        mainContent.add(leftPanel);

        // === RIGHT PANEL - Keranjang Belanja ===
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.fill = GridBagConstraints.HORIZONTAL;
        gbcRight.weightx = 1.0;
        gbcRight.insets = new Insets(0, 0, 10, 0);

        JLabel lblKeranjangTitle = new JLabel("Keranjang Belanja");
        lblKeranjangTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKeranjangTitle.setForeground(new Color(52, 152, 219));
        rightPanel.add(lblKeranjangTitle, gbcRight);
        gbcRight.gridy++;
        gbcRight.insets = new Insets(5, 0, 5, 0);

        String[] columns = {"Nama Item", "Qty", "Harga", "Subtotal"};
        modelKeranjang = new DefaultTableModel(columns, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        tableKeranjang = new JTable(modelKeranjang);
        tableKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableKeranjang.setRowHeight(30);
        tableKeranjang.setShowGrid(true);
        tableKeranjang.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = tableKeranjang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));

        // Double-click untuk edit qty
        tableKeranjang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editQty();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tableKeranjang);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollTable.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));
        gbcRight.fill = GridBagConstraints.BOTH;
        gbcRight.weighty = 1.0;
        rightPanel.add(scrollTable, gbcRight);
        gbcRight.gridy++;
        gbcRight.weighty = 0.0;
        gbcRight.insets = new Insets(10, 0, 10, 0);

        // Tombol di bawah tabel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton btnHapus = new JButton("🗑️ Hapus Item");
        btnHapus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(new Color(231, 76, 60));
        btnHapus.setFocusPainted(false);
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.addActionListener(e -> hapusItem());

        JButton btnEditQty = new JButton("✏️ Edit Quantity");
        btnEditQty.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnEditQty.setBackground(new Color(52, 152, 219));
        btnEditQty.setForeground(new Color(52, 152, 219));
        btnEditQty.setFocusPainted(false);
        btnEditQty.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditQty.addActionListener(e -> editQty());

        JButton btnClear = new JButton("🔄 Clear");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnClear.setBackground(new Color(149, 165, 166));
        btnClear.setForeground(new Color(149, 165, 166));
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> clearKeranjang());

        btnPanel.add(btnHapus);
        btnPanel.add(btnEditQty);
        btnPanel.add(btnClear);
        rightPanel.add(btnPanel, gbcRight);
        gbcRight.gridy++;
        gbcRight.insets = new Insets(10, 0, 10, 0);

        JSeparator sep2 = new JSeparator();
        rightPanel.add(sep2, gbcRight);
        gbcRight.gridy++;

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(52, 73, 94));
        
        lblTotalHarga = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalHarga.setForeground(new Color(52, 152, 219));
        
        totalPanel.add(lblTotal, BorderLayout.WEST);
        totalPanel.add(lblTotalHarga, BorderLayout.EAST);
        rightPanel.add(totalPanel, gbcRight);

        mainContent.add(rightPanel);
        add(mainContent, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(52, 73, 94));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        return txt;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }

    private void loadItems() {
        String jenis = (String) cmbJenisTransaksi.getSelectedItem();
        cmbItem.removeAllItems();
        txtHarga.setText("");
        
        if (jenis.equals("- Pilih -")) return;
        
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = jenis.equals("Barang") 
                ? "SELECT id_barang, nama_barang, stok FROM tb_barang WHERE status = 'Tersedia' AND stok > 0 ORDER BY nama_barang"
                : "SELECT id_layanan, nama_layanan FROM tb_layanan WHERE status = 'Tersedia' ORDER BY nama_layanan";
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            cmbItem.addItem("- Pilih " + jenis + " -");
            
            while (rs.next()) {
                if (jenis.equals("Barang")) {
                    cmbItem.addItem(rs.getString("nama_barang") + " (Stok: " + rs.getInt("stok") + ")");
                } else {
                    cmbItem.addItem(rs.getString("nama_layanan"));
                }
            }
            
            rs.close(); st.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load items: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadHarga() {
        String selectedItem = (String) cmbItem.getSelectedItem();
        if (selectedItem == null || selectedItem.startsWith("- Pilih")) {
            txtHarga.setText("");
            return;
        }
        
        String jenis = (String) cmbJenisTransaksi.getSelectedItem();
        String namaItem = selectedItem.split(" \\(")[0];
        
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = jenis.equals("Barang")
                ? "SELECT harga_jual, harga_beli FROM tb_barang WHERE nama_barang = ?"
                : "SELECT harga_layanan FROM tb_layanan WHERE nama_layanan = ?";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, namaItem);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double harga = jenis.equals("Barang") ? rs.getDouble("harga_jual") : rs.getDouble("harga_layanan");
                txtHarga.setText("Rp " + String.format("%,.0f", harga));
            }
            
            rs.close(); ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load harga: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void tambahKeKeranjang() {
        String jenis = (String) cmbJenisTransaksi.getSelectedItem();
        if ("- Pilih -".equals(jenis)) {
            JOptionPane.showMessageDialog(this, "Pilih jenis transaksi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedItem = (String) cmbItem.getSelectedItem();
        if (selectedItem == null || selectedItem.startsWith("- Pilih")) {
            JOptionPane.showMessageDialog(this, "Pilih item!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String namaItem = selectedItem.split(" \\(")[0];
        
        try {
            Connection conn = Koneksi.getKoneksi();
            int idItem = 0;
            double harga = 0;
            double hargaBeli = 0;
            int stokTersedia = 0;
            
            if ("Barang".equals(jenis)) {
                String sql = "SELECT id_barang, harga_jual, harga_beli, stok FROM tb_barang WHERE nama_barang = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, namaItem);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    idItem = rs.getInt("id_barang");
                    harga = rs.getDouble("harga_jual");
                    hargaBeli = rs.getDouble("harga_beli");
                    stokTersedia = rs.getInt("stok");
                    
                    if (quantity > stokTersedia) {
                        JOptionPane.showMessageDialog(this, 
                            "Stok tidak mencukupi! Stok tersedia: " + stokTersedia, 
                            "Validasi", 
                            JOptionPane.WARNING_MESSAGE);
                        rs.close(); ps.close();
                        return;
                    }
                }
                rs.close(); ps.close();
            } else {
                String sql = "SELECT id_layanan, harga_layanan FROM tb_layanan WHERE nama_layanan = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, namaItem);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    idItem = rs.getInt("id_layanan");
                    harga = rs.getDouble("harga_layanan");
                }
                rs.close(); ps.close();
            }
            
            ItemKeranjang item = new ItemKeranjang();
            item.jenis = jenis;
            item.idItem = idItem;
            item.namaItem = namaItem;
            item.quantity = quantity;
            item.harga = harga;
            item.hargaBeli = hargaBeli;
            item.subtotal = quantity * harga;
            
            keranjang.add(item);
            updateTableKeranjang();
            
            txtQuantity.setText("1");
            cmbItem.setSelectedIndex(0);
            txtHarga.setText("");
            
            JOptionPane.showMessageDialog(this, "Item berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateTableKeranjang() {
        modelKeranjang.setRowCount(0);
        double total = 0;
        
        for (ItemKeranjang item : keranjang) {
            Object[] row = {
                item.namaItem,
                item.quantity,
                "Rp " + String.format("%,.0f", item.harga),
                "Rp " + String.format("%,.0f", item.subtotal)
            };
            modelKeranjang.addRow(row);
            total += item.subtotal;
        }
        
        lblTotalHarga.setText("Rp " + String.format("%,.0f", total));
        hitungKembalian();
    }
    
    private void hitungKembalian() {
        try {
            double total = keranjang.stream().mapToDouble(i -> i.subtotal).sum();
            String bayarStr = txtUangDibayar.getText().trim();
            if (bayarStr.isEmpty()) {
                lblKembalian.setText("Rp 0");
                lblKembalian.setForeground(new Color(46, 204, 113));
                return;
            }

            double bayar = Double.parseDouble(bayarStr);
            double kembalian = bayar - total;

            if (kembalian < 0) {
                lblKembalian.setForeground(Color.RED);
                lblKembalian.setText("Kurang Rp " + String.format("%,.0f", Math.abs(kembalian)));
            } else {
                lblKembalian.setForeground(new Color(46, 204, 113));
                lblKembalian.setText("Rp " + String.format("%,.0f", kembalian));
            }
        } catch (NumberFormatException ex) {
            lblKembalian.setText("Input tidak valid");
            lblKembalian.setForeground(Color.RED);
        }
    }
    
    private void hapusItem() {
        int row = tableKeranjang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        keranjang.remove(row);
        updateTableKeranjang();
    }
    
    private void editQty() {
        int row = tableKeranjang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan diubah jumlahnya!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ItemKeranjang item = keranjang.get(row);
        
        String input = JOptionPane.showInputDialog(
            this,
            "Ubah jumlah untuk '" + item.namaItem + "':",
            item.quantity
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int newQty;
        try {
            newQty = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newQty < 0) {
            JOptionPane.showMessageDialog(this, "Jumlah tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newQty == 0) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Jumlah diubah menjadi 0. Hapus item dari keranjang?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                keranjang.remove(row);
            } else {
                return;
            }
        } else {
            if ("Barang".equals(item.jenis)) {
                try {
                    Connection conn = Koneksi.getKoneksi();
                    String sql = "SELECT stok FROM tb_barang WHERE id_barang = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, item.idItem);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int stokTersedia = rs.getInt("stok");
                        if (newQty > stokTersedia) {
                            JOptionPane.showMessageDialog(this,
                                "Stok tidak mencukupi!\nStok tersedia: " + stokTersedia,
                                "Validasi",
                                JOptionPane.WARNING_MESSAGE);
                            rs.close();
                            ps.close();
                            return;
                        }
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error cek stok: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
            item.quantity = newQty;
            item.subtotal = newQty * item.harga;
        }

        updateTableKeranjang();
        hitungKembalian();
        
        if (newQty > 0) {
            JOptionPane.showMessageDialog(this, "Jumlah berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearKeranjang() {
        if (keranjang.isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Hapus semua item di keranjang?", 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            keranjang.clear();
            updateTableKeranjang();
        }
    }
    
    private void prosesTransaksi() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String metodePembayaran = (String) cmbMetodePembayaran.getSelectedItem();
        double totalHarga = keranjang.stream().mapToDouble(i -> i.subtotal).sum();
        
        if ("Cash".equals(metodePembayaran)) {
            String bayarStr = txtUangDibayar.getText().trim();
            if (bayarStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan uang dibayarkan!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtUangDibayar.requestFocus();
                return;
            }
            
            try {
                double bayar = Double.parseDouble(bayarStr);
                if (bayar < totalHarga) {
                    JOptionPane.showMessageDialog(this, "Uang dibayarkan kurang!\nTotal: Rp " + 
                        String.format("%,.0f", totalHarga) + "\nDibayar: Rp " + 
                        String.format("%,.0f", bayar), "Validasi", JOptionPane.WARNING_MESSAGE);
                    txtUangDibayar.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah uang tidak valid!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtUangDibayar.requestFocus();
                return;
            }
        }
        
        boolean adaBarang = keranjang.stream().anyMatch(i -> "Barang".equals(i.jenis));
        boolean adaLayanan = keranjang.stream().anyMatch(i -> "Layanan".equals(i.jenis));
        String jenisTransaksi = (adaBarang && adaLayanan) ? "Keduanya" : (adaBarang ? "Barang" : "Layanan");
        
        try {
            Connection conn = Koneksi.getKoneksi();
            conn.setAutoCommit(false);
            
            String noTransaksi = generateNoTransaksi(conn);
            
            String sqlHeader = "INSERT INTO tb_transaksi (no_transaksi, id_kasir, jenis_transaksi, metode_pembayaran, total_harga) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psHeader = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            psHeader.setString(1, noTransaksi);
            psHeader.setInt(2, idKasir);
            psHeader.setString(3, jenisTransaksi);
            psHeader.setString(4, metodePembayaran);
            psHeader.setDouble(5, totalHarga);
            psHeader.executeUpdate();
            
            ResultSet rsKey = psHeader.getGeneratedKeys();
            int idTransaksi = rsKey.next() ? rsKey.getInt(1) : 0;
            rsKey.close(); psHeader.close();
            
            for (ItemKeranjang item : keranjang) {
                if ("Barang".equals(item.jenis)) {
                    String sqlDetail = "INSERT INTO tb_detail_transaksi_barang (id_transaksi, id_barang, quantity, harga_satuan, harga_beli, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                    psDetail.setInt(1, idTransaksi);
                    psDetail.setInt(2, item.idItem);
                    psDetail.setInt(3, item.quantity);
                    psDetail.setDouble(4, item.harga);
                    psDetail.setDouble(5, item.hargaBeli);
                    psDetail.setDouble(6, item.subtotal);
                    psDetail.executeUpdate();
                    psDetail.close();
                } else {
                    String sqlDetail = "INSERT INTO tb_detail_transaksi_layanan (id_transaksi, id_layanan, quantity, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                    psDetail.setInt(1, idTransaksi);
                    psDetail.setInt(2, item.idItem);
                    psDetail.setInt(3, item.quantity);
                    psDetail.setDouble(4, item.harga);
                    psDetail.setDouble(5, item.subtotal);
                    psDetail.executeUpdate();
                    psDetail.close();
                }
            }
            
            conn.commit();
            conn.setAutoCommit(true);
            
            JOptionPane.showMessageDialog(this, 
                "Transaksi berhasil!\nNo Transaksi: " + noTransaksi, 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            
            tampilkanStruk(idTransaksi);
            resetForm();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            try {
                Koneksi.getKoneksi().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private String generateNoTransaksi(Connection conn) throws SQLException {
        String tglNow = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String sql = "SELECT COUNT(*) + 1 as counter FROM tb_transaksi WHERE DATE(tgl_transaksi) = CURDATE()";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int counter = rs.next() ? rs.getInt("counter") : 1;
        rs.close(); st.close();
        return "TRX-" + tglNow + "-" + String.format("%04d", counter);
    }
    
    private void tampilkanStruk(int idTransaksi) {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM tb_transaksi WHERE id_transaksi = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                StrukDialog struk = new StrukDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    rs.getString("no_transaksi"),
                    rs.getTimestamp("tgl_transaksi"),
                    rs.getString("metode_pembayaran"),
                    rs.getDouble("total_harga"),
                    namaKasir,
                    keranjang
                );
                struk.setVisible(true);
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            e.printStackTrace();    
        }
    }
    
    private void resetForm() {
        cmbJenisTransaksi.setSelectedIndex(0);
        cmbItem.removeAllItems();
        txtQuantity.setText("1");
        txtHarga.setText("");
        cmbMetodePembayaran.setSelectedIndex(0);
        txtUangDibayar.setText("");
        lblKembalian.setText("Rp 0");
        lblKembalian.setForeground(new Color(46, 204, 113));
        keranjang.clear();
        updateTableKeranjang();
    }

    // Inner class
    class ItemKeranjang {
        String jenis;
        int idItem;
        String namaItem;
        int quantity;
        double harga;
        double hargaBeli;
        double subtotal;
    }
}