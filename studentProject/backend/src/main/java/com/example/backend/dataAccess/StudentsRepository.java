package com.example.backend.dataAccess;

import com.example.backend.entities.Student; // Öğrenci entity'si
import org.springframework.data.jpa.repository.JpaRepository; // JPA repository arayüzü
import org.springframework.data.jpa.repository.Query; // Query anotasyonu
import org.springframework.data.repository.query.Param; // Parametre anotasyonu
import org.springframework.stereotype.Repository; // Repository anotasyonu
import java.util.List; // Liste

@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM students s WHERE " + // JPQL sorgusu
       "LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // İsim arama
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // Soyisim arama
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%')") // Numara arama
    List<Student> findByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm); // İsim, soyisim veya numara ile arama metodu

    List<Student> findByNumber(String number); // Numara ile arama metodu

    List<Student> findByVerifiedTrueAndViewTrue(); // Onaylanmış ve görünür öğrencileri getir metodu

    List<Student> findByVerifiedFalse(); // Onaylanmamış öğrencileri getir metodu

    List<Student> findByVerifiedTrue(); // Onaylanmış öğrencileri getir metodu

    @Query("SELECT s FROM students s WHERE s.verified = true AND s.view = true AND " + // Onaylanmış ve görünür öğrencilerde arama sorgusu
       "(LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // İsim arama
       "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " + // Soyisim arama
       "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%'))") // Numara arama
    List<Student> findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm); // Onaylanmış öğrencilerde arama metodu
}