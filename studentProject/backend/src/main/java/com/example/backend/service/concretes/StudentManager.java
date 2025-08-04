package com.example.backend.service.concretes;

import com.example.backend.dataAccess.StudentsRepository;
import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.abstracts.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentManager implements StudentService {

    private final StudentsRepository studentsRepository;

    @Override
    public List<Student> findAll() {
        return studentsRepository.findAllWithLessons();
    }

    @Override
    public Student findById(int id) {
        return studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
    }

    @Override
    public Student save(Student student) {
        return studentsRepository.save(student);
    }

    @Override
    public Student update(int id, Student studentDetails) {
        Student updateStudent = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        
        updateStudent.setName(studentDetails.getName());
        updateStudent.setSurname(studentDetails.getSurname());
        updateStudent.setNumber(studentDetails.getNumber());
        updateStudent.setVerified(studentDetails.getVerified());
        updateStudent.setView(studentDetails.getView());
        
        return studentsRepository.save(updateStudent);
    }

    @Override
    public void deleteById(int id) {
        if (!studentsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Öğrenci bulunamadı: " + id);
        }
        studentsRepository.deleteById(id);
    }

    @Override
    public List<Student> search(String searchTerm) {
        return studentsRepository.findByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm);
    }

    @Override
    public List<Student> findVerifiedAndViewable() {
        return studentsRepository.findByVerifiedTrueAndViewTrue();
    }

    @Override
    public List<Student> findUnverified() {
        return studentsRepository.findByVerifiedFalse();
    }

    @Override
    public List<Student> findVerified() {
        return studentsRepository.findByVerifiedTrue();
    }

    @Override
    public List<Student> searchVerifiedAndViewable(String searchTerm) {
        return studentsRepository.findVerifiedAndViewableByNameOrSurnameOrNumberStartsWithIgnoreCase(searchTerm);
    }

    @Override
    public Student approveStudent(int id) {
        Student student = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        
        student.setVerified(true);
        
        return studentsRepository.save(student);
    }

    @Override
    public Student setStudentVisibility(int id, boolean view) {
        Student student = studentsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı: " + id));
        
        student.setView(view);
        
        return studentsRepository.save(student);
    }
}