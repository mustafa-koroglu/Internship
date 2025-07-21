
package com.example.backend.service.concretes;

import com.example.backend.dataAccess.AppUserRepository;
import com.example.backend.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security için kullanıcı doğrulama işlemlerini yöneten servis.
 * Veritabanından kullanıcıyı bulur ve UserDetails nesnesi döner.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    /** Kullanıcı veritabanı işlemleri için repository */
    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Kullanıcı adını alıp, UserDetails nesnesi döndüren metot (Spring Security için zorunlu)
     * @param username Kullanıcı adı
     * @return UserDetails nesnesi
     * @throws UsernameNotFoundException Kullanıcı bulunamazsa fırlatılır
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Kullanıcıyı veritabanında bulur, yoksa exception fırlatır
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
        // UserDetails nesnesi oluşturur ve döner (Spring Security için)
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
    }
}