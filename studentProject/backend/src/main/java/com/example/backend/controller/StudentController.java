package com.example.backend.controller;


import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.concretes.StudentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Öğrenci ile ilgili HTTP isteklerini yöneten controller sınıfı
@RestController
@RequestMapping("/api/v3")
public class StudentController {

    // Student işlemlerini yöneten servis
    @Autowired
    private StudentManager studentManager;

    // Tüm öğrencileri getirir
    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentManager.findAll();
    }

    // ID'ye göre öğrenci getirir
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        try {
            Student student = studentManager.findById(id);
            return ResponseEntity.ok(student);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Yeni öğrenci ekler
    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        return studentManager.save(student);
    }

    // Öğrenci günceller
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        try {
            Student updatedStudent = studentManager.update(id, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Öğrenci siler
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteStudent(@PathVariable Integer id) {
        try {
            studentManager.deleteById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Öğrenci adına göre arama
    @GetMapping("/students/search/name")
    public ResponseEntity<List<Student>> searchStudentsByName(@RequestParam String name) {
        List<Student> students = studentManager.searchByName(name);
        return ResponseEntity.ok(students);
    }

    // Öğrenci adı veya soyadına göre arama
    @GetMapping("/students/search")
    public ResponseEntity<List<Student>> searchStudentsByNameOrSurname(@RequestParam String q) {
        String trimmed = q.trim();
        List<Student> students = studentManager.searchByNameOrSurname(trimmed);
        return ResponseEntity.ok(students);
    }

    // Öğrenci numarasına göre arama
    @GetMapping("/students/search/number")
    public ResponseEntity<List<Student>> searchStudentsByNumber(@RequestParam String number) {
        String trimmed = number.trim();
        List<Student> students = studentManager.searchByNumber(trimmed);
        return ResponseEntity.ok(students);
    }
}
