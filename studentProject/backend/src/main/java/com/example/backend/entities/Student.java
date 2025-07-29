
package com.example.backend.entities;

import jakarta.persistence.*; // JPA için gerekli anotasyonlar
import lombok.AllArgsConstructor; // Tüm alanlar için constructor oluşturur
import lombok.Data; // Getter, setter, toString, equals, hashCode otomatik ekler
import lombok.NoArgsConstructor; // Parametresiz constructor oluşturur

/**
 * Öğrenci entity sınıfı.
 * Veritabanındaki 'students' tablosunu temsil eder.
 * <p>
 * Öğrenci onay sistemi için eklenen alanlar:
 * - verified: Öğrencinin admin tarafından onaylanıp onaylanmadığını belirtir
 * - view: Öğrencinin kullanıcı arayüzünde görünür olup olmadığını belirtir
 * <p>
 * İş kuralları:
 * - CSV'den okunan öğrenciler: verified=false, view=false (onay bekler)
 * - Manuel eklenen öğrenciler: verified=true, view=true (otomatik onaylı)
 * - Sadece verified=true VE view=true olan öğrenciler kullanıcılara gösterilir
 */
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