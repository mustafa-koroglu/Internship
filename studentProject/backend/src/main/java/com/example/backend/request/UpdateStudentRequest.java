// Bu dosya, öğrenci güncelleme isteği için kullanılan DTO'yu tanımlar.
package com.example.backend.request;

// Öğrenci güncelleme isteği için kullanılan veri transfer nesnesi
public class UpdateStudentRequest {
    // Öğrencinin adı
    private String name;
    // Öğrencinin soyadı
    private String surname;
    // Öğrencinin numarası
    private String number;
    // Öğrencinin adını döndüren getter metodu
    public String getName() { return name; }
    // Öğrencinin adını ayarlayan setter metodu
    public void setName(String name) { this.name = name; }
    // Öğrencinin soyadını döndüren getter metodu
    public String getSurname() { return surname; }
    // Öğrencinin soyadını ayarlayan setter metodu
    public void setSurname(String surname) { this.surname = surname; }
    // Öğrencinin numarasını döndüren getter metodu
    public String getNumber() { return number; }
    // Öğrencinin numarasını ayarlayan setter metodu
    public void setNumber(String number) { this.number = number; }
} 