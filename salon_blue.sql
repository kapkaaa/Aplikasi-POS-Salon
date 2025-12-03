/*
 Database Schema: salon_blue
 Cleaned and optimized SQL dump
 Date: 27/10/2025
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS salon_blue;
USE salon_blue;

-- ----------------------------
-- Table structure for tb_kategori_barang
-- ----------------------------
DROP TABLE IF EXISTS `tb_kategori_barang`;
CREATE TABLE `tb_kategori_barang` (
  `id_kategori` INT NOT NULL AUTO_INCREMENT,
  `nama_kategori` VARCHAR(50) NOT NULL,
  `deskripsi` TEXT,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_kategori`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id_user` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nama_lengkap` VARCHAR(100) NOT NULL,
  `alamat` VARCHAR(100) NOT NULL,
  `role` ENUM('Admin','Kasir') NOT NULL,
  `no_telepon` VARCHAR(15) DEFAULT NULL,
  `status` ENUM('Aktif','Nonaktif') DEFAULT 'Aktif',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_barang
-- ----------------------------
DROP TABLE IF EXISTS `tb_barang`;
CREATE TABLE `tb_barang` (
  `id_barang` INT NOT NULL AUTO_INCREMENT,
  `id_kategori` INT DEFAULT NULL,
  `nama_barang` VARCHAR(100) NOT NULL,
  `harga_jual` DECIMAL(10,2) NOT NULL,
  `stok` INT NOT NULL DEFAULT 0,
  `satuan` VARCHAR(20) DEFAULT 'pcs',
  `deskripsi` TEXT,
  `status` ENUM('Tersedia','Habis') DEFAULT 'Tersedia',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_barang`),
  KEY `idx_barang_nama` (`nama_barang`),
  KEY `fk_barang_kategori` (`id_kategori`),
  CONSTRAINT `fk_barang_kategori` FOREIGN KEY (`id_kategori`) REFERENCES `tb_kategori_barang` (`id_kategori`) ON DELETE SET NULL,
  CONSTRAINT `chk_barang_stok` CHECK (`stok` >= 0),
  CONSTRAINT `chk_barang_harga` CHECK (`harga_jual` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_layanan
-- ----------------------------
DROP TABLE IF EXISTS `tb_layanan`;
CREATE TABLE `tb_layanan` (
  `id_layanan` INT NOT NULL AUTO_INCREMENT,
  `nama_layanan` VARCHAR(100) NOT NULL,
  `harga_layanan` DECIMAL(10,2) NOT NULL,
  `durasi_estimasi` INT DEFAULT NULL COMMENT 'Durasi dalam menit',
  `deskripsi` TEXT,
  `status` ENUM('Tersedia','Tidak Tersedia') DEFAULT 'Tersedia',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_layanan`),
  KEY `idx_layanan_nama` (`nama_layanan`),
  CONSTRAINT `chk_layanan_harga` CHECK (`harga_layanan` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_transaksi
-- ----------------------------
DROP TABLE IF EXISTS `tb_transaksi`;
CREATE TABLE `tb_transaksi` (
  `id_transaksi` INT NOT NULL AUTO_INCREMENT,
  `no_transaksi` VARCHAR(50) NOT NULL,
  `id_kasir` INT NOT NULL,
  `tgl_transaksi` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `jenis_transaksi` ENUM('Barang','Layanan','Keduanya') NOT NULL,
  `metode_pembayaran` ENUM('Cash','QRIS') NOT NULL,
  `total_harga` DECIMAL(12,2) NOT NULL,
  `nama_pelanggan` VARCHAR(100) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_transaksi`),
  UNIQUE KEY `no_transaksi` (`no_transaksi`),
  KEY `idx_transaksi_tanggal` (`tgl_transaksi`),
  KEY `idx_transaksi_kasir` (`id_kasir`),
  KEY `idx_transaksi_metode` (`metode_pembayaran`),
  CONSTRAINT `fk_transaksi_kasir` FOREIGN KEY (`id_kasir`) REFERENCES `tb_user` (`id_user`),
  CONSTRAINT `chk_transaksi_total` CHECK (`total_harga` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_detail_transaksi_barang
-- ----------------------------
DROP TABLE IF EXISTS `tb_detail_transaksi_barang`;
CREATE TABLE `tb_detail_transaksi_barang` (
  `id_detail_barang` INT NOT NULL AUTO_INCREMENT,
  `id_transaksi` INT NOT NULL,
  `id_barang` INT NOT NULL,
  `quantity` INT NOT NULL,
  `harga_satuan` DECIMAL(10,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_detail_barang`),
  KEY `idx_detail_barang_transaksi` (`id_transaksi`),
  KEY `idx_detail_barang_barang` (`id_barang`),
  CONSTRAINT `fk_detail_barang_transaksi` FOREIGN KEY (`id_transaksi`) REFERENCES `tb_transaksi` (`id_transaksi`) ON DELETE CASCADE,
  CONSTRAINT `fk_detail_barang_barang` FOREIGN KEY (`id_barang`) REFERENCES `tb_barang` (`id_barang`),
  CONSTRAINT `chk_detail_barang_qty` CHECK (`quantity` > 0),
  CONSTRAINT `chk_detail_barang_harga` CHECK (`harga_satuan` > 0),
  CONSTRAINT `chk_detail_barang_subtotal` CHECK (`subtotal` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_detail_transaksi_layanan
-- ----------------------------
DROP TABLE IF EXISTS `tb_detail_transaksi_layanan`;
CREATE TABLE `tb_detail_transaksi_layanan` (
  `id_detail_layanan` INT NOT NULL AUTO_INCREMENT,
  `id_transaksi` INT NOT NULL,
  `id_layanan` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `harga_satuan` DECIMAL(10,2) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_detail_layanan`),
  KEY `idx_detail_layanan_transaksi` (`id_transaksi`),
  KEY `idx_detail_layanan_layanan` (`id_layanan`),
  CONSTRAINT `fk_detail_layanan_transaksi` FOREIGN KEY (`id_transaksi`) REFERENCES `tb_transaksi` (`id_transaksi`) ON DELETE CASCADE,
  CONSTRAINT `fk_detail_layanan_layanan` FOREIGN KEY (`id_layanan`) REFERENCES `tb_layanan` (`id_layanan`),
  CONSTRAINT `chk_detail_layanan_qty` CHECK (`quantity` > 0),
  CONSTRAINT `chk_detail_layanan_harga` CHECK (`harga_satuan` > 0),
  CONSTRAINT `chk_detail_layanan_subtotal` CHECK (`subtotal` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tb_log_stok
-- ----------------------------
DROP TABLE IF EXISTS `tb_log_stok`;
CREATE TABLE `tb_log_stok` (
  `id_log` INT NOT NULL AUTO_INCREMENT,
  `id_barang` INT NOT NULL,
  `jenis_aktivitas` ENUM('Tambah','Kurang','Penjualan') NOT NULL,
  `jumlah` INT NOT NULL,
  `stok_sebelum` INT NOT NULL,
  `stok_sesudah` INT NOT NULL,
  `keterangan` TEXT,
  `id_user` INT DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_log`),
  KEY `idx_log_barang` (`id_barang`),
  KEY `idx_log_user` (`id_user`),
  CONSTRAINT `fk_log_barang` FOREIGN KEY (`id_barang`) REFERENCES `tb_barang` (`id_barang`) ON DELETE CASCADE,
  CONSTRAINT `fk_log_user` FOREIGN KEY (`id_user`) REFERENCES `tb_user` (`id_user`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Insert data for tb_kategori_barang
-- ----------------------------
INSERT INTO `tb_kategori_barang` (`id_kategori`, `nama_kategori`, `deskripsi`, `created_at`) VALUES
(1, 'Hair Care', 'Produk perawatan rambut', '2025-10-25 14:27:45'),
(2, 'Styling Product', 'Produk styling rambut', '2025-10-25 14:27:45'),
(3, 'Face Care', 'Produk perawatan wajah', '2025-10-25 14:27:45'),
(4, 'Aksesoris', 'Aksesoris salon', '2025-10-25 14:27:45');

-- ----------------------------
-- Insert data for tb_user
-- ----------------------------
INSERT INTO `tb_user` (`id_user`, `username`, `password`, `nama_lengkap`, `alamat`, `role`, `no_telepon`, `status`) VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', 'Administrator', 'rumah', 'Admin', '081234567890', 'Aktif'),
(2, 'kasir1', 'de28f8f7998f23ab4194b51a6029416f', 'Kasir Pertama', 'rumah', 'Kasir', '081234567891', 'Aktif');

-- ----------------------------
-- Insert data for tb_barang
-- ----------------------------
INSERT INTO `tb_barang` (`id_barang`, `id_kategori`, `nama_barang`, `harga_jual`, `stok`, `satuan`, `deskripsi`, `status`) VALUES
(1, 2, 'Pomade Premium', 75000.00, 47, 'pcs', 'Pomade berbahan natural', 'Tersedia'),
(2, 1, 'Shampoo Anti Dandruff', 45000.00, 30, 'botol', 'Shampoo anti ketombe', 'Tersedia'),
(3, 1, 'Hair Tonic', 35000.00, 37, 'botol', 'Hair tonic untuk pertumbuhan rambut', 'Tersedia'),
(4, 3, 'Masker Wajah', 25000.00, 24, 'pcs', 'Masker wajah organik', 'Tersedia');

-- ----------------------------
-- Insert data for tb_layanan
-- ----------------------------
INSERT INTO `tb_layanan` (`id_layanan`, `nama_layanan`, `harga_layanan`, `durasi_estimasi`, `deskripsi`, `status`) VALUES
(1, 'Potong Rambut Pria', 35000.00, 30, 'Potong rambut model klasik hingga modern', 'Tersedia'),
(2, 'Potong Rambut Wanita', 50000.00, 45, 'Potong rambut wanita + styling', 'Tersedia'),
(3, 'Hair Coloring', 150000.00, 90, 'Pewarnaan rambut dengan berbagai pilihan warna', 'Tersedia'),
(4, 'Smoothing', 250000.00, 120, 'Treatment smoothing untuk rambut lurus berkilau', 'Tersedia'),
(5, 'Facial Wash', 75000.00, 60, 'Pembersihan wajah menyeluruh', 'Tersedia'),
(6, 'Creambath', 85000.00, 60, 'Perawatan rambut dengan creambath', 'Tersedia'),
(7, 'Hair Spa', 125000.00, 75, 'Treatment spa untuk kesehatan rambut', 'Tersedia');

-- ----------------------------
-- View: vw_stok_barang
-- ----------------------------
DROP VIEW IF EXISTS `vw_stok_barang`;
CREATE VIEW `vw_stok_barang` AS
SELECT 
    b.id_barang,
    b.nama_barang,
    k.nama_kategori,
    b.harga_jual,
    b.stok,
    b.satuan,
    b.status,
    CASE 
        WHEN b.stok = 0 THEN 'Habis'
        WHEN b.stok < 10 THEN 'Stok Menipis'
        ELSE 'Aman'
    END AS kondisi_stok
FROM tb_barang b
LEFT JOIN tb_kategori_barang k ON b.id_kategori = k.id_kategori;

-- ----------------------------
-- View: vw_history_transaksi
-- ----------------------------
DROP VIEW IF EXISTS `vw_history_transaksi`;
CREATE VIEW `vw_history_transaksi` AS
SELECT 
    t.id_transaksi,
    t.no_transaksi,
    t.tgl_transaksi,
    t.jenis_transaksi,
    t.metode_pembayaran,
    t.total_harga,
    t.nama_pelanggan,
    u.nama_lengkap AS nama_kasir,
    u.username AS username_kasir
FROM tb_transaksi t
JOIN tb_user u ON t.id_kasir = u.id_user
ORDER BY t.tgl_transaksi DESC;

-- ----------------------------
-- View: vw_laporan_pendapatan
-- ----------------------------
DROP VIEW IF EXISTS `vw_laporan_pendapatan`;
CREATE VIEW `vw_laporan_pendapatan` AS
SELECT 
    DATE(t.tgl_transaksi) AS tanggal,
    t.metode_pembayaran,
    COUNT(t.id_transaksi) AS jumlah_transaksi,
    SUM(t.total_harga) AS total_pendapatan,
    u.nama_lengkap AS nama_kasir
FROM tb_transaksi t
JOIN tb_user u ON t.id_kasir = u.id_user
GROUP BY DATE(t.tgl_transaksi), t.metode_pembayaran, u.nama_lengkap;

-- ----------------------------
-- Stored Procedure: sp_generate_no_transaksi
-- ----------------------------
DROP PROCEDURE IF EXISTS `sp_generate_no_transaksi`;

DELIMITER $$
CREATE PROCEDURE `sp_generate_no_transaksi`(OUT no_transaksi VARCHAR(50))
BEGIN
    DECLARE tgl_now VARCHAR(8);
    DECLARE counter INT;
    
    SET tgl_now = DATE_FORMAT(NOW(), '%Y%m%d');
    
    SELECT COUNT(*) + 1 INTO counter
    FROM tb_transaksi
    WHERE DATE(tgl_transaksi) = CURDATE();
    
    SET no_transaksi = CONCAT('TRX-', tgl_now, '-', LPAD(counter, 4, '0'));
END$$
DELIMITER ;

-- ----------------------------
-- Trigger: trg_update_status_barang
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_update_status_barang`;

DELIMITER $$
CREATE TRIGGER `trg_update_status_barang` 
BEFORE UPDATE ON `tb_barang`
FOR EACH ROW
BEGIN
    IF NEW.stok = 0 THEN
        SET NEW.status = 'Habis';
    ELSEIF NEW.stok > 0 THEN
        SET NEW.status = 'Tersedia';
    END IF;
END$$
DELIMITER ;

-- ----------------------------
-- Trigger: trg_after_insert_detail_barang
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_after_insert_detail_barang`;

DELIMITER $$
CREATE TRIGGER `trg_after_insert_detail_barang` 
AFTER INSERT ON `tb_detail_transaksi_barang`
FOR EACH ROW
BEGIN
    DECLARE stok_lama INT;
    DECLARE no_trx VARCHAR(50);
    
    -- Ambil stok sebelum update
    SELECT stok INTO stok_lama FROM tb_barang WHERE id_barang = NEW.id_barang;
    
    -- Ambil nomor transaksi
    SELECT no_transaksi INTO no_trx FROM tb_transaksi WHERE id_transaksi = NEW.id_transaksi;
    
    -- Update stok barang
    UPDATE tb_barang 
    SET stok = stok - NEW.quantity
    WHERE id_barang = NEW.id_barang;
    
    -- Log aktivitas stok
    INSERT INTO tb_log_stok (id_barang, jenis_aktivitas, jumlah, stok_sebelum, stok_sesudah, keterangan)
    VALUES (
        NEW.id_barang,
        'Penjualan',
        NEW.quantity,
        stok_lama,
        stok_lama - NEW.quantity,
        CONCAT('Transaksi: ', no_trx)
    );
END$$
DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;

-- Query selesai
SELECT 'Database salon_blue berhasil dibuat!' AS status;