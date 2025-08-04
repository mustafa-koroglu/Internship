package com.example.backend.service.concretes;

import com.example.backend.dataAccess.LessonRepository;
import com.example.backend.dataAccess.StudentsRepository;
import com.example.backend.entities.Lesson;
import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.request.CreateLessonRequest;
import com.example.backend.request.UpdateLessonRequest;
import com.example.backend.request.AssignLessonRequest;
import com.example.backend.response.LessonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonManager {
    private final LessonRepository lessonRepository;
    private final StudentsRepository studentsRepository;

    @Autowired
    public LessonManager(LessonRepository lessonRepository, StudentsRepository studentsRepository) {
        this.lessonRepository = lessonRepository;
        this.studentsRepository = studentsRepository;
    }

    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    public LessonResponse getLessonById(int id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
        return mapToLessonResponse(lesson);
    }

    public LessonResponse createLesson(CreateLessonRequest request) {
        Lesson lesson = new Lesson();
        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription());
        lesson.setAcademicYear(request.getAcademicYear());
        lesson.setTerm(request.getTerm());
        
        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(savedLesson);
    }

    public LessonResponse updateLesson(int id, UpdateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
        
        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription());
        lesson.setAcademicYear(request.getAcademicYear());
        lesson.setTerm(request.getTerm());
        
        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(updatedLesson);
    }

    public void deleteLesson(int id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }

    public void assignLessonsToStudent(AssignLessonRequest request) {
        Student student = studentsRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));
        
        List<Lesson> lessons = lessonRepository.findAllById(request.getLessonIds());
        
        // Mevcut dersleri temizle ve yeni dersleri ata
        student.getLessons().clear();
        student.getLessons().addAll(lessons);
        
        studentsRepository.save(student);
    }

    public List<LessonResponse> getStudentLessons(int studentId) {
        Student student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        return student.getLessons().stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getName(),
                lesson.getDescription(),
                lesson.getAcademicYear(),
                lesson.getTerm()
        );
    }
} 