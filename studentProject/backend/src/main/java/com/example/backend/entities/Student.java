package com.example.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Student (Öğrenci) tablosunu temsil eden entity sınıfı
@Entity(name="students")
public class Student {

    // Birincil anahtar (id)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    // Öğrencinin adı
    @Column(name ="name")
    private String name;
    // Öğrencinin soyadı
    @Column(name="surname")
    private String surname;
    // Öğrenci numarası (benzersiz)
    @Column(unique=true,name ="number")
    private String number;
}
