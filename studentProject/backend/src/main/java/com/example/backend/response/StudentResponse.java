package com.example.backend.response;

import java.util.List;

public class StudentResponse {
    private int id;

    private String name;

    private String surname;

    private String number;

    private Boolean verified;

    private Boolean view;

    private List<LessonResponse> lessons;

    private List<IpAddressResponse> ipAddresses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getView() {
        return view;
    }

    public void setView(Boolean view) {
        this.view = view;
    }

    public List<LessonResponse> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonResponse> lessons) {
        this.lessons = lessons;
    }

    public List<IpAddressResponse> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<IpAddressResponse> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }
} 