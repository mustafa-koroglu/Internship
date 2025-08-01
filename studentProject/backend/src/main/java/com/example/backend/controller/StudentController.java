package com.example.backend.controller; // Controller paketi

import com.example.backend.entities.Student; // Öğrenci entity'si
import com.example.backend.exception.ResourceNotFoundException; // Kaynak bulunamadı hatası
import com.example.backend.request.CreateStudentRequest; // Öğrenci oluşturma isteği
import com.example.backend.request.UpdateStudentRequest; // Öğrenci güncelleme isteği
import com.example.backend.response.StudentResponse; // Öğrenci yanıt DTO'su
import com.example.backend.service.concretes.StudentManager; // Öğrenci yönetici servisi
import com.example.backend.utility.ResponseMapper; // Yanıt dönüştürücü
import lombok.RequiredArgsConstructor; // Constructor injection için
import org.springframework.http.ResponseEntity; // HTTP yanıt wrapper'ı
import org.springframework.web.bind.annotation.*; // Web controller anotasyonları

import java.util.HashMap; // Hash map
import java.util.List; // Liste
import java.util.Map; // Map
import java.util.stream.Collectors; // Stream toplama

@RestController // REST controller anotasyonu
@RequestMapping("/api/v3") // API endpoint prefix'i
@RequiredArgsConstructor // Constructor injection
public class StudentController { // Öğrenci controller sınıfı

    private final StudentManager studentManager; // Öğrenci yönetici servisi

    @GetMapping("/students") // Tüm öğrencileri getir
    public List<StudentResponse> getStudents() { // Öğrenci listesi metodu
        return studentManager.findAll().stream().map(ResponseMapper::toStudentResponse).collect(Collectors.toList()); // Tüm öğrencileri döndür
    }

    @GetMapping("/students/verified") // Onaylanmış öğrencileri getir
    public List<StudentResponse> getVerifiedStudents() { // Onaylanmış öğrenci listesi metodu
        return studentManager.findVerifiedAndViewable().stream().map(ResponseMapper::toStudentResponse).collect(Collectors.toList()); // Onaylanmış öğrencileri döndür
    }

    @GetMapping("/students/unverified") // Onaylanmamış öğrencileri getir
    public List<StudentResponse> getUnverifiedStudents() { // Onaylanmamış öğrenci listesi metodu
        return studentManager.findUnverified().stream().map(ResponseMapper::toStudentResponse).collect(Collectors.toList()); // Onaylanmamış öğrencileri döndür
    }

    @GetMapping("/students/all") // Tüm öğrencileri getir (alternatif endpoint)
    public List<StudentResponse> getAllStudents() { // Tüm öğrenci listesi metodu
        return studentManager.findAll().stream().map(ResponseMapper::toStudentResponse).collect(Collectors.toList()); // Tüm öğrencileri döndür
    }

    @GetMapping("/students/{id}") // ID ile öğrenci getir
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable int id) { // ID ile öğrenci getirme metodu
        try { // Hata yakalama bloğu
            Student student = studentManager.findById(id); // Öğrenciyi bul
            return ResponseEntity.ok(ResponseMapper.toStudentResponse(student)); // Başarılı yanıt döndür
        } catch (ResourceNotFoundException e) { // Kaynak bulunamadı hatası
            return ResponseEntity.notFound().build(); // 404 yanıtı döndür
        }
    }

    @GetMapping("/students/search") // Öğrenci ara
    public ResponseEntity<List<StudentResponse>> searchStudents(@RequestParam String q) { // Öğrenci arama metodu
        String trimmed = q.trim(); // Arama terimini temizle
        List<StudentResponse> students = studentManager.search(trimmed) // Öğrencileri ara
                .stream().map(ResponseMapper::toStudentResponse).collect(Collectors.toList()); // Sonuçları dönüştür
        return ResponseEntity.ok(students); // Başarılı yanıt döndür
    }

    @GetMapping("/students/verified/search") // Onaylanmış öğrencilerde ara
    public ResponseEntity<List<StudentResponse>> searchVerifiedStudents(@RequestParam String q) { // Onaylanmış öğrenci arama metodu
        String trimmed = q.trim(); // Arama terimini temizle
        List<StudentResponse> students = studentManager.searchVerifiedAndViewable(trimmed) // Onaylanmış öğrencilerde ara
                .stream().map(ResponseMapper::toStudentResponse).collect(Collectors.toList()); // Sonuçları dönüştür
        return ResponseEntity.ok(students); // Başarılı yanıt döndür
    }

    @PostMapping("/students") // Yeni öğrenci oluştur
    public ResponseEntity<StudentResponse> createStudent(@RequestBody CreateStudentRequest request) { // Öğrenci oluşturma metodu
        try { // Hata yakalama bloğu
            Student newStudent = new Student(); // Yeni öğrenci oluştur
            newStudent.setName(request.getName()); // İsimi ayarla
            newStudent.setSurname(request.getSurname()); // Soyismi ayarla
            newStudent.setNumber(request.getNumber()); // Numarayı ayarla
            newStudent.setVerified(true); // Onaylı olarak işaretle
            newStudent.setView(true); // Görünür olarak işaretle
            
            Student savedStudent = studentManager.save(newStudent); // Öğrenciyi kaydet
            return ResponseEntity.ok(ResponseMapper.toStudentResponse(savedStudent)); // Başarılı yanıt döndür
        } catch (Exception e) { // Genel hata yakalama
            return ResponseEntity.badRequest().build(); // 400 yanıtı döndür
        }
    }

    @PutMapping("/students/{id}") // Öğrenci güncelle
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Integer id, @RequestBody UpdateStudentRequest request) { // Öğrenci güncelleme metodu
        try { // Hata yakalama bloğu
            Student studentDetails = new Student(); // Öğrenci detayları oluştur
            studentDetails.setName(request.getName()); // İsimi ayarla
            studentDetails.setSurname(request.getSurname()); // Soyismi ayarla
            studentDetails.setNumber(request.getNumber()); // Numarayı ayarla
            studentDetails.setVerified(request.getVerified()); // Onay durumunu ayarla
            studentDetails.setView(request.getView()); // Görünürlük durumunu ayarla
            
            Student updatedStudent = studentManager.update(id, studentDetails); // Öğrenciyi güncelle
            return ResponseEntity.ok(ResponseMapper.toStudentResponse(updatedStudent)); // Başarılı yanıt döndür
        } catch (ResourceNotFoundException e) { // Kaynak bulunamadı hatası
            return ResponseEntity.notFound().build(); // 404 yanıtı döndür
        }
    }

    @PutMapping("/students/{id}/approve") // Öğrenci onayla
    public ResponseEntity<StudentResponse> approveStudent(@PathVariable Integer id) { // Öğrenci onaylama metodu
        try { // Hata yakalama bloğu
            Student approvedStudent = studentManager.approveStudent(id); // Öğrenciyi onayla
            return ResponseEntity.ok(ResponseMapper.toStudentResponse(approvedStudent)); // Başarılı yanıt döndür
        } catch (ResourceNotFoundException e) { // Kaynak bulunamadı hatası
            return ResponseEntity.notFound().build(); // 404 yanıtı döndür
        }
    }

    @PutMapping("/students/{id}/visibility") // Öğrenci görünürlüğünü ayarla
    public ResponseEntity<StudentResponse> setStudentVisibility(@PathVariable Integer id, @RequestParam boolean view) { // Görünürlük ayarlama metodu
        try { // Hata yakalama bloğu
            Student updatedStudent = studentManager.setStudentVisibility(id, view); // Görünürlüğü ayarla
            return ResponseEntity.ok(ResponseMapper.toStudentResponse(updatedStudent)); // Başarılı yanıt döndür
        } catch (ResourceNotFoundException e) { // Kaynak bulunamadı hatası
            return ResponseEntity.notFound().build(); // 404 yanıtı döndür
        }
    }

    @DeleteMapping("/students/{id}") // Öğrenci sil
    public ResponseEntity<Map<String, Boolean>> deleteStudent(@PathVariable Integer id) { // Öğrenci silme metodu
        try { // Hata yakalama bloğu
            studentManager.deleteById(id); // Öğrenciyi sil
            Map<String, Boolean> response = new HashMap<>(); // Yanıt map'i oluştur
            response.put("deleted", Boolean.TRUE); // Silme durumunu ekle
            return ResponseEntity.ok(response); // Başarılı yanıt döndür
        } catch (ResourceNotFoundException e) { // Kaynak bulunamadı hatası
            return ResponseEntity.notFound().build(); // 404 yanıtı döndür
        }
    }
} 