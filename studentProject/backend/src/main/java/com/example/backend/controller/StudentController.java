
package com.example.backend.controller;

import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.request.CreateStudentRequest;
import com.example.backend.request.UpdateStudentRequest;
import com.example.backend.response.StudentResponse;
import com.example.backend.service.concretes.StudentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Öğrenci işlemlerini yöneten REST controller.
 * /api/v3/students: Listeleme, ekleme, güncelleme, silme, arama işlemleri
 */
@RestController
@RequestMapping("/api/v3")
public class StudentController {

    // Öğrenci işlemlerini yöneten servis
    @Autowired
    private StudentManager studentManager;

    /**
     * Entity'den Response DTO'ya dönüşüm yapan yardımcı metot
     */
    private StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setSurname(student.getSurname());
        response.setNumber(student.getNumber());
        return response;
    }

    /**
     * Tüm öğrencileri getirir (GET /students)
     * @return Öğrenci listesi
     */
    @GetMapping("/students")
    public List<StudentResponse> getStudents() {
        return studentManager.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * ID'ye göre öğrenci getirir (GET /students/{id})
     * @param id Öğrenci ID
     * @return Öğrenci veya 404
     */
    @GetMapping("/students/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable int id) {
        try {
            Student student = studentManager.findById(id);
            return ResponseEntity.ok(toResponse(student));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Öğrenci adı, soyadı veya numarasına göre arama yapar (GET /students/search?q=...)
     */
    @GetMapping("/students/search")
    public ResponseEntity<List<StudentResponse>> searchStudents(@RequestParam String q) {
        String trimmed = q.trim();
        List<StudentResponse> students = studentManager.search(trimmed)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    /**
     * Yeni öğrenci ekler (POST /students)
     * @param request Yeni öğrenci bilgileri
     * @return Eklenen öğrenci
     */
    @PostMapping("/students")
    public StudentResponse addStudent(@RequestBody CreateStudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setSurname(request.getSurname());
        student.setNumber(request.getNumber());
        Student saved = studentManager.save(student);
        return toResponse(saved);
    }

    /**
     * Öğrenci günceller (PUT /students/{id})
     * @param id Öğrenci ID
     * @param request Güncellenecek bilgiler
     * @return Güncellenen öğrenci veya 404
     */
    @PutMapping("/students/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Integer id, @RequestBody UpdateStudentRequest request) {
        try {
            Student studentDetails = new Student();
            studentDetails.setName(request.getName());
            studentDetails.setSurname(request.getSurname());
            studentDetails.setNumber(request.getNumber());
            Student updatedStudent = studentManager.update(id, studentDetails);
            return ResponseEntity.ok(toResponse(updatedStudent));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Öğrenci siler (DELETE /students/{id})
     * @param id Öğrenci ID
     * @return Silme sonucu
     */
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
}