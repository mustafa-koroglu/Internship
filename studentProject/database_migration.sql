-- Öğrenci Yönetim Sistemi - Veritabanı Migration Script
-- Bu script, mevcut students tablosuna verified ve view alanlarını ekler

-- PostgreSQL için migration script
-- Tarih: 2024-01-01

-- 1. Mevcut students tablosuna yeni alanları ekle
ALTER TABLE students 
ADD COLUMN IF NOT EXISTS verified BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS view BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Mevcut kayıtları güncelle (manuel eklenen öğrenciler onaylı ve görünür olsun)
-- Bu komut, mevcut tüm öğrencileri onaylı ve görünür yapar
-- Eğer sadece belirli öğrencileri onaylamak istiyorsanız, WHERE koşulu ekleyebilirsiniz
UPDATE students 
SET verified = TRUE, view = TRUE 
WHERE verified IS NULL OR view IS NULL;

-- 3. İndeksler oluştur (performans için)
CREATE INDEX IF NOT EXISTS idx_students_verified ON students(verified);
CREATE INDEX IF NOT EXISTS idx_students_view ON students(view);
CREATE INDEX IF NOT EXISTS idx_students_verified_view ON students(verified, view);

-- 4. Tablo yapısını kontrol et
-- Bu komutları çalıştırarak tablo yapısını kontrol edebilirsiniz:
-- \d students (PostgreSQL)
-- DESCRIBE students; (MySQL)

-- 5. Örnek veri ekleme (test için)
-- INSERT INTO students (name, surname, number, verified, view) VALUES 
-- ('Test', 'Öğrenci', '2024001', TRUE, TRUE),
-- ('Onaysız', 'Öğrenci', '2024002', FALSE, FALSE);

-- 6. Rollback script (gerekirse geri almak için)
-- ALTER TABLE students DROP COLUMN IF EXISTS verified;
-- ALTER TABLE students DROP COLUMN IF EXISTS view;
-- DROP INDEX IF EXISTS idx_students_verified;
-- DROP INDEX IF EXISTS idx_students_view;
-- DROP INDEX IF EXISTS idx_students_verified_view;

-- Migration tamamlandı!
-- Artık öğrenciler verified ve view alanlarına sahip.
-- CSV'den okunan öğrenciler: verified=FALSE, view=FALSE
-- Manuel eklenen öğrenciler: verified=TRUE, view=TRUE 