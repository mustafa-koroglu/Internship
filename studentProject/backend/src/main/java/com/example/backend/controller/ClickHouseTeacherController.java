package com.example.backend.controller;

import java.util.Map;
import java.util.List;
import com.example.backend.service.concretes.ClickHouseTeacherManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
public class ClickHouseTeacherController {

    @Autowired
    private ClickHouseTeacherManager manager;


    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllTeachers() {
        List<Map<String, Object>> teachers = manager.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTeacherById(@PathVariable Long id) {
        Map<String, Object> teacher = manager.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    @PostMapping
    public ResponseEntity<String> addTeacher(@RequestBody Map<String, Object> teacherData) {
        manager.addTeacher(teacherData);
        return ResponseEntity.ok("Öğretmen başarıyla eklendi");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeTeacher(@PathVariable Long id) {
        manager.removeTeacher(id);
        return ResponseEntity.ok("Öğretmen başarıyla silindi");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTeacher(@PathVariable Long id, @RequestBody Map<String, Object> teacherData) {
        manager.updateTeacher(id, teacherData);
        return ResponseEntity.ok("Öğretmen başarıyla güncellendi");
    }

}