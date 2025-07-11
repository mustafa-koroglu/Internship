package com.example1.studentsrestapi.business.concretes;

import com.example1.studentsrestapi.business.abstracts.StudentService;
import com.example1.studentsrestapi.dataAccess.StudentsRepository;
import com.example1.studentsrestapi.entitites.concretes.Student;
import com.example1.studentsrestapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentManager implements StudentService {

    @Autowired
    private StudentsRepository studentsRepository;

    @Override
    public List<Student> getAll() {
        return studentsRepository.findAll();
    }
    @Override
    public Student getById(int id) {
        return studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    public Student create(Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public Student update(int id, Student studentDetails) {
        Student student = getById(id);
        student.setName(studentDetails.getName());
        student.setSurname(studentDetails.getSurname());
        student.setNumber(studentDetails.getNumber());
        return studentsRepository.save(student);
    }

    @Override
    public void delete(int id) {
        Student student = getById(id);
        studentsRepository.delete(student);
    }
}
