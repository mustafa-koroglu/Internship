
package com.example.backend.dataAccess;

import com.example.backend.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Öğrenci veritabanı işlemlerini yöneten repository interface'i.
 * 
 * Öğrenci onay sistemi için eklenen metodlar:
 * - findVerifiedAndViewable(): Kullanıcılar için onaylanmış ve görünür öğrenciler
 * - findUnverified(): Admin için onaylanmamış öğrenciler
 * - findVerified(): Admin için onaylanmış öğrenciler
 * - findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(): Kullanıcılar için arama
 */
@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {

    /**
     * Öğrenci ismi, soyismi veya numarasına göre BAŞLANGICI eşleşen arama yapar
     * Büyük/küçük harf duyarsız, baştaki boşlukları temizler
     * @param searchTerm Arama terimi
     * @return Öğrenci listesi
     */
    @Query("SELECT s FROM students s WHERE " +
       "LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%')")
    List<Student> findByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Öğrenci numarasına göre arama yapar
     * @param number Öğrenci numarası
     * @return Öğrenci listesi (boş liste döner eğer bulunamazsa)
     */
    List<Student> findByNumber(String number);

    /**
     * Onaylanmış ve görünür öğrencileri getirir (kullanıcılar için)
     * 
     * Bu metod, sadece admin tarafından onaylanmış VE kullanıcılara gösterilmesine
     * izin verilen öğrencileri döner. Kullanıcı arayüzünde sadece bu öğrenciler
     * görüntülenir.
     * 
     * İş kuralları:
     * - verified = true (admin onaylamış)
     * - view = true (kullanıcılara gösterilir)
     * 
     * @return Onaylanmış ve görünür öğrenci listesi
     */
    List<Student> findByVerifiedTrueAndViewTrue();

    /**
     * Onaylanmamış öğrencileri getirir (admin için)
     * 
     * Bu metod, henüz admin tarafından onaylanmamış öğrencileri döner.
     * Admin panelinde bu öğrenciler "Onaysız" olarak görünür ve
     * onaylama butonu ile onaylanabilir.
     * 
     * İş kuralları:
     * - verified = false (henüz onaylanmamış)
     * - Genellikle CSV'den okunan öğrenciler
     * 
     * @return Onaylanmamış öğrenci listesi
     */
    List<Student> findByVerifiedFalse();

    /**
     * Onaylanmış öğrencileri getirir (admin için)
     * 
     * Bu metod, admin tarafından onaylanmış tüm öğrencileri döner.
     * Görünürlük durumuna bakmaksızın sadece onay durumunu kontrol eder.
     * 
     * İş kuralları:
     * - verified = true (admin onaylamış)
     * - view durumu önemli değil
     * 
     * @return Onaylanmış öğrenci listesi
     */
    List<Student> findByVerifiedTrue();

    /**
     * Onaylanmış ve görünür öğrencilerde arama yapar (kullanıcılar için)
     * 
     * Bu metod, kullanıcıların arama yapması için kullanılır.
     * Sadece onaylanmış VE görünür öğrencilerde arama yapar.
     * 
     * Arama kriterleri:
     * - İsim, soyisim veya numara ile başlayan eşleşmeler
     * - Büyük/küçük harf duyarsız
     * - Baştaki boşlukları temizler
     * 
     * İş kuralları:
     * - verified = true AND view = true
     * - Kullanıcılar sadece bu öğrencileri arayabilir
     * 
     * @param searchTerm Arama terimi
     * @return Filtrelenmiş öğrenci listesi
     */
    @Query("SELECT s FROM students s WHERE s.verified = true AND s.view = true AND " +
       "(LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%'))")
    List<Student> findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);
}