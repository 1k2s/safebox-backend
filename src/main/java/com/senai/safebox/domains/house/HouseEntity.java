package com.senai.safebox.domains.house;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="house")
public class HouseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String number;

    public HouseEntity(@NotNull String number) {
        this.number = number;
    }

    public HouseEntity() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
}
