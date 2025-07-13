package com.example.backend.dataAccess;

import com.example.backend.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Öğrenci tablosu için veri erişim işlemlerini sağlayan repository arayüzü
@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {
    
    // İsme göre arama (büyük/küçük harf duyarsız)
    List<Student> findByNameContainingIgnoreCase(String name);
    
     // İsim veya soyisme göre arama (BAŞLANGICI eşleşen, büyük/küçük harf duyarsız, baştaki boşlukları temizler)
     @Query("SELECT s FROM students s WHERE LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(:searchTerm, '%')) OR LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(:searchTerm, '%'))")
     List<Student> findByNameOrSurnameStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);

    // Numaraya göre arama (BAŞLANGICI eşleşen)
    List<Student> findByNumberStartingWith(String number);
 }



    
   