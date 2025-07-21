package com.example.backend.service.abstracts;

import com.example.backend.entities.Student;
import java.util.List;

/**
 * Öğrenci ile ilgili servis işlemlerini tanımlayan arayüz.
 * Uygulamanın iş mantığı katmanında öğrenci işlemlerinin sözleşmesini belirler.
 */
public interface StudentService {
    List<Student> findAll();
    Student findById(int id);
    Student save(Student student);
    Student update(int id, Student student);
    void deleteById(int id);
    List<Student> search(String searchTerm);
}