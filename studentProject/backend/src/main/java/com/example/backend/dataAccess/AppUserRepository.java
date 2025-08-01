package com.example.backend.dataAccess;

import com.example.backend.entities.AppUser; // Uygulama kullanıcı entity'si
import org.springframework.data.jpa.repository.JpaRepository; // JPA repository arayüzü
import java.util.Optional; // Optional sınıfı

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}