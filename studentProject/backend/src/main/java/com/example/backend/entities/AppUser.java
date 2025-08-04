package com.example.backend.entities; // Entity paketi

import jakarta.persistence.*; // JPA anotasyonları
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Data; // Getter, setter, toString, equals, hashCode
import lombok.NoArgsConstructor; // Parametresiz constructor

@Data // Lombok data anotasyonu
@AllArgsConstructor // Tüm alanlar için constructor
@NoArgsConstructor // Parametresiz constructor
@Entity // JPA entity anotasyonu
@Table(name = "users") // Tablo adı
public class AppUser { // Kullanıcı entity sınıfı

    @Id // Primary key anotasyonu
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Otomatik artan ID
    private Long id; // Kullanıcı ID'si

    @Column(unique = true) // Benzersiz kullanıcı adı
    private String username; // Kullanıcı adı

    private String password; // Şifre

    private String role; // Kullanıcı rolü
}