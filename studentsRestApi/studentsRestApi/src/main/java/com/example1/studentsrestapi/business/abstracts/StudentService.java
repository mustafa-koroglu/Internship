package com.example1.studentsrestapi.business.abstracts;

import com.example1.studentsrestapi.entitites.concretes.Student;

import java.util.List;

public interface StudentService {
    List<Student> getAll();
    Student getById(int id);
    Student create(Student student);
    Student update(int id, Student student);
    void delete(int id);
}
