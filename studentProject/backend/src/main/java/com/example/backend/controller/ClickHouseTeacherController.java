package com.example.backend.controller;

import com.example.backend.entities.Teacher;
import com.example.backend.service.concretes.ClickHouseTeacherManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
public class ClickHouseTeacherController {

    @Autowired
    private ClickHouseTeacherManager manager;

    @PostMapping
    public ResponseEntity<String> addTeacher(@RequestBody Teacher teacher) {
        manager.addTeacher(teacher);
        return ResponseEntity.ok("Öğretmen başarıyla eklendi");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeTeacher(@PathVariable int id) {
        manager.removeTeacher(id);
        return ResponseEntity.ok("Öğretmen başarıyla silindi");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTeacher(@PathVariable int id, @RequestBody Teacher teacher) {
        teacher.setId(id);
        manager.updateTeacher(teacher);
        return ResponseEntity.ok("Öğretmen başarıyla güncellendi");
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = manager.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable int id) {
        Teacher teacher = manager.getTeacherById(id);
        if (teacher != null) {
            return ResponseEntity.ok(teacher);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}