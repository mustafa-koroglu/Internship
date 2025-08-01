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

-- 7. Files tablosu oluştur (CSV işleme kayıtları için)
CREATE TABLE IF NOT EXISTS files (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    full_file_name VARCHAR(255) NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('DONE', 'FAIL')),
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    student_count INTEGER DEFAULT 0,
    description TEXT
);

-- 8. Files tablosu için indeksler
CREATE INDEX IF NOT EXISTS idx_files_status ON files(status);
CREATE INDEX IF NOT EXISTS idx_files_processed_at ON files(processed_at);
CREATE INDEX IF NOT EXISTS idx_files_file_name ON files(file_name);

-- 9. Files tablosu rollback script (gerekirse geri almak için)
-- DROP INDEX IF EXISTS idx_files_status;
-- DROP INDEX IF EXISTS idx_files_processed_at;
-- DROP INDEX IF EXISTS idx_files_file_name;
-- DROP TABLE IF EXISTS files; 