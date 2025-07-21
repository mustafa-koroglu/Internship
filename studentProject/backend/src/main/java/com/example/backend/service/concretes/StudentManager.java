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
 */
@Service
public class StudentManager implements StudentService {

    // Öğrenci veritabanı işlemleri için repository bağımlılığı
    @Autowired
    private StudentsRepository studentsRepository;


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
     * @param searchTerm Arama terimi
     * @return Öğrenci listesi
     */
    @Override
    public List<Student> search(String searchTerm) {
        // Arama terimi ile isim, soyisim veya numarası başlayan öğrencileri döner
        return studentsRepository.findByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm);
    }
}