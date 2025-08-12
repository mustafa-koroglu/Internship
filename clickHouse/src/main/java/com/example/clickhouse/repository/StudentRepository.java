package com.example.clickhouse.repository;

import com.example.clickhouse.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // veritabanından gelen satırlrı java nesnesine dönüştürür: RowMapper
    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setName(rs.getString("name"));
        student.setSurname(rs.getString("surname"));
        student.setNumber(rs.getString("number"));
        return student;
    };

    public List<Student> findAll() {
        String sql = "SELECT id, name, surname, number FROM students";
        return jdbcTemplate.query(sql, studentRowMapper);
    }

    public Optional<Student> findById(Long id) {
        String sql = "SELECT id, name, surname, number FROM students WHERE id = ?";
        List<Student> students = jdbcTemplate.query(sql, studentRowMapper, id);
        return students.isEmpty() ? Optional.empty() : Optional.of(students.get(0));
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            return insert(student);
        } else {
            return update(student);
        }
    }

    public Student insert(Student student) {
        // Get next available ID
        String maxIdSql = "SELECT max(id) FROM students";
        Long maxId = jdbcTemplate.queryForObject(maxIdSql, Long.class);
        Long nextId = (maxId == null) ? 1L : maxId + 1;
        student.setId(nextId);
        
        // Insert new student
        String sql = "INSERT INTO students (id, name, surname, number) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, student.getId(), student.getName(), student.getSurname(), student.getNumber());
        return student;
    }

    public Student update(Student student) {
        String sql = "ALTER TABLE students UPDATE name = ?, surname = ?, number = ? WHERE id = ?";
        jdbcTemplate.update(sql, student.getName(), student.getSurname(), student.getNumber(), student.getId());
        return student;
    }

    public void deleteById(Long id) {
        String sql = "ALTER TABLE students DELETE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


}
