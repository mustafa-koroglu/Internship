package com.example.backend.service.abstracts;

import com.example.backend.entities.Student;
import java.util.List;

/**
 * Öğrenci onay sistemi için eklenen metodlar:
 * - findVerifiedAndViewable(): Kullanıcılar için onaylanmış öğrenciler
 * - findUnverified(): Admin için onaylanmamış öğrenciler
 * - findVerified(): Admin için onaylanmış öğrenciler
 * - searchVerifiedAndViewable(): Kullanıcılar için arama
 * - approveStudent(): Öğrenci onaylama
 * - setStudentVisibility(): Görünürlük kontrolü
 */
public interface StudentService {

    List<Student> findAll();

    Student findById(int id);

    Student save(Student student);

    Student update(int id, Student student);

    void deleteById(int id);

    List<Student> search(String searchTerm);
    
    // ========== ÖĞRENCİ ONAY SİSTEMİ METODLARI ==========
    
    /**
     * Onaylanmış ve görünür öğrencileri getirir (kullanıcılar için)
     * 
     * Bu metod, kullanıcı arayüzünde gösterilecek öğrencileri döner.
     * Sadece admin tarafından onaylanmış VE görünürlüğü açık olan
     * öğrenciler kullanıcılara gösterilir.
     * 
     * İş kuralları:
     * - verified = true (admin onaylamış)
     * - view = true (kullanıcılara gösterilir)
     * 
     * @return Onaylanmış ve görünür öğrenci listesi
     */
    List<Student> findVerifiedAndViewable();
    
    /**
     * Onaylanmamış öğrencileri getirir (admin için)
     * 
     * Bu metod, henüz admin tarafından onaylanmamış öğrencileri döner.
     * Admin panelinde bu öğrenciler "Onaysız" olarak görünür ve
     * onaylama butonu ile onaylanabilir.
     * 
     * İş kuralları:
     * - verified = false (henüz onaylanmamış)
     * - Genellikle CSV'den okunan öğrenciler
     * 
     * @return Onaylanmamış öğrenci listesi
     */
    List<Student> findUnverified();
    
    /**
     * Onaylanmış öğrencileri getirir (admin için)
     * 
     * Bu metod, admin tarafından onaylanmış tüm öğrencileri döner.
     * Görünürlük durumuna bakmaksızın sadece onay durumunu kontrol eder.
     * 
     * İş kuralları:
     * - verified = true (admin onaylamış)
     * - view durumu önemli değil
     * 
     * @return Onaylanmış öğrenci listesi
     */
    List<Student> findVerified();
    
    /**
     * Onaylanmış ve görünür öğrencilerde arama yapar (kullanıcılar için)
     * 
     * Bu metod, kullanıcıların arama yapması için kullanılır.
     * Sadece onaylanmış VE görünür öğrencilerde arama yapar.
     * 
     * Arama kriterleri:
     * - İsim, soyisim veya numara ile başlayan eşleşmeler
     * - Büyük/küçük harf duyarsız
     * 
     * İş kuralları:
     * - verified = true AND view = true
     * - Kullanıcılar sadece bu öğrencileri arayabilir
     * 
     * @param searchTerm Arama terimi
     * @return Filtrelenmiş öğrenci listesi
     */
    List<Student> searchVerifiedAndViewable(String searchTerm);
    
    /**
     * Öğrenciyi onaylar (verified = true yapar)
     * 
     * Bu metod, admin tarafından öğrenci onaylama işlemini gerçekleştirir.
     * Onaylanan öğrenci artık kullanıcılara gösterilebilir hale gelir.
     * 
     * İş kuralları:
     * - Öğrenci ID'si geçerli olmalı
     * - verified = true yapılır
     * - view durumu değişmez
     * 
     * @param id Öğrenci ID
     * @return Onaylanan öğrenci
     */
    Student approveStudent(int id);
    
    /**
     * Öğrencinin görünürlük durumunu ayarlar
     * 
     * Bu metod, admin tarafından öğrencinin kullanıcılara gösterilip
     * gösterilmeyeceğini kontrol etmek için kullanılır.
     * 
     * İş kuralları:
     * - Öğrenci ID'si geçerli olmalı
     * - view = true/false yapılır
     * - verified durumu değişmez
     * 
     * @param id Öğrenci ID
     * @param view Görünürlük durumu (true: görünür, false: gizli)
     * @return Güncellenen öğrenci
     */
    Student setStudentVisibility(int id, boolean view);
}