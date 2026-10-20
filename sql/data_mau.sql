-- Du lieu mau (tuy chon). Ung dung tu nap cac du lieu nay neu CSDL con trong.
USE `vphone`;

INSERT IGNORE INTO nguoi_dung (ma_nguoi_dung, ho_ten, email, mat_khau, so_dien_thoai, vai_tro, dia_chi, chuc_vu) VALUES
 ('QT001', 'Quan tri VPHONE', 'admin@vphone.vn', 'admin123', '0900000001', 'QUAN_TRI', '', 'Chu cua hang'),
 ('KH001', 'Nguyen Van A', 'khach@vphone.vn', '123456', '0900000002', 'KHACH_HANG', 'Ba Dinh, Ha Noi', '');

INSERT IGNORE INTO san_pham (ma_sp, ten_sp, hang, gia_ban, so_luong_ton, loai, ram, bo_nho, mau_sac, loai_phu_kien) VALUES
 ('SP01', 'iPhone 15',     'Apple',   22990000, 10, 'DIEN_THOAI', 6, 128, 'Den',  NULL),
 ('SP02', 'Galaxy S24',    'Samsung', 19990000,  5, 'DIEN_THOAI', 8, 256, 'Xam',  NULL),
 ('SP03', 'Redmi Note 13', 'Xiaomi',   5990000, 20, 'DIEN_THOAI', 8, 128, 'Xanh', NULL),
 ('SP04', 'Galaxy A55',    'Samsung',  9990000,  8, 'DIEN_THOAI', 8, 256, 'Tim',  NULL),
 ('PK01', 'Cap sac Type-C 1m',  'Anker',  250000, 50, 'PHU_KIEN', NULL, NULL, NULL, 'Cap sac'),
 ('PK02', 'Op lung iPhone 15',  'Spigen', 450000,  3, 'PHU_KIEN', NULL, NULL, NULL, 'Op lung');

INSERT IGNORE INTO nha_cung_cap (ma_ncc, ten_ncc, dia_chi, so_dien_thoai) VALUES
 ('NCC01', 'Cong ty FPT Trading',    'Ha Noi', '0241234567'),
 ('NCC02', 'Cong ty Viettel Store',  'Ha Noi', '0247654321');
