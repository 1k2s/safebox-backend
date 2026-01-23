package com.senai.safebox.domains.resident;


import com.senai.safebox.domains.house.HouseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="resident")
public class ResidentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Estratégia em que o banco realiza a criação dos IDs
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String document;

    @NotNull
    private String phone;

    @ManyToOne()
    @JoinColumn(name="id_house") //foreigh Key do relacionamento, coluna que faz a ligação entre tabelas
    @NotNull
    private HouseEntity house;



    public ResidentEntity( String name,  String document,  String phone, HouseEntity house) {
        this.name = name;
        this.document = document;
        this.house = house;
        this.phone = phone;
    }

    public ResidentEntity() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public HouseEntity getHouse() {
        return house;
    }

    public void setHouse(HouseEntity house) {
        this.house = house;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
