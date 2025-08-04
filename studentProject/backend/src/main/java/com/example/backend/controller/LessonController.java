package com.example.backend.controller;

import com.example.backend.request.CreateLessonRequest;
import com.example.backend.request.UpdateLessonRequest;
import com.example.backend.request.AssignLessonRequest;
import com.example.backend.response.LessonResponse;
import com.example.backend.service.concretes.LessonManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LessonController {
    private final LessonManager lessonManager;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        List<LessonResponse> lessons = lessonManager.getAllLessons();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable int id) {
        LessonResponse lesson = lessonManager.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody CreateLessonRequest request) {
        LessonResponse createdLesson = lessonManager.createLesson(request);
        return ResponseEntity.ok(createdLesson);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable int id, @RequestBody UpdateLessonRequest request) {
        LessonResponse updatedLesson = lessonManager.updateLesson(id, request);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable int id) {
        lessonManager.deleteLesson(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignLessonsToStudent(@RequestBody AssignLessonRequest request) {
        lessonManager.assignLessonsToStudent(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LessonResponse>> getStudentLessons(@PathVariable int studentId) {
        List<LessonResponse> lessons = lessonManager.getStudentLessons(studentId);
        return ResponseEntity.ok(lessons);
    }
} 