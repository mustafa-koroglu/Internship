package com.example.backend.entities;

import jakarta.persistence.*; // JPA anotasyonları
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Data; // Getter, setter, toString, equals, hashCode
import lombok.NoArgsConstructor; // Parametresiz constructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(unique = true, name = "number")
    private String number;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "view", nullable = false)
    private Boolean view = false;
}