package com.example.backend.entities; // Entity paketi

import jakarta.persistence.*; // JPA anotasyonları
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Getter;
import lombok.NoArgsConstructor; // Parametresiz constructor
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor // Tüm alanlar için constructor
@NoArgsConstructor // Parametresiz constructor
@Entity(name = "students") // JPA entity anotasyonu
public class Student { // Öğrenci entity sınıfı
    @Id // Primary key anotasyonu
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Otomatik artan ID
    private int id; // Öğrenci ID'si

    @Column(name = "name") // İsim sütunu
    private String name; // Öğrenci adı

    @Column(name = "surname") // Soyisim sütunu
    private String surname; // Öğrenci soyadı

    @Column(unique = true, name = "number") // Benzersiz numara sütunu
    private String number; // Öğrenci numarası

    @Column(name = "verified", nullable = false) // Onay durumu sütunu
    private Boolean verified = false; // Onay durumu

    @Column(name = "view", nullable = false) // Görünürlük sütunu
    private Boolean view = false; // Görünürlük durumu

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_lessons",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private Set<Lesson> lessons = new HashSet<>();

}