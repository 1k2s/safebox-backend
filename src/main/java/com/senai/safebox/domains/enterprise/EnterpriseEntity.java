package com.senai.safebox.domains.enterprise;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="enterprise")
public class EnterpriseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String cnpj;

    public EnterpriseEntity(String name, String cnpj) {
        this.name = name;
        this.cnpj = cnpj;
    }

    public EnterpriseEntity() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
