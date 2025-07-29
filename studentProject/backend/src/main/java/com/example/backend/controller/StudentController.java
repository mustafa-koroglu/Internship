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
 */
@RestController
@RequestMapping("/api/v3")
public class StudentController {

    @Autowired
    private StudentManager studentManager;

    private StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setName(student.getName());
        response.setSurname(student.getSurname());
        response.setNumber(student.getNumber());
        response.setVerified(student.getVerified());
        response.setView(student.getView());
        return response;
    }

    @GetMapping("/students")
    public List<StudentResponse> getStudents() {
        System.out.println("GET /students endpoint called");
        return studentManager.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/students/verified")
    public List<StudentResponse> getVerifiedStudents() {
        return studentManager.findVerifiedAndViewable().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/students/unverified")
    public List<StudentResponse> getUnverifiedStudents() {
        return studentManager.findUnverified().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/students/all")
    public List<StudentResponse> getAllStudents() {
        return studentManager.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable int id) {
        try {
            Student student = studentManager.findById(id);
            return ResponseEntity.ok(toResponse(student));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/students/search")
    public ResponseEntity<List<StudentResponse>> searchStudents(@RequestParam String q) {
        String trimmed = q.trim();
        List<StudentResponse> students = studentManager.search(trimmed)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/verified/search")
    public ResponseEntity<List<StudentResponse>> searchVerifiedStudents(@RequestParam String q) {
        String trimmed = q.trim();
        List<StudentResponse> students = studentManager.searchVerifiedAndViewable(trimmed)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @PostMapping("/students")
    public ResponseEntity<StudentResponse> createStudent(@RequestBody CreateStudentRequest request) {
        try {
            Student newStudent = new Student();
            newStudent.setName(request.getName());
            newStudent.setSurname(request.getSurname());
            newStudent.setNumber(request.getNumber());
            newStudent.setVerified(true);
            newStudent.setView(true);
            
            Student savedStudent = studentManager.save(newStudent);
            return ResponseEntity.ok(toResponse(savedStudent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Integer id, @RequestBody UpdateStudentRequest request) {
        try {
            Student studentDetails = new Student();
            studentDetails.setName(request.getName());
            studentDetails.setSurname(request.getSurname());
            studentDetails.setNumber(request.getNumber());
            studentDetails.setVerified(request.getVerified());
            studentDetails.setView(request.getView());
            
            Student updatedStudent = studentManager.update(id, studentDetails);
            return ResponseEntity.ok(toResponse(updatedStudent));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/students/{id}/approve")
    public ResponseEntity<StudentResponse> approveStudent(@PathVariable Integer id) {
        try {
            Student approvedStudent = studentManager.approveStudent(id);
            return ResponseEntity.ok(toResponse(approvedStudent));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/students/{id}/visibility")
    public ResponseEntity<StudentResponse> setStudentVisibility(@PathVariable Integer id, @RequestParam boolean view) {
        try {
            Student updatedStudent = studentManager.setStudentVisibility(id, view);
            return ResponseEntity.ok(toResponse(updatedStudent));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

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