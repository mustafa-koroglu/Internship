package com.example.clickhouse.service;

import com.example.clickhouse.entity.Teacher;
import com.example.clickhouse.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher teacher) {
        Optional<Teacher> existingTeacher = teacherRepository.findById(id);
        if (existingTeacher.isPresent()) {
            teacher.setId(id);
            return teacherRepository.save(teacher);
        }
        throw new RuntimeException("Ogretmen bulunamadi: " + id);
    }

    public void deleteTeacher(Long id) {
        Optional<Teacher> existingTeacher = teacherRepository.findById(id);
        if (existingTeacher.isPresent()) {
            teacherRepository.deleteById(id);
        } else {
            throw new RuntimeException("Ogretmen bulunamadi: " + id);
        }
    }
}
