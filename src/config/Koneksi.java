package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Kelas untuk mengelola koneksi ke database MySQL
 */
public class Koneksi {
    
    private static Connection conn;
    
    // Konfigurasi database
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "salon_blue";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; // Sesuaikan dengan password MySQL Anda
    
    /**
     * Method untuk mendapatkan koneksi database
     * @return Connection object
     */
    public static Connection getKoneksi() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // URL koneksi
            String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
            
            // Buat koneksi
            conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
            
            System.out.println("✓ Koneksi database berhasil!");
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL tidak ditemukan!");
            JOptionPane.showMessageDialog(null, 
                "Driver MySQL tidak ditemukan!\n" + e.getMessage(), 
                "Error Driver", 
                JOptionPane.ERROR_MESSAGE);
            return null;
            
        } catch (SQLException e) {
            System.err.println("✗ Koneksi database gagal!");
            JOptionPane.showMessageDialog(null, 
                "Gagal terhubung ke database!\n" + e.getMessage(), 
                "Error Koneksi", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Method untuk menutup koneksi database
     */
    public static void closeKoneksi() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("✓ Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error saat menutup koneksi: " + e.getMessage());
        }
    }
    
    /**
     * Method untuk test koneksi
     * @return true jika koneksi berhasil
     */
    public static boolean testKoneksi() {
        Connection testConn = getKoneksi();
        if (testConn != null) {
            closeKoneksi();
            return true;
        }
        return false;
    }
}