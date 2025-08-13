package com.example.clickhouse.repository;

import com.example.clickhouse.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TeacherRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Teacher> teacherRowMapper = (rs, rowNum) -> {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getLong("id"));
        teacher.setName(rs.getString("name"));
        teacher.setSurname(rs.getString("surname"));
        teacher.setPhoneNumber(rs.getString("phoneNumber"));
        return teacher;
    };

    public List<Teacher> findAll() {
        String sql = "SELECT id, name, surname, phoneNumber FROM teacher ORDER BY id";
        return jdbcTemplate.query(sql, teacherRowMapper);
    }

    public Optional<Teacher> findById(Long id) {
        String sql = "SELECT id, name, surname, phoneNumber FROM teacher WHERE id = ?";
        List<Teacher> teachers = jdbcTemplate.query(sql, teacherRowMapper, id);
        return teachers.isEmpty() ? Optional.empty() : Optional.of(teachers.get(0));
    }

    private Long currentId = 1L;

    public Teacher save(Teacher teacher) {
        if (teacher.getId() == null) {

            String sql = "INSERT INTO teacher (id, name, surname, phoneNumber) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, currentId, teacher.getName(), teacher.getSurname(), teacher.getPhoneNumber());
            teacher.setId(currentId);
            currentId++;
        } else {
            String sql = "INSERT INTO teacher (id, name, surname, phoneNumber) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, teacher.getId(), teacher.getName(), teacher.getSurname(), teacher.getPhoneNumber());
        }
        return teacher;
    }

    public void deleteById(Long id) {
        String sql = "ALTER TABLE teacher DELETE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


}
