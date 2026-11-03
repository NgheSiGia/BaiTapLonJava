-- VPHONE - xoa toan bo bang (de lam lai tu dau). Chay bang user VPHONE.
-- Thu tu xoa: bang con truoc, bang cha sau.
DROP TABLE chi_tiet_phieu_nhap CASCADE CONSTRAINTS;
DROP TABLE phieu_nhap          CASCADE CONSTRAINTS;
DROP TABLE chi_tiet_don_hang   CASCADE CONSTRAINTS;
DROP TABLE don_hang            CASCADE CONSTRAINTS;
DROP TABLE nha_cung_cap        CASCADE CONSTRAINTS;
DROP TABLE san_pham            CASCADE CONSTRAINTS;
DROP TABLE nguoi_dung          CASCADE CONSTRAINTS;

-- Don sach thung rac (recycle bin) cua Oracle: neu khong, cac bang vua xoa van nam o dang BIN$... trong user_objects.
PURGE RECYCLEBIN;
