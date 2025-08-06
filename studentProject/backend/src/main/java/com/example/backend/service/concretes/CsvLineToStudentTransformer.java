package com.example.backend.service.concretes;

import com.example.backend.entities.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class CsvLineToStudentTransformer {

    @Transformer(inputChannel = "csvLineChannel", outputChannel = "studentChannel")
    public Student transformCsvLineToStudent(String[] csvLine) {
        if (csvLine == null || csvLine.length < 3) {
            log.debug("CSV satiri gecersiz: {}", Arrays.toString(csvLine));
            return null;
        }

        try {
            Student student = new Student();

            // İlk 3 sütunu al (name, surname, number)
            String name = csvLine[0] != null ? csvLine[0].trim() : null;
            String surname = csvLine[1] != null ? csvLine[1].trim() : null;
            String number = csvLine[2] != null ? csvLine[2].trim() : null;

            // Boş değer kontrolü
            if (isEmpty(name) || isEmpty(surname) || isEmpty(number)) {
                log.debug("Bos deger bulundu: name='{}', surname='{}', number='{}'", name, surname, number);
                return null;
            }

            student.setName(name);
            student.setSurname(surname);
            student.setNumber(number);
            student.setVerified(false);
            student.setView(false);

            log.debug("Student nesnesi olusturuldu: {} {} ({})", name, surname, number);
            return student;

        } catch (Exception e) {
            log.error("CSV satiri Student nesnesine donusturulurken hata: {}", e.getMessage());
            return null;
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
} 