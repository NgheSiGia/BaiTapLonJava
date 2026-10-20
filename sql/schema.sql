-- VPHONE - luoc do CSDL cho MySQL/MariaDB (XAMPP)
-- Cach dung: phpMyAdmin -> Import -> chon file nay. (Ung dung cung tu tao CSDL/bang khi chay lan dau.)

CREATE DATABASE IF NOT EXISTS `vphone` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `vphone`;

CREATE TABLE IF NOT EXISTS nguoi_dung (
  ma_nguoi_dung VARCHAR(20) PRIMARY KEY,
  ho_ten VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  mat_khau VARCHAR(100) NOT NULL,
  so_dien_thoai VARCHAR(20) NOT NULL DEFAULT '',
  vai_tro VARCHAR(20) NOT NULL,
  dia_chi VARCHAR(200) NOT NULL DEFAULT '',
  chuc_vu VARCHAR(100) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS san_pham (
  ma_sp VARCHAR(20) PRIMARY KEY,
  ten_sp VARCHAR(150) NOT NULL,
  hang VARCHAR(50) NOT NULL,
  gia_ban DECIMAL(15,0) NOT NULL CHECK (gia_ban > 0),
  so_luong_ton INT NOT NULL CHECK (so_luong_ton >= 0),
  loai VARCHAR(20) NOT NULL,
  ram INT NULL,
  bo_nho INT NULL,
  mau_sac VARCHAR(50) NULL,
  loai_phu_kien VARCHAR(100) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS nha_cung_cap (
  ma_ncc VARCHAR(20) PRIMARY KEY,
  ten_ncc VARCHAR(150) NOT NULL,
  dia_chi VARCHAR(200) NOT NULL DEFAULT '',
  so_dien_thoai VARCHAR(20) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS don_hang (
  ma_don_hang VARCHAR(20) PRIMARY KEY,
  ma_khach_hang VARCHAR(20) NOT NULL,
  ten_khach_hang VARCHAR(100) NOT NULL,
  ngay_dat DATE NOT NULL,
  trang_thai VARCHAR(20) NOT NULL,
  FOREIGN KEY (ma_khach_hang) REFERENCES nguoi_dung(ma_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chi_tiet_don_hang (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ma_don_hang VARCHAR(20) NOT NULL,
  ma_sp VARCHAR(20) NOT NULL,
  so_luong INT NOT NULL CHECK (so_luong > 0),
  don_gia DECIMAL(15,0) NOT NULL,
  FOREIGN KEY (ma_don_hang) REFERENCES don_hang(ma_don_hang) ON DELETE CASCADE,
  FOREIGN KEY (ma_sp) REFERENCES san_pham(ma_sp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS phieu_nhap (
  ma_phieu VARCHAR(20) PRIMARY KEY,
  ma_ncc VARCHAR(20) NOT NULL,
  ma_quan_tri VARCHAR(20) NOT NULL,
  ngay_nhap DATE NOT NULL,
  FOREIGN KEY (ma_ncc) REFERENCES nha_cung_cap(ma_ncc),
  FOREIGN KEY (ma_quan_tri) REFERENCES nguoi_dung(ma_nguoi_dung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chi_tiet_phieu_nhap (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ma_phieu VARCHAR(20) NOT NULL,
  ma_sp VARCHAR(20) NOT NULL,
  so_luong_nhap INT NOT NULL CHECK (so_luong_nhap > 0),
  gia_nhap DECIMAL(15,0) NOT NULL CHECK (gia_nhap > 0),
  FOREIGN KEY (ma_phieu) REFERENCES phieu_nhap(ma_phieu) ON DELETE CASCADE,
  FOREIGN KEY (ma_sp) REFERENCES san_pham(ma_sp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
