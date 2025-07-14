// Bu dosya, kullanıcı doğrulama işlemleri için UserDetailsService implementasyonunu sağlar.
package com.example.backend.service.concretes;

// Kullanıcı repository'sini import eder
import com.example.backend.dataAccess.AppUserRepository;
// Kullanıcı entity'sini import eder
import com.example.backend.entities.AppUser;
// Spring'in dependency injection ve güvenlik sınıflarını import eder
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Bu sınıf bir servis bileşenidir (Spring tarafından yönetilir)
@Service
public class AppUserDetailsService implements UserDetailsService {

    // Kullanıcı veritabanı işlemleri için repository
    @Autowired
    private AppUserRepository appUserRepository;

    // Kullanıcı adını alıp, UserDetails nesnesi döndüren metot (Spring Security için zorunlu)
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