package com.example1.studentsrestapi.dataAccess;

import com.example1.studentsrestapi.entitites.concretes.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentsRepository extends JpaRepository <Student,Integer>{



}
