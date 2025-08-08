package com.example.backend.service.concretes;

import com.example.backend.dataAccess.ClickHouseTeacherRepository;
import com.example.backend.entities.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClickHouseTeacherManager {

    private final ClickHouseTeacherRepository repository;

    public List<Teacher> getAllTeachers() {
        return repository.getAllTeachers();
    }

    public Teacher getTeacherById(int id) {
        return repository.getTeacherById(id);
    }

    public void addTeacher(Teacher teacher) {
        repository.addTeacher(teacher);
    }

    public void removeTeacher(int id) {
        repository.removeTeacher(id);
    }

    public void updateTeacher(Teacher teacher) {
        repository.updateTeacher(teacher);
    }


}