// Bu dosya, öğrenciyle ilgili yanıtlar için kullanılan DTO'yu tanımlar.
package com.example.backend.response;

// Öğrenci yanıtı için kullanılan veri transfer nesnesi
public class StudentResponse {
    // Öğrencinin veritabanındaki benzersiz kimliği
    private int id;
    // Öğrencinin adı
    private String name;
    // Öğrencinin soyadı
    private String surname;
    // Öğrencinin numarası
    private String number;
    // Öğrencinin id'sini döndüren getter metodu
    public int getId() { return id; }
    // Öğrencinin id'sini ayarlayan setter metodu
    public void setId(int id) { this.id = id; }
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