// Bu dosya, öğrenci entity'sini tanımlar ve veritabanı ile ilişkilendirir.
package com.example.backend.entities;

// JPA anotasyonları ve Lombok kütüphanesi importları
import jakarta.persistence.*; // JPA için gerekli anotasyonlar
import lombok.AllArgsConstructor; // Tüm alanlar için constructor oluşturur
import lombok.Data; // Getter, setter, toString, equals, hashCode otomatik ekler
import lombok.NoArgsConstructor; // Parametresiz constructor oluşturur

// Lombok ile getter, setter, toString, equals ve hashCode metodlarını ekler
@Data
// Tüm alanlar için constructor ekler
@AllArgsConstructor
// Parametresiz constructor ekler
@NoArgsConstructor
// Bu sınıfın bir JPA entity olduğunu belirtir ve veritabanında "students" tablosuna karşılık gelir
@Entity(name="students")
public class Student {

    // Birincil anahtar (id) alanı, otomatik olarak artan değer
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    // Öğrencinin adı, veritabanında "name" sütununa karşılık gelir
    @Column(name ="name")
    private String name;

    // Öğrencinin soyadı, veritabanında "surname" sütununa karşılık gelir
    @Column(name="surname")
    private String surname;

    // Öğrenci numarası, veritabanında "number" sütununa karşılık gelir ve benzersiz olmalıdır
    @Column(unique=true, name ="number")
    private String number;
}