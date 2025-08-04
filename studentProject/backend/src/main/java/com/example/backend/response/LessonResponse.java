package com.example.backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonResponse {
    private int id;
    private String name;
    private String description;
    private String academicYear;
    private String term;
} 