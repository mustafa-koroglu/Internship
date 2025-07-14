// Bu dosya, Student entity'si için veri tabanı işlemlerini gerçekleştiren repository arayüzünü tanımlar.
package com.example.backend.dataAccess;

// Student entity'sini import eder
import com.example.backend.entities.Student;
// Spring Data JPA'nın JpaRepository arayüzünü import eder
import org.springframework.data.jpa.repository.JpaRepository;
// Özel sorgular için @Query anotasyonunu import eder
import org.springframework.data.jpa.repository.Query;
// Parametreli sorgular için @Param anotasyonunu import eder
import org.springframework.data.repository.query.Param;
// Repository olarak işaretlemek için @Repository anotasyonunu import eder
import org.springframework.stereotype.Repository;

// Liste veri tipi için import
import java.util.List;

// Bu arayüz, öğrenci tablosu için veri erişim işlemlerini sağlar
@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {
    
    // Öğrenci ismine göre arama yapar (büyük/küçük harf duyarsız)
    List<Student> findByNameContainingIgnoreCase(String name);
    
    // Öğrenci ismi, soyismi veya numarasına göre BAŞLANGICI eşleşen arama yapar
    // Büyük/küçük harf duyarsız, baştaki boşlukları temizler
    @Query("SELECT s FROM students s WHERE " +
       "LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%')")
    List<Student> findByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);

    // Öğrenci numarasına göre BAŞLANGICI eşleşen arama yapar
    List<Student> findByNumberStartingWith(String number);
}