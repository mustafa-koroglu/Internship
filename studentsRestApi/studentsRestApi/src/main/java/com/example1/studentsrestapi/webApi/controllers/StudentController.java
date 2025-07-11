package com.example1.studentsrestapi.webApi.controllers;

import com.example1.studentsrestapi.business.concretes.StudentManager;
import com.example1.studentsrestapi.entitites.concretes.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/")
public class StudentController {

    @Autowired
    private StudentManager studentManager;

    // Tüm öğrencileri getir
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentManager.getAll();
    }

    // Yeni öğrenci oluştur
    @PostMapping("/students")
    public Student createStudent(@RequestBody Student student) {
        return studentManager.create(student);
    }

    // ID'ye göre öğrenci getir
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        Student student = studentManager.getById(id);
        return ResponseEntity.ok(student);
    }

    // Öğrenciyi güncelle
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        Student updatedStudent = studentManager.update(id, studentDetails);
        return ResponseEntity.ok(updatedStudent);
    }

    // Öğrenciyi sil
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteStudent(@PathVariable Integer id) {
        studentManager.delete(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
