
package com.example.backend.service.concretes; // Service paketi

import com.example.backend.dataAccess.AppUserRepository; // Kullanıcı repository'si
import com.example.backend.entities.AppUser; // Uygulama kullanıcı entity'si
import org.springframework.beans.factory.annotation.Autowired; // Autowired anotasyonu
import org.springframework.security.core.userdetails.User; // Spring Security kullanıcı sınıfı
import org.springframework.security.core.userdetails.UserDetails; // Kullanıcı detayları arayüzü
import org.springframework.security.core.userdetails.UserDetailsService; // Kullanıcı detay servisi arayüzü
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Kullanıcı adı bulunamadı hatası
import org.springframework.stereotype.Service; // Service anotasyonu

@Service // Spring service anotasyonu
public class AppUserDetailsService implements UserDetailsService { // Kullanıcı detay servisi

    @Autowired // Bağımlılık enjeksiyonu
    private AppUserRepository appUserRepository; // Kullanıcı repository'si

    @Override // Arayüz implementasyonu
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // Kullanıcı adı ile kullanıcı yükleme metodu
        AppUser appUser = appUserRepository.findByUsername(username) // Kullanıcıyı veritabanında bul
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username)); // Bulunamazsa hata fırlat
        return User.builder() // Spring Security User builder'ı başlat
                .username(appUser.getUsername()) // Kullanıcı adını ayarla
                .password(appUser.getPassword()) // Şifreyi ayarla
                .roles(appUser.getRole()) // Rolü ayarla
                .build(); // User nesnesini oluştur ve döndür
    }
}