// Bu dosya, öğrenci güncelleme isteği için kullanılan DTO'yu tanımlar.
package com.example.backend.request;

/**
 * Öğrenci güncelleme isteği için kullanılan veri transfer nesnesi (DTO).
 * Öğrenci adı, soyadı ve numarası ile güncelleme yapılır.
 */
public class UpdateStudentRequest {
    /** Öğrencinin adı */
    private String name;
    /** Öğrencinin soyadı */
    private String surname;
    /** Öğrencinin numarası */
    private String number;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
} 