package com.example.backend.dataAccess;

import com.example.backend.entities.Student; // Öğrenci entity'si
import org.springframework.data.jpa.repository.JpaRepository; // JPA repository arayüzü
import org.springframework.data.jpa.repository.Query; // Query anotasyonu
import org.springframework.data.repository.query.Param; // Parametre anotasyonu
import org.springframework.stereotype.Repository; // Repository anotasyonu

import java.util.List; // Liste

@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons LEFT JOIN FETCH s.ipAddresses WHERE " +
            "LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
            "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
            "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%')")
    List<Student> findByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);

    List<Student> findByNumber(String number);

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons LEFT JOIN FETCH s.ipAddresses WHERE s.verified = true AND s.view = true")
    List<Student> findByVerifiedTrueAndViewTrue();

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons LEFT JOIN FETCH s.ipAddresses WHERE s.verified = false")
    List<Student> findByVerifiedFalse();

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons LEFT JOIN FETCH s.ipAddresses WHERE s.verified = true")
    List<Student> findByVerifiedTrue();

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons LEFT JOIN FETCH s.ipAddresses WHERE s.verified = true AND s.view = true AND " +
            "(LOWER(TRIM(s.name)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
            "LOWER(TRIM(s.surname)) LIKE LOWER(CONCAT(TRIM(:searchTerm), '%')) OR " +
            "TRIM(CAST(s.number AS string)) LIKE CONCAT(TRIM(:searchTerm), '%'))")
    List<Student> findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT s FROM students s LEFT JOIN FETCH s.lessons LEFT JOIN FETCH s.ipAddresses")
    List<Student> findAllWithLessons();
}