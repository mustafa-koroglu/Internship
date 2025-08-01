package com.example.backend.service.concretes; // Service paketi

import com.example.backend.dataAccess.StudentsRepository; // Öğrenci repository'si
import com.example.backend.entities.Student; // Öğrenci entity'si
import com.example.backend.exception.ResourceNotFoundException; // Kaynak bulunamadı hatası
import com.example.backend.service.abstracts.StudentService; // Öğrenci servis arayüzü
import lombok.RequiredArgsConstructor; // Constructor injection
import org.springframework.stereotype.Service; // Service anotasyonu
import java.util.List; // Liste

@Service // Spring service anotasyonu
@RequiredArgsConstructor // Constructor injection
public class StudentManager implements StudentService { // Öğrenci yönetici sınıfı

    private final StudentsRepository studentsRepository; // Öğrenci repository'si

    @Override // Arayüz implementasyonu
    public List<Student> findAll() { // Tüm öğrencileri getir metodu
        return studentsRepository.findAll(); // Tüm öğrencileri döndür
    }

    @Override // Arayüz implementasyonu
    public Student findById(int id) { // ID ile öğrenci bulma metodu
        return studentsRepository.findById(id) // ID ile öğrenciyi bul
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id)); // Bulunamazsa hata fırlat
    }

    @Override // Arayüz implementasyonu
    public Student save(Student student) { // Öğrenci kaydetme metodu
        return studentsRepository.save(student); // Öğrenciyi kaydet ve döndür
    }

    @Override // Arayüz implementasyonu
    public Student update(int id, Student studentDetails) { // Öğrenci güncelleme metodu
        Student updateStudent = studentsRepository.findById(id) // Güncellenecek öğrenciyi bul
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id)); // Bulunamazsa hata fırlat
        
        updateStudent.setName(studentDetails.getName()); // İsmi güncelle
        updateStudent.setSurname(studentDetails.getSurname()); // Soyismi güncelle
        updateStudent.setNumber(studentDetails.getNumber()); // Numarayı güncelle
        
        updateStudent.setVerified(studentDetails.getVerified()); // Onay durumunu güncelle
        updateStudent.setView(studentDetails.getView()); // Görünürlük durumunu güncelle
        
        return studentsRepository.save(updateStudent); // Güncellenmiş öğrenciyi kaydet ve döndür
    }

    @Override // Arayüz implementasyonu
    public void deleteById(int id) { // Öğrenci silme metodu
        if (!studentsRepository.existsById(id)) { // Öğrenci var mı kontrol et
            throw new ResourceNotFoundException("Öğrenci bulunamadı: " + id); // Yoksa hata fırlat
        }
        studentsRepository.deleteById(id); // Öğrenciyi sil
    }

    @Override // Arayüz implementasyonu
    public List<Student> search(String searchTerm) { // Öğrenci arama metodu
        return studentsRepository.findByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm); // Arama yap ve döndür
    }

    @Override // Arayüz implementasyonu
    public List<Student> findVerifiedAndViewable() { // Onaylanmış ve görünür öğrencileri getir metodu
        return studentsRepository.findByVerifiedTrueAndViewTrue(); // Onaylanmış ve görünür öğrencileri döndür
    }

    @Override // Arayüz implementasyonu
    public List<Student> findUnverified() { // Onaylanmamış öğrencileri getir metodu
        return studentsRepository.findByVerifiedFalse(); // Onaylanmamış öğrencileri döndür
    }

    @Override // Arayüz implementasyonu
    public List<Student> findVerified() { // Onaylanmış öğrencileri getir metodu
        return studentsRepository.findByVerifiedTrue(); // Onaylanmış öğrencileri döndür
    }

    @Override // Arayüz implementasyonu
    public List<Student> searchVerifiedAndViewable(String searchTerm) { // Onaylanmış öğrencilerde arama metodu
        return studentsRepository.findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm); // Arama yap ve döndür
    }

    @Override // Arayüz implementasyonu
    public Student approveStudent(int id) { // Öğrenci onaylama metodu
        Student student = studentsRepository.findById(id) // Onaylanacak öğrenciyi bul
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id)); // Bulunamazsa hata fırlat
        
        student.setVerified(true); // Öğrenciyi onayla
        
        return studentsRepository.save(student); // Onaylanmış öğrenciyi kaydet ve döndür
    }

    @Override // Arayüz implementasyonu
    public Student setStudentVisibility(int id, boolean view) { // Öğrenci görünürlük ayarlama metodu
        Student student = studentsRepository.findById(id) // Görünürlüğü değiştirilecek öğrenciyi bul
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id)); // Bulunamazsa hata fırlat
        
        student.setView(view); // Görünürlük durumunu ayarla
        
        return studentsRepository.save(student); // Güncellenmiş öğrenciyi kaydet ve döndür
    }
}