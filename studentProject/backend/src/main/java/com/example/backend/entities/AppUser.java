// Bu dosya, kullanıcıları temsil eden AppUser entity'sini tanımlar.
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
// Bu sınıfın bir JPA entity olduğunu belirtir
@Entity
// Entity'nin veritabanında "users" tablosuna karşılık geldiğini belirtir
@Table(name = "users")
public class AppUser {
    // Birincil anahtar (id) alanı, otomatik olarak artan değer
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kullanıcı adı, veritabanında benzersiz olmalı
    @Column(unique = true)
    private String username;

    // Kullanıcı şifresi
    private String password;

    // Kullanıcı rolü (ör: ADMIN, USER)
    private String role;

    // Lombok ile getter ve setter'lar otomatik olarak eklenir
}