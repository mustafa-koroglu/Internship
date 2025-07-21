
package com.example.backend.request;

/**
 * Yeni öğrenci ekleme isteği için kullanılan veri transfer nesnesi (DTO).
 * Öğrenci adı, soyadı ve numarası ile ekleme yapılır.
 */
public class CreateStudentRequest {
    private String name;
    private String surname;
    private String number;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
} 