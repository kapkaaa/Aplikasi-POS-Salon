package view.admin;

import config.Koneksi;
import view.LoginPage;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Dashboard Admin - Halaman Utama Admin
 */
public class DashboardAdmin extends JFrame {
    
    private int idUser;
    private String namaUser;
    private JPanel contentPanel;
    private JLabel lblNamaUser;
    private JLabel lblWaktu;
    
    // Menu buttons
    private JButton btnDashboard;
    private JButton btnKelolaBarang;
    private JButton btnKelolaLayanan;
    private JButton btnKelolaUser;
    private JButton btnPendapatan;
    private JButton btnHistory;
    private JButton btnLogout;
    
    public DashboardAdmin(int idUser, String namaUser) {
        this.idUser = idUser;
        this.namaUser = namaUser;
        
        initComponents();
        setLocationRelativeTo(null);
        loadDashboardContent(); // Load default content
        startClock(); // Mulai jam
    }
    
    private void initComponents() {
        // Setup JFrame
        setTitle("Dashboard Admin - Salon Blue");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(new Color(70, 130, 180));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        
        // Logo & Title di Sidebar
        JLabel lblLogo = new JLabel("💈");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBounds(0, 20, 250, 50);
        sidebar.add(lblLogo);
        
        JLabel lblAppName = new JLabel("SALON BLUE");
        lblAppName.setFont(new Font("Poppins", Font.BOLD, 20));
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setHorizontalAlignment(SwingConstants.CENTER);
        lblAppName.setBounds(0, 75, 250, 30);
        sidebar.add(lblAppName);
        
        JLabel lblRole = new JLabel("Administrator");
        lblRole.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblRole.setForeground(new Color(200, 220, 240));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);
        lblRole.setBounds(0, 105, 250, 20);
        sidebar.add(lblRole);
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(100, 150, 200));
        separator.setBounds(25, 140, 200, 1);
        sidebar.add(separator);
        
        // Menu Buttons
        int yPos = 160;
        
        btnDashboard = createMenuButton("🏠  Dashboard", yPos);
        btnDashboard.setBackground(new Color(100, 149, 237)); // Active state
        btnDashboard.setForeground(new Color(115, 160, 228));
        sidebar.add(btnDashboard);
        yPos += 50;
        
        btnKelolaBarang = createMenuButton("📦  Kelola Barang", yPos);
        btnKelolaBarang.setForeground(new Color(115, 160, 228));
        sidebar.add(btnKelolaBarang);
        yPos += 50;
        
        btnKelolaLayanan = createMenuButton("✂️  Kelola Layanan", yPos);
        btnKelolaLayanan.setForeground(new Color(115, 160, 228));
        sidebar.add(btnKelolaLayanan);
        yPos += 50;
        
        btnKelolaUser = createMenuButton("👥  Kelola User", yPos);
        btnKelolaUser.setForeground(new Color(115, 160, 228));
        sidebar.add(btnKelolaUser);
        yPos += 50;
        
        btnPendapatan = createMenuButton("💰  Pendapatan", yPos);
        btnPendapatan.setForeground(new Color(115, 160, 228));
        sidebar.add(btnPendapatan);
        yPos += 50;
        
        btnHistory = createMenuButton("📊  History Penjualan", yPos);
        btnHistory.setForeground(new Color(115, 160, 228));
        sidebar.add(btnHistory);
        
        // Logout Button (di bawah) - posisi dinamis
        btnLogout = new JButton("🚪  Logout");
        btnLogout.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnLogout.setForeground(new Color(115, 160, 228));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setBounds(15, 0, 220, 45); // Y position akan di-set nanti
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(200, 35, 51));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(new Color(220, 53, 69));
            }
        });
        
        // Component listener untuk posisi logout button dinamis
        sidebar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                btnLogout.setBounds(15, sidebar.getHeight() - 65, 220, 45);
            }
        });
        sidebar.add(btnLogout);
        
        mainPanel.add(sidebar, BorderLayout.WEST);
        
        // ===== TOP BAR =====
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(getWidth(), 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        JPanel topBarContent = new JPanel();
        topBarContent.setLayout(null);
        topBarContent.setBackground(Color.WHITE);
        
        JLabel lblWelcome = new JLabel("Selamat Datang,");
        lblWelcome.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.GRAY);
        lblWelcome.setBounds(30, 15, 200, 20);
        topBarContent.add(lblWelcome);
        
        lblNamaUser = new JLabel(namaUser);
        lblNamaUser.setFont(new Font("Poppins", Font.BOLD, 18));
        lblNamaUser.setForeground(new Color(70, 130, 180));
        lblNamaUser.setBounds(30, 35, 300, 25);
        topBarContent.add(lblNamaUser);
        
        // Jam dan Tanggal
        lblWaktu = new JLabel();
        lblWaktu.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblWaktu.setForeground(Color.GRAY);
        lblWaktu.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Component listener untuk posisi waktu dinamis
        topBarContent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                lblWaktu.setBounds(topBarContent.getWidth() - 320, 25, 290, 25);
            }
        });
        topBarContent.add(lblWaktu);
        
        topBar.add(topBarContent, BorderLayout.CENTER);
        
        // ===== CONTENT PANEL =====
        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(new Color(240, 242, 245));
        
        // Panel wrapper untuk top bar dan content
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Setup Menu Actions
        setupMenuActions();
    }
    
    private JButton createMenuButton(String text, int yPos) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.PLAIN, 14));
        btn.setForeground(new Color(240, 248, 255)); // Alice Blue - warna lebih terang dan bagus
        btn.setBackground(new Color(70, 130, 180));
        btn.setBounds(15, yPos, 220, 45);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(new Color(100, 149, 237))) {
                    btn.setBackground(new Color(90, 140, 190));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(new Color(100, 149, 237))) {
                    btn.setBackground(new Color(70, 130, 180));
                }
            }
        });
        
        return btn;
    }
    
    private void setupMenuActions() {
        // Dashboard
        btnDashboard.addActionListener(e -> {
            setActiveMenu(btnDashboard);
            loadDashboardContent();
        });
        
        // Kelola Barang
        btnKelolaBarang.addActionListener(e -> {
            setActiveMenu(btnKelolaBarang);
            loadKelolaBarangContent();
        });
        
        // Kelola Layanan
        btnKelolaLayanan.addActionListener(e -> {
            setActiveMenu(btnKelolaLayanan);
            loadKelolaLayananContent();
        });
        
        // Kelola User
        btnKelolaUser.addActionListener(e -> {
            setActiveMenu(btnKelolaUser);
            loadKelolaUserContent();
        });
        
        // Pendapatan
        btnPendapatan.addActionListener(e -> {
            setActiveMenu(btnPendapatan);
            loadPendapatanContent();
        });
        
        // History
        btnHistory.addActionListener(e -> {
            setActiveMenu(btnHistory);
            loadHistoryContent();
        });
        
        // Logout
        btnLogout.addActionListener(e -> handleLogout());
    }
    
    private void setActiveMenu(JButton activeBtn) {
        // Reset semua button
        JButton[] buttons = {btnDashboard, btnKelolaBarang, btnKelolaLayanan, 
                             btnKelolaUser, btnPendapatan, btnHistory};
        
        for (JButton btn : buttons) {
            btn.setBackground(new Color(70, 130, 180));
        }
        
        // Set active button
        activeBtn.setBackground(new Color(100, 149, 237));
    }
    
    private void loadDashboardContent() {
        contentPanel.removeAll();
        
        // Title
        JLabel lblTitle = new JLabel("Dashboard Overview");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBounds(30, 20, 400, 35);
        contentPanel.add(lblTitle);
        
        // Statistik Cards
        try {
            Connection conn = Koneksi.getKoneksi();
            
            // Total Barang
            String sqlBarang = "SELECT COUNT(*) as total FROM tb_barang";
            Statement stBarang = conn.createStatement();
            ResultSet rsBarang = stBarang.executeQuery(sqlBarang);
            int totalBarang = 0;
            if (rsBarang.next()) totalBarang = rsBarang.getInt("total");
            
            // Total Layanan
            String sqlLayanan = "SELECT COUNT(*) as total FROM tb_layanan";
            Statement stLayanan = conn.createStatement();
            ResultSet rsLayanan = stLayanan.executeQuery(sqlLayanan);
            int totalLayanan = 0;
            if (rsLayanan.next()) totalLayanan = rsLayanan.getInt("total");
            
            // Total User
            String sqlUser = "SELECT COUNT(*) as total FROM tb_user WHERE status='Aktif'";
            Statement stUser = conn.createStatement();
            ResultSet rsUser = stUser.executeQuery(sqlUser);
            int totalUser = 0;
            if (rsUser.next()) totalUser = rsUser.getInt("total");
            
            // Total Transaksi Hari Ini
            String sqlTransaksi = "SELECT COUNT(*) as total FROM tb_transaksi WHERE DATE(tgl_transaksi) = CURDATE()";
            Statement stTransaksi = conn.createStatement();
            ResultSet rsTransaksi = stTransaksi.executeQuery(sqlTransaksi);
            int totalTransaksi = 0;
            if (rsTransaksi.next()) totalTransaksi = rsTransaksi.getInt("total");
            
            // Pendapatan Hari Ini
            String sqlPendapatan = "SELECT COALESCE(SUM(total_harga), 0) as total FROM tb_transaksi WHERE DATE(tgl_transaksi) = CURDATE()";
            Statement stPendapatan = conn.createStatement();
            ResultSet rsPendapatan = stPendapatan.executeQuery(sqlPendapatan);
            double pendapatanHariIni = 0;
            if (rsPendapatan.next()) pendapatanHariIni = rsPendapatan.getDouble("total");
            
            // Stok Menipis
            String sqlStokMenipis = "SELECT COUNT(*) as total FROM tb_barang WHERE stok < 10 AND stok > 0";
            Statement stStokMenipis = conn.createStatement();
            ResultSet rsStokMenipis = stStokMenipis.executeQuery(sqlStokMenipis);
            int stokMenipis = 0;
            if (rsStokMenipis.next()) stokMenipis = rsStokMenipis.getInt("total");
            
            // Create Cards
            createStatCard("📦 Total Barang", String.valueOf(totalBarang), new Color(52, 152, 219), 30, 80);
            createStatCard("✂️ Total Layanan", String.valueOf(totalLayanan), new Color(155, 89, 182), 260, 80);
            createStatCard("👥 Total User", String.valueOf(totalUser), new Color(46, 204, 113), 490, 80);
            createStatCard("📊 Transaksi Hari Ini", String.valueOf(totalTransaksi), new Color(241, 196, 15), 720, 80);
            createStatCard("💰 Pendapatan Hari Ini", "Rp " + String.format("%,.0f", pendapatanHariIni), new Color(231, 76, 60), 30, 220);
            createStatCard("⚠️ Stok Menipis", String.valueOf(stokMenipis) + " Item", new Color(230, 126, 34), 260, 220);
            
            rsBarang.close();
            rsLayanan.close();
            rsUser.close();
            rsTransaksi.close();
            rsPendapatan.close();
            rsStokMenipis.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(null);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBounds(30, 380, 890, 220);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JLabel lblInfo = new JLabel("📌 Informasi");
        lblInfo.setFont(new Font("Poppins", Font.BOLD, 16));
        lblInfo.setBounds(20, 15, 200, 25);
        infoPanel.add(lblInfo);
        
        JTextArea txtInfo = new JTextArea();
        txtInfo.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setEditable(false);
        txtInfo.setBackground(Color.WHITE);
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtInfo.setText(
            "Selamat datang di Dashboard Admin Salon Blue!\n\n" +
            "Menu yang tersedia:\n" +
            "• Kelola Barang - Manajemen produk dan stok barang\n" +
            "• Kelola Layanan - Manajemen layanan salon\n" +
            "• Kelola User - Manajemen admin dan kasir\n" +
            "• Pendapatan - Laporan pendapatan berdasarkan metode pembayaran\n" +
            "• History Penjualan - Riwayat semua transaksi\n\n" +
            "Pastikan untuk selalu memantau stok barang yang menipis!"
        );
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBounds(20, 50, 850, 150);
        scrollInfo.setBorder(null);
        infoPanel.add(scrollInfo);
        
        contentPanel.add(infoPanel);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void createStatCard(String title, String value, Color color, int x, int y) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBounds(x, y, 210, 120);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Color indicator
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setBounds(0, 0, 5, 120);
        card.add(colorBar);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBounds(20, 15, 170, 20);
        card.add(lblTitle);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Poppins", Font.BOLD, 24));
        lblValue.setForeground(color);
        lblValue.setBounds(20, 45, 170, 35);
        card.add(lblValue);
        
        contentPanel.add(card);
    }
    
    private void loadKelolaBarangContent() {
        contentPanel.removeAll();
        
        KelolaBarangPanel panelBarang = new KelolaBarangPanel();
        panelBarang.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelBarang);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadKelolaLayananContent() {
        contentPanel.removeAll();
        
        KelolaLayananPanel panelLayanan = new KelolaLayananPanel();
        panelLayanan.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelLayanan);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadKelolaUserContent() {
        contentPanel.removeAll();
        
        KelolaUserPanel panelUser = new KelolaUserPanel();
        panelUser.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelUser);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadPendapatanContent() {
        contentPanel.removeAll();
        
        PendapatanPanel panelPendapatan = new PendapatanPanel();
        panelPendapatan.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelPendapatan);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadHistoryContent() {
        contentPanel.removeAll();
        
        HistoryPanel panelHistory = new HistoryPanel();
        panelHistory.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelHistory);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage().setVisible(true);
        }
    }
    
    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm:ss");
            lblWaktu.setText(sdf.format(new java.util.Date()));
        });
        timer.start();
    }
}