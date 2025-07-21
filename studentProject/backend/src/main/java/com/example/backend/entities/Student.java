
package com.example.backend.entities;

import jakarta.persistence.*; // JPA için gerekli anotasyonlar
import lombok.AllArgsConstructor; // Tüm alanlar için constructor oluşturur
import lombok.Data; // Getter, setter, toString, equals, hashCode otomatik ekler
import lombok.NoArgsConstructor; // Parametresiz constructor oluşturur

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="students")
public class Student {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name ="name")
    private String name;

    @Column(name="surname")
    private String surname;

    @Column(unique=true, name ="number")
    private String number;
}