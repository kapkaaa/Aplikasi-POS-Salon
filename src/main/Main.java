package main;

import view.LoadingPage;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Class - Entry Point Aplikasi
 * Kelas utama untuk menjalankan aplikasi Salon Blue
 * 
 * @author Your Name
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Set Look and Feel ke System Default
        try {
            // Pilihan Look and Feel:
            // 1. System Default (Windows/Mac/Linux native)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // 2. Nimbus (Modern & Cross-platform) - Alternatif
            // for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            //     if ("Nimbus".equals(info.getName())) {
            //         UIManager.setLookAndFeel(info.getClassName());
            //         break;
            //     }
            // }
            
        } catch (Exception e) {
            System.err.println("Error setting Look and Feel: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Jalankan aplikasi di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Tampilkan Loading Page
                LoadingPage loadingPage = new LoadingPage();
                loadingPage.setVisible(true);
                
                System.out.println("=================================");
                System.out.println("  SALON BLUE - APLIKASI KASIR");
                System.out.println("  Version 1.0.0");
                System.out.println("=================================");
                
            } catch (Exception e) {
                System.err.println("Error saat menjalankan aplikasi: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}