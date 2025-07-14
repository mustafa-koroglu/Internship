// Bu dosya, AppUser entity'si için veri tabanı işlemlerini gerçekleştiren repository arayüzünü tanımlar.
package com.example.backend.dataAccess;

// AppUser entity'sini import eder
import com.example.backend.entities.AppUser;
// Spring Data JPA'nın JpaRepository arayüzünü import eder
import org.springframework.data.jpa.repository.JpaRepository;
// Optional tipini import eder, null dönebilecek sorgular için kullanılır
import java.util.Optional;

// AppUser entity'si için repository arayüzü, JpaRepository'den kalıtım alır
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Kullanıcı adını parametre alarak kullanıcıyı opsiyonel olarak döndüren metot
    Optional<AppUser> findByUsername(String username);
}