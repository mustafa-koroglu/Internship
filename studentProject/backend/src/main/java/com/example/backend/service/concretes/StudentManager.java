// Bu dosya, öğrenci işlemlerinin iş mantığını yöneten servis sınıfını tanımlar.
package com.example.backend.service.concretes;

// Öğrenci repository'sini import eder
import com.example.backend.dataAccess.StudentsRepository;
// Öğrenci entity'sini import eder
import com.example.backend.entities.Student;
// Kaynak bulunamadığında fırlatılan özel exception sınıfını import eder
import com.example.backend.exception.ResourceNotFoundException;
// Öğrenci servis arayüzünü import eder
import com.example.backend.service.abstracts.StudentService;
// Spring'in dependency injection ve servis anotasyonunu import eder
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Liste veri tipi için import
import java.util.List;

// Bu sınıf bir servis bileşenidir (Spring tarafından yönetilir)
@Service
public class StudentManager implements StudentService {

    // Öğrenci veritabanı işlemleri için repository
    @Autowired
    private StudentsRepository studentsRepository;

    // Tüm öğrencileri getirir
    @Override
    public List<Student> findAll() {
        return studentsRepository.findAll();
    }

    // ID'ye göre öğrenci getirir, yoksa exception fırlatır
    @Override
    public Student findById(int id) {
        return studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
    }

    // Yeni öğrenci kaydeder
    @Override
    public Student save(Student student) {
        return studentsRepository.save(student);
    }

    // Öğrenci günceller
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

    // ID'ye göre öğrenci siler, yoksa exception fırlatır
    @Override
    public void deleteById(int id) {
        if (!studentsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Öğrenci bulunamadı: " + id);
        }
        studentsRepository.deleteById(id);
    }
    @Override
public List<Student> searchByName(String name) {
    return studentsRepository.findByNameContainingIgnoreCase(name);
}

@Override
public List<Student> searchByNameOrSurname(String searchTerm) {
    return studentsRepository.findByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm);
}

@Override
public List<Student> searchByNumber(String number) {
    return studentsRepository.findByNumberStartingWith(number);
}
}