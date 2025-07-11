package com.example1.studentsrestapi.entitites.concretes;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity   // veritabanı varlığı olduğunu bildirir.
@Table(name = "students") // benim sınfıımın veritabanındaki karşılığı students adındaki tablodur.

public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "number")
    private String number;




}
