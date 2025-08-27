package com.example.backend.service.concretes;

import com.example.backend.dataAccess.StudentsRepository;
import com.example.backend.entities.Student;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.abstracts.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
        try {
            List<Student> existingStudents = studentsRepository.findByNumber(student.getNumber());
            
            if (existingStudents.isEmpty()) {
                student.setVerified(false);
                student.setView(false);
                log.info("Yeni ogrenci kaydediliyor: {} {} ({})", 
                        student.getName(), student.getSurname(), student.getNumber());
            } else {
                Student existingStudent = existingStudents.get(0);
                if (!existingStudent.getVerified()) {
                    existingStudent.setName(student.getName());
                    existingStudent.setSurname(student.getSurname());
                    log.info("Mevcut ogrenci guncelleniyor: {} {} ({})", 
                            existingStudent.getName(), existingStudent.getSurname(), existingStudent.getNumber());
                    return studentsRepository.save(existingStudent);
                } else {
                    log.info("Ogrenci zaten onaylanmis, guncelleme yapilmiyor: {} {} ({})", 
                            existingStudent.getName(), existingStudent.getSurname(), existingStudent.getNumber());
                    return existingStudent;
                }
            }
            
            return studentsRepository.save(student);
        } catch (Exception e) {
            log.error("Ogrenci kaydedilirken hata: {} {} - Hata: {}", 
                     student.getName(), student.getSurname(), e.getMessage());
            throw new RuntimeException("Ogrenci kaydedilemedi: " + e.getMessage(), e);
        }
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