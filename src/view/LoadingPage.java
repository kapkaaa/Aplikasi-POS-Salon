package view;

import config.Koneksi;
import javax.swing.*;
import java.awt.*;

/**
 * Loading Page / Splash Screen
 * Tampilan awal aplikasi dengan progress bar
 */
public class LoadingPage extends JFrame {
    
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblLogo;
    private JLabel lblTitle;
    
    public LoadingPage() {
        initComponents();
        setLocationRelativeTo(null);
        startLoading();
    }
    
    private void initComponents() {
        // Setup JFrame
        setTitle("Salon Blue - Loading");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Tanpa border
        setResizable(false);
        
        // Panel utama dengan gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient dari biru muda ke biru tua
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(135, 206, 250),
                    0, getHeight(), new Color(70, 130, 180)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        
        // Logo (gunakan icon atau text)
        lblLogo = new JLabel("💈");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBounds(200, 50, 100, 100);
        mainPanel.add(lblLogo);
        
        // Title
        lblTitle = new JLabel("SALON BLUE");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(100, 160, 300, 40);
        mainPanel.add(lblTitle);
        
        // Subtitle
        JLabel lblSubtitle = new JLabel("Aplikasi Kasir Salon Unisex");
        lblSubtitle.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setBounds(100, 200, 300, 25);
        mainPanel.add(lblSubtitle);
        
        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(100, 260, 300, 25);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(true);
        mainPanel.add(progressBar);
        
        // Status Label
        lblStatus = new JLabel("Memuat aplikasi...");
        lblStatus.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setBounds(100, 295, 300, 20);
        mainPanel.add(lblStatus);
        
        // Version
        JLabel lblVersion = new JLabel("v1.0.0");
        lblVersion.setFont(new Font("Poppins", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(240, 248, 255));
        lblVersion.setBounds(420, 360, 60, 20);
        mainPanel.add(lblVersion);
        
        add(mainPanel);
    }
    
    private void startLoading() {
        // Thread untuk progress bar
        Thread loadingThread = new Thread(() -> {
            try {
                // Step 1: Inisialisasi (0-30%)
                updateProgress(0, "Memuat aplikasi...");
                Thread.sleep(500);
                updateProgress(15, "Memuat komponen...");
                Thread.sleep(500);
                updateProgress(30, "Menginisialisasi...");
                Thread.sleep(500);
                
                // Step 2: Cek koneksi database (30-70%)
                updateProgress(40, "Menghubungkan ke database...");
                Thread.sleep(700);
                
                boolean dbConnected = Koneksi.testKoneksi();
                
                if (dbConnected) {
                    updateProgress(70, "Database terhubung!");
                    Thread.sleep(500);
                } else {
                    updateProgress(70, "Database gagal terhubung!");
                    Thread.sleep(1000);
                    JOptionPane.showMessageDialog(this, 
                        "Tidak dapat terhubung ke database!\nPastikan MySQL sudah berjalan.", 
                        "Error Database", 
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                
                // Step 3: Persiapan UI (70-100%)
                updateProgress(85, "Memuat interface...");
                Thread.sleep(500);
                updateProgress(100, "Selesai!");
                Thread.sleep(500);
                
                // Tutup loading dan buka login page
                this.dispose();
                new LoginPage().setVisible(true);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        loadingThread.start();
    }
    
    private void updateProgress(int value, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            lblStatus.setText(status);
        });
    }
    
}