-- VPHONE - tao user (schema) VPHONE trong PDB. CHAY BANG QUYEN SYSDBA, chi can lam mot lan.
--   Cach 1 (SQL*Plus):      sqlplus / as sysdba     roi go:  @01_tao_user_oracle.sql
--   Cach 2 (SQL Developer): ket noi SYS (Role: SYSDBA) roi chay file nay.
-- Ten PDB cua Oracle Free la FREEPDB1 (kiem tra bang lenh: show pdbs;). Neu PDB cua ban khac, doi ten ben duoi.

ALTER SESSION SET CONTAINER = FREEPDB1;

-- Neu da tao truoc do va muon lam lai: bo dau -- o dong duoi (se xoa het du lieu cua VPHONE)
-- DROP USER vphone CASCADE;

CREATE USER vphone IDENTIFIED BY vphone123
  DEFAULT TABLESPACE users
  TEMPORARY TABLESPACE temp
  QUOTA UNLIMITED ON users;

GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE, CREATE TRIGGER TO vphone;

-- Kiem tra: ket noi thu
--   CONN vphone/vphone123@localhost:1521/freepdb1
--   SHOW USER;
