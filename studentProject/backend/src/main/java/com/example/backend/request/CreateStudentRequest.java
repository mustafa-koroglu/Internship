package com.example.backend.request; // Request paketi

public class CreateStudentRequest { // Öğrenci oluşturma isteği DTO sınıfı
    private String name; // Öğrenci adı

    private String surname; // Öğrenci soyadı

    private String number; // Öğrenci numarası

    private Boolean verified; // Onay durumu

    private Boolean view; // Görünürlük durumu

    public String getName() { return name; } // İsim getter
    public void setName(String name) { this.name = name; } // İsim setter
    public String getSurname() { return surname; } // Soyisim getter
    public void setSurname(String surname) { this.surname = surname; } // Soyisim setter
    public String getNumber() { return number; } // Numara getter
    public void setNumber(String number) { this.number = number; } // Numara setter
    public Boolean getVerified() { return verified; } // Onay durumu getter
    public void setVerified(Boolean verified) { this.verified = verified; } // Onay durumu setter
    public Boolean getView() { return view; } // Görünürlük durumu getter
    public void setView(Boolean view) { this.view = view; } // Görünürlük durumu setter
} 