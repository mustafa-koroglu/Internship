// Bu dosya, öğrenci güncelleme isteği için kullanılan DTO'yu tanımlar.
package com.example.backend.request;

/**
 * Öğrenci güncelleme isteği için kullanılan veri transfer nesnesi (DTO).
 * Öğrenci adı, soyadı, numarası, onay durumu ve görünürlük durumu ile güncelleme yapılır.
 */
public class UpdateStudentRequest {
    /** Öğrencinin adı */
    private String name;
    /** Öğrencinin soyadı */
    private String surname;
    /** Öğrencinin numarası */
    private String number;
    /** Öğrencinin onay durumu */
    private Boolean verified;
    /** Öğrencinin görünürlük durumu */
    private Boolean view;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public Boolean getView() { return view; }
    public void setView(Boolean view) { this.view = view; }
} 