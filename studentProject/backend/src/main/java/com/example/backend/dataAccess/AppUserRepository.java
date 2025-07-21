
package com.example.backend.dataAccess;

import com.example.backend.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    /**
     * Kullanıcı adını parametre alarak kullanıcıyı opsiyonel olarak döndüren metot
     * @param username Kullanıcı adı
     * @return Optional<AppUser>
     */
    Optional<AppUser> findByUsername(String username);
}