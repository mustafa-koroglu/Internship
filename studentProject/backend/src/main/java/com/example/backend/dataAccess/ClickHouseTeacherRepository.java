package com.example.backend.dataAccess;

import com.example.backend.entities.Teacher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClickHouseTeacherRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClickHouseTeacherRepository(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Teacher> getAllTeachers() {
        String sql = "SELECT id, name, surname, phone_number FROM teachers";
        return jdbcTemplate.query(sql, (rs, rowNum) -> { //rowmapper
            Teacher teacher = new Teacher();
            teacher.setId(rs.getInt("id"));
            teacher.setName(rs.getString("name"));
            teacher.setSurname(rs.getString("surname"));
            teacher.setPhoneNumber(rs.getString("phone_number"));
            return teacher;
        });
    }

    public Teacher getTeacherById(int id) {
        String sql = "SELECT id, name, surname, phone_number FROM teachers WHERE id = ?";
        List<Teacher> teachers = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Teacher teacher = new Teacher();
            teacher.setId(rs.getInt("id"));
            teacher.setName(rs.getString("name"));
            teacher.setSurname(rs.getString("surname"));
            teacher.setPhoneNumber(rs.getString("phone_number"));
            return teacher;
        }, id);

        return teachers.isEmpty() ? null : teachers.get(0);
    }

    public void addTeacher(Teacher teacher) {
        String sql = "INSERT INTO teachers (id, name, surname, phone_number) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, teacher.getId(), teacher.getName(), teacher.getSurname(), teacher.getPhoneNumber());
    }

    public void removeTeacher(int id) {
        String sql = "ALTER TABLE teachers DELETE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void updateTeacher(Teacher teacher) {
        String sql = "ALTER TABLE teachers UPDATE name = ?, surname = ?, phone_number = ? WHERE id = ?";
        jdbcTemplate.update(sql, teacher.getName(), teacher.getSurname(), teacher.getPhoneNumber(), teacher.getId());
    }

}