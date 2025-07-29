// Bu sınıf, öğrenci işlemlerinin iş mantığını yöneten servis katmanıdır.
// Öğrenci ile ilgili CRUD ve arama işlemlerini gerçekleştirir.
package com.example.backend.service.concretes;

import com.example.backend.dataAccess.StudentsRepository;
import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.abstracts.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Öğrenci işlemlerinin iş mantığını yöneten servis sınıfı.
 * Veritabanı işlemleri ve iş kuralları burada toplanır.
 * 
 * Öğrenci onay sistemi için eklenen özellikler:
 * - Onaylanmış öğrenci filtreleme
 * - Onaylanmamış öğrenci yönetimi
 * - Görünürlük kontrolü
 * - Admin onaylama işlemleri
 * 
 * İş kuralları:
 * - CSV'den okunan öğrenciler: verified=false, view=false
 * - Manuel eklenen öğrenciler: verified=true, view=true
 * - Sadece verified=true VE view=true olan öğrenciler kullanıcılara gösterilir
 */
@Service
public class StudentManager implements StudentService {

    // Öğrenci veritabanı işlemleri için repository bağımlılığı
    @Autowired
    private StudentsRepository studentsRepository;

    // ========== TEMEL CRUD İŞLEMLERİ ==========

    // Tüm öğrencileri getirir (veritabanındaki tüm öğrencileri döner)
    @Override
    public List<Student> findAll() {
        return studentsRepository.findAll();
    }

    /**
     * ID'ye göre öğrenci getirir, yoksa exception fırlatır
     * @param id Öğrenci ID
     * @return Öğrenci
     */
    @Override
    public Student findById(int id) {
        return studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
    }

    /**
     * Yeni öğrenci kaydeder (veritabanına ekler)
     * @param student Yeni öğrenci
     * @return Kaydedilen öğrenci
     */
    @Override
    public Student save(Student student) {
        return studentsRepository.save(student);
    }

    /**
     * Öğrenci günceller (id ile bulup, yeni bilgileriyle günceller)
     * 
     * Bu metod, öğrencinin tüm alanlarını günceller:
     * - name, surname, number: Temel bilgiler
     * - verified, view: Onay ve görünürlük durumu
     * 
     * @param id Öğrenci ID
     * @param studentDetails Güncellenecek bilgiler
     * @return Güncellenen öğrenci
     */
    @Override
    public Student update(int id, Student studentDetails) {
        // Güncellenecek öğrenciyi bulur, yoksa exception fırlatır
        Student updateStudent = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        
        // Öğrenci bilgilerini günceller
        updateStudent.setName(studentDetails.getName());
        updateStudent.setSurname(studentDetails.getSurname());
        updateStudent.setNumber(studentDetails.getNumber());
        
        // Öğrenci onay sistemi alanlarını günceller
        updateStudent.setVerified(studentDetails.getVerified());
        updateStudent.setView(studentDetails.getView());
        
        // Güncellenmiş öğrenciyi kaydeder ve döner
        return studentsRepository.save(updateStudent);
    }

    /**
     * ID'ye göre öğrenci siler, yoksa exception fırlatır
     * @param id Öğrenci ID
     */
    @Override
    public void deleteById(int id) {
        // Silinecek öğrencinin varlığını kontrol eder
        if (!studentsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Öğrenci bulunamadı: " + id);
        }
        studentsRepository.deleteById(id);
    }

    /**
     * İsim, soyisim veya numaraya göre arama yapar (başlangıcı eşleşenler)
     * Bu metod admin için tüm öğrencilerde arama yapar.
     * 
     * @param searchTerm Arama terimi
     * @return Öğrenci listesi
     */
    @Override
    public List<Student> search(String searchTerm) {
        // Arama terimi ile isim, soyisim veya numarası başlayan öğrencileri döner
        return studentsRepository.findByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm);
    }

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
    @Override
    public List<Student> findVerifiedAndViewable() {
        return studentsRepository.findByVerifiedTrueAndViewTrue();
    }

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
    @Override
    public List<Student> findUnverified() {
        return studentsRepository.findByVerifiedFalse();
    }

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
    @Override
    public List<Student> findVerified() {
        return studentsRepository.findByVerifiedTrue();
    }

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
    @Override
    public List<Student> searchVerifiedAndViewable(String searchTerm) {
        return studentsRepository.findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm);
    }

    /**
     * Öğrenciyi onaylar (verified = true yapar)
     * 
     * Bu metod, admin tarafından öğrenci onaylama işlemini gerçekleştirir.
     * Onaylanan öğrenci artık kullanıcılara gösterilebilir hale gelir.
     * 
     * İş kuralları:
     * - Öğrenci ID'si geçerli olmalı
     * - verified = true yapılır
     * - view durumu değişmez (admin manuel olarak ayarlayabilir)
     * 
     * @param id Öğrenci ID
     * @return Onaylanan öğrenci
     */
    @Override
    public Student approveStudent(int id) {
        // Onaylanacak öğrenciyi bulur, yoksa exception fırlatır
        Student student = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        
        // Öğrenciyi onaylar
        student.setVerified(true);
        
        // Onaylanan öğrenciyi kaydeder ve döner
        return studentsRepository.save(student);
    }

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
    @Override
    public Student setStudentVisibility(int id, boolean view) {
        // Görünürlüğü değiştirilecek öğrenciyi bulur, yoksa exception fırlatır
        Student student = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        
        // Öğrencinin görünürlük durumunu ayarlar
        student.setView(view);
        
        // Güncellenen öğrenciyi kaydeder ve döner
        return studentsRepository.save(student);
    }
}