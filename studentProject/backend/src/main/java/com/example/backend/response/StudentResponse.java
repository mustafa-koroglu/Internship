
package com.example.backend.response;

/**
 * Öğrenci yanıtı için kullanılan veri transfer nesnesi (DTO).
 * Öğrencinin id, adı, soyadı, numarası, onay durumu ve görünürlük durumunu içerir.
 */
public class StudentResponse {
    private int id;
    private String name;
    private String surname;
    private String number;
    private Boolean verified;
    private Boolean view;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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