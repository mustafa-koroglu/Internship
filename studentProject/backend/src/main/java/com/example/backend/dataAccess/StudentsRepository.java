package com.example.backend.dataAccess;

import com.example.backend.entities.Student; // Öğrenci entity'si
import org.springframework.data.jpa.repository.JpaRepository; // JPA repository arayüzü
import org.springframework.data.jpa.repository.Query; // Query anotasyonu
import org.springframework.data.repository.query.Param; // Parametre anotasyonu
import org.springframework.stereotype.Repository; // Repository anotasyonu
import java.util.List; // Liste

@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons WHERE " + // JPQL sorgusu - derslerle birlikte
       "LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // İsim arama
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // Soyisim arama
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%')") // Numara arama
    List<Student> findByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm); // İsim, soyisim veya numara ile arama metodu

    List<Student> findByNumber(String number); // Numara ile arama metodu

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons WHERE s.verified = true AND s.view = true") // Onaylanmış ve görünür öğrencileri getir metodu - derslerle birlikte
    List<Student> findByVerifiedTrueAndViewTrue(); // Onaylanmış ve görünür öğrencileri getir metodu

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons WHERE s.verified = false") // Onaylanmamış öğrencileri getir metodu - derslerle birlikte
    List<Student> findByVerifiedFalse(); // Onaylanmamış öğrencileri getir metodu

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons WHERE s.verified = true") // Onaylanmış öğrencileri getir metodu - derslerle birlikte
    List<Student> findByVerifiedTrue(); // Onaylanmış öğrencileri getir metodu

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons WHERE s.verified = true AND s.view = true AND " + // Onaylanmış ve görünür öğrencilerde arama sorgusu - derslerle birlikte
       "(LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // İsim arama
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // Soyisim arama
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%'))") // Numara arama
    List<Student> findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm); // Onaylanmış öğrencilerde arama metodu

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons") // Tüm öğrencileri getir metodu - derslerle birlikte
    List<Student> findAllWithLessons(); // Tüm öğrencileri derslerle birlikte getir metodu
}