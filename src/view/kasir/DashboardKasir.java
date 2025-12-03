package view.kasir;

import config.Koneksi;
import view.LoginPage;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * Dashboard Kasir - Halaman Utama Kasir
 */
public class DashboardKasir extends JFrame {
    
    private int idUser;
    private String namaUser;
    private JPanel contentPanel;
    private JLabel lblNamaUser;
    private JLabel lblWaktu;
    
    // Menu buttons
    private JButton btnDashboard;
    private JButton btnPenjualan;
    private JButton btnHistory;
    private JButton btnLaporan;
    private JButton btnLogout;
    
    public DashboardKasir(int idUser, String namaUser) {
        this.idUser = idUser;
        this.namaUser = namaUser;
        
        initComponents();
        setLocationRelativeTo(null);
        loadDashboardContent();
        startClock();
    }
    
    private void initComponents() {
        setTitle("Dashboard Kasir - Salon Blue");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(new Color(52, 152, 219));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        
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
        
        JLabel lblRole = new JLabel("Kasir");
        lblRole.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblRole.setForeground(new Color(200, 230, 255));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);
        lblRole.setBounds(0, 105, 250, 20);
        sidebar.add(lblRole);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(100, 180, 230));
        separator.setBounds(25, 140, 200, 1);
        sidebar.add(separator);
        
        
        int yPos = 160;
        
        btnDashboard = createMenuButton("🏠  Dashboard", yPos);
        btnDashboard.setBackground(new Color(41, 128, 185));
        btnDashboard.setForeground(new Color(115, 160, 228));
        sidebar.add(btnDashboard);
        yPos += 50;
        
        btnPenjualan = createMenuButton("🛒  Penjualan", yPos);
        btnPenjualan.setForeground(new Color(115, 160, 228));
        sidebar.add(btnPenjualan);
        yPos += 50;
        
        btnHistory = createMenuButton("📋  History Transaksi", yPos);
        btnHistory.setForeground(new Color(115, 160, 228));
        sidebar.add(btnHistory);
        yPos += 50;
        
        btnLaporan = createMenuButton("📊  Laporan", yPos);
        btnLaporan.setForeground(new Color(115, 160, 228));
        sidebar.add(btnLaporan);
        
        btnLogout = new JButton("🚪  Logout");
        btnLogout.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnLogout.setForeground(new Color(115, 160, 228));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setBounds(15, 0, 220, 45);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(192, 57, 43));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(new Color(231, 76, 60));
            }
        });
        
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
        lblNamaUser.setForeground(new Color(52, 152, 219));
        lblNamaUser.setBounds(30, 35, 300, 25);
        topBarContent.add(lblNamaUser);
        
        lblWaktu = new JLabel();
        lblWaktu.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblWaktu.setForeground(Color.GRAY);
        lblWaktu.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topBarContent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                lblWaktu.setBounds(topBarContent.getWidth() - 320, 25, 290, 25);
            }
        });
        topBarContent.add(lblWaktu);
        
        topBar.add(topBarContent, BorderLayout.CENTER);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(new Color(240, 242, 245));
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        setupMenuActions();
    }
    
    private JButton createMenuButton(String text, int yPos) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.PLAIN, 14));
        btn.setForeground(new Color(240, 248, 255));
        btn.setBackground(new Color(52, 152, 219));
        btn.setBounds(15, yPos, 220, 45);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(new Color(41, 128, 185))) {
                    btn.setBackground(new Color(70, 170, 230));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(new Color(41, 128, 185))) {
                    btn.setBackground(new Color(52, 152, 219));
                }
            }
        });
        
        return btn;
    }
    
    private void setupMenuActions() {
        btnDashboard.addActionListener(e -> {
            setActiveMenu(btnDashboard);
            loadDashboardContent();
        });
        
        btnPenjualan.addActionListener(e -> {
            setActiveMenu(btnPenjualan);
            loadPenjualanContent();
        });
        
        btnHistory.addActionListener(e -> {
            setActiveMenu(btnHistory);
            loadHistoryContent();
        });
        
        btnLaporan.addActionListener(e -> {
            setActiveMenu(btnLaporan);
            loadLaporanContent();
        });
        
        btnLogout.addActionListener(e -> handleLogout());
    }
    
    private void setActiveMenu(JButton activeBtn) {
        JButton[] buttons = {btnDashboard, btnPenjualan, btnHistory, btnLaporan};
        
        for (JButton btn : buttons) {
            btn.setBackground(new Color(52, 152, 219));
        }
        
        activeBtn.setBackground(new Color(41, 128, 185));
    }
    
    private void loadDashboardContent() {
        contentPanel.removeAll();
        
        JLabel lblTitle = new JLabel("Dashboard Kasir");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 50));
        lblTitle.setBounds(30, 20, 400, 35);
        contentPanel.add(lblTitle);
        
        try {
            Connection conn = Koneksi.getKoneksi();
            
            // Transaksi Hari Ini
            String sqlTransaksi = "SELECT COUNT(*) as total FROM tb_transaksi WHERE id_kasir = ? AND DATE(tgl_transaksi) = CURDATE()";
            PreparedStatement psTransaksi = conn.prepareStatement(sqlTransaksi);
            psTransaksi.setInt(1, idUser);
            ResultSet rsTransaksi = psTransaksi.executeQuery();
            int totalTransaksi = 0;
            if (rsTransaksi.next()) totalTransaksi = rsTransaksi.getInt("total");
            
            // Pendapatan Hari Ini
            String sqlPendapatan = "SELECT COALESCE(SUM(total_harga), 0) as total FROM tb_transaksi WHERE id_kasir = ? AND DATE(tgl_transaksi) = CURDATE()";
            PreparedStatement psPendapatan = conn.prepareStatement(sqlPendapatan);
            psPendapatan.setInt(1, idUser);
            ResultSet rsPendapatan = psPendapatan.executeQuery();
            double pendapatanHariIni = 0;
            if (rsPendapatan.next()) pendapatanHariIni = rsPendapatan.getDouble("total");
            
            createStatCard("📊 Transaksi Hari Ini", String.valueOf(totalTransaksi), new Color(52, 152, 219), 30, 80);
            createStatCard("💰 Pendapatan Hari Ini", "Rp " + String.format("%,.0f", pendapatanHariIni), new Color(46, 204, 113), 260, 80);
            
            rsTransaksi.close();
            psTransaksi.close();
            rsPendapatan.close();
            psPendapatan.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(null);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBounds(30, 220, 890, 200);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        JLabel lblInfo = new JLabel("📌 Menu Kasir");
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
            "Menu yang tersedia:\n\n" +
            "• Penjualan - Buat transaksi baru (barang atau layanan)\n" +
            "• History Transaksi - Lihat riwayat transaksi Anda\n" +
            "• Laporan - Laporan pendapatan untuk setor tunai\n\n" +
            "Selamat bekerja!"
        );
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBounds(20, 50, 850, 130);
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
    
    private void loadPenjualanContent() {
        contentPanel.removeAll();
        
        PenjualanPanel panelPenjualan = new PenjualanPanel(idUser, namaUser);
        panelPenjualan.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelPenjualan);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadHistoryContent() {
        contentPanel.removeAll();
        
        HistoryKasirPanel panelHistory = new HistoryKasirPanel(idUser);
        panelHistory.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelHistory);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadLaporanContent() {
        contentPanel.removeAll();
        
        LaporanKasirPanel panelLaporan = new LaporanKasirPanel(idUser, namaUser);
        panelLaporan.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(panelLaporan);
        
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