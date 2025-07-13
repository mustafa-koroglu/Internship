package com.example.backend.service.concretes;

import com.example.backend.dataAccess.StudentsRepository;
import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.abstracts.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// StudentService arayüzünü uygulayan, öğrenci işlemlerinin iş mantığını yöneten servis sınıfı
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

    // ID'ye göre öğrenci getirir
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
        Student updateStudent = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        updateStudent.setName(studentDetails.getName());
        updateStudent.setSurname(studentDetails.getSurname());
        updateStudent.setNumber(studentDetails.getNumber());
        return studentsRepository.save(updateStudent);
    }

    // ID'ye göre öğrenci siler
    @Override
    public void deleteById(int id) {
        if (!studentsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Öğrenci bulunamadı: " + id);
        }
        studentsRepository.deleteById(id);
    }

    // İsme göre arama yapar
    @Override
    public List<Student> searchByName(String name) {
        return studentsRepository.findByNameContainingIgnoreCase(name);
    }

    // İsim veya soyisme BAŞLAYAN arama yapar
    @Override
    public List<Student> searchByNameOrSurname(String searchTerm) {
        return studentsRepository.findByNameOrSurnameStartsWithIgnoreCase(searchTerm);
    }

    // Numaraya göre arama yapar
    @Override
    public List<Student> searchByNumber(String number) {
        return studentsRepository.findByNumberStartingWith(number);
    }
}
