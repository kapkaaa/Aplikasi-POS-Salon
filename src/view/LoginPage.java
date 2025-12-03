package view;

import config.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.*;
import javax.swing.*;

public class LoginPage extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JCheckBox chkShowPassword;
    
    public LoginPage() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Setup JFrame
        setTitle("Login - Salon Blue");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        
        // ===== LEFT PANEL (Ilustrasi) =====
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(135, 206, 250),
                    0, getHeight(), new Color(70, 130, 180)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(null);
        leftPanel.setBounds(0, 0, 400, 550);
        
        // Logo dan text di left panel
        JLabel lblLogoLeft = new JLabel("💈");
        lblLogoLeft.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        lblLogoLeft.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogoLeft.setBounds(50, 150, 300, 120);
        leftPanel.add(lblLogoLeft);
        
        JLabel lblWelcome = new JLabel("SALON BLUE");
        lblWelcome.setFont(new Font("Poppins", Font.BOLD, 36));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(50, 280, 300, 45);
        leftPanel.add(lblWelcome);
        
        JLabel lblTagline = new JLabel("Aplikasi Kasir Modern");
        lblTagline.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblTagline.setForeground(new Color(240, 248, 255));
        lblTagline.setHorizontalAlignment(SwingConstants.CENTER);
        lblTagline.setBounds(50, 330, 300, 25);
        leftPanel.add(lblTagline);
        
        mainPanel.add(leftPanel);
        
        // ===== RIGHT PANEL (Form Login) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBounds(400, 0, 500, 550);
        
        // Title Login
        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("Poppins", Font.BOLD, 32));
        lblLogin.setForeground(new Color(70, 130, 180));
        lblLogin.setBounds(80, 80, 150, 40);
        rightPanel.add(lblLogin);
        
        JLabel lblSubLogin = new JLabel("Masuk ke akun Anda");
        lblSubLogin.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblSubLogin.setForeground(Color.GRAY);
        lblSubLogin.setBounds(80, 120, 200, 25);
        rightPanel.add(lblSubLogin);
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblUsername.setForeground(Color.DARK_GRAY);
        lblUsername.setBounds(80, 180, 100, 25);
        rightPanel.add(lblUsername);
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Poppins", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtUsername.setBounds(80, 210, 340, 45);
        rightPanel.add(txtUsername);
        
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblPassword.setForeground(Color.DARK_GRAY);
        lblPassword.setBounds(80, 270, 100, 25);
        rightPanel.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Poppins", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtPassword.setBounds(80, 300, 340, 45);
        rightPanel.add(txtPassword);
        
        chkShowPassword = new JCheckBox("Tampilkan Password");
        chkShowPassword.setFont(new Font("Poppins", Font.PLAIN, 12));
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.setBounds(80, 355, 200, 25);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        rightPanel.add(chkShowPassword);
        
        // Login Button
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Poppins", Font.BOLD, 14));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.black);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(80, 400, 340, 45);
        btnLogin.addActionListener(e -> handleLogin());
        
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(100, 149, 237));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(70, 130, 180));
            }
        });
        rightPanel.add(btnLogin);
        
        // Exit Button
        btnExit = new JButton("Keluar");
        btnExit.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnExit.setForeground(Color.GRAY);
        btnExit.setBackground(Color.WHITE);
        btnExit.setBorder(BorderFactory.createEmptyBorder());
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.setBounds(80, 460, 100, 25);
        btnExit.addActionListener(e -> System.exit(0));
        rightPanel.add(btnExit);
        
        mainPanel.add(rightPanel);
        
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyListener);
        txtPassword.addKeyListener(enterKeyListener);
        
        add(mainPanel);
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password tidak boleh kosong!",
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Proses login
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ? AND status = 'Aktif'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, md5(password));
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Login berhasil
                int idUser = rs.getInt("id_user");
                String namaLengkap = rs.getString("nama_lengkap");
                String role = rs.getString("role");
                
//                JOptionPane.showMessageDialog(this,
//                    "Login berhasil!\nSelamat datang, " + namaLengkap,
//                    "Sukses",
//                    JOptionPane.INFORMATION_MESSAGE);
                
                // Redirect ke dashboard sesuai role
                this.dispose();
                
                if (role.equals("Admin")) {
                    // Buka Dashboard Admin
                    new view.admin.DashboardAdmin(idUser, namaLengkap).setVisible(true);
                } else {
                    // Buka Dashboard Kasir 
                    new view.kasir.DashboardKasir(idUser, namaLengkap).setVisible(true);
                }
                
            } else {
                // Login gagal
                JOptionPane.showMessageDialog(this,
                    "Username atau password salah!\nAtau akun Anda nonaktif.",
                    "Login Gagal",
                    JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error saat login: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Method untuk enkripsi password dengan MD5
     */
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