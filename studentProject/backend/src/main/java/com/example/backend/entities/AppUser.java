package com.example.backend.entities;

import jakarta.persistence.*; // JPA için gerekli anotasyonlar
import lombok.AllArgsConstructor; // Tüm alanlar için constructor oluşturur
import lombok.Data; // Getter, setter, toString, equals, hashCode otomatik ekler
import lombok.NoArgsConstructor; // Parametresiz constructor oluşturur


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Kullanıcı adı, veritabanında benzersiz olmalı
     */
    @Column(unique = true)
    private String username;

    /**
     * Kullanıcı şifresi (hashlenmiş olarak tutulur)
     */
    private String password;

    /**
     * Kullanıcı rolü (ör: ADMIN, USER)
     */
    private String role;
}