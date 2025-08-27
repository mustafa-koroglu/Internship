package com.example.backend.entities;

import jakarta.persistence.*; // JPA anotasyonları
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Getter;
import lombok.NoArgsConstructor; // Parametresiz constructor
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_lessons",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private Set<Lesson> lessons = new HashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<IpAddress> ipAddresses = new HashSet<>();

}