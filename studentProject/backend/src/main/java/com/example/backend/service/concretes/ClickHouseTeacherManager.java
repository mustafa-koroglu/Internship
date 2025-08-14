package com.example.backend.service.concretes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClickHouseTeacherManager {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String CLICKHOUSE_SERVICE_URL = "http://localhost:8081/api/v1/teachers";

    public List<Map<String, Object>> getAllTeachers() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(CLICKHOUSE_SERVICE_URL, String.class);
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Ogretmenler getirilemedi: " + e.getMessage());
        }
    }

    public Map<String, Object> getTeacherById(Long id) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(CLICKHOUSE_SERVICE_URL + "/" + id, String.class);
            return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Ogretmen bulunamadi: " + e.getMessage());
        }
    }

    public void addTeacher(Object teacherData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // gönderien formatın json formatında olduğunu belirtir.
        HttpEntity<Object> request = new HttpEntity<>(teacherData, headers); //header ve entity tek bir nesnede toplar
        
        restTemplate.postForEntity(CLICKHOUSE_SERVICE_URL, request, String.class);
    }

    public void removeTeacher(Long id) {
        restTemplate.delete(CLICKHOUSE_SERVICE_URL + "/" + id);
    }

    public void updateTeacher(Long id, Object teacherData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(teacherData, headers);
        
        restTemplate.exchange(CLICKHOUSE_SERVICE_URL + "/" + id, 
                            HttpMethod.PUT, request, String.class);
    }
}


