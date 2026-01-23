package com.senai.safebox.domains.user;


import com.senai.safebox.domains.user.enums.UserRoles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRoles role;


    public UserEntity(String username, String password, UserRoles role) {
        this.username = username;
        this.password = password;
        this.role = role;
    };

    public UserEntity() {};



    /*Getters and Setters*/
    public String getUsername() {
        return username;
    };

    public void setUsername(String username) {
        this.username = username;
    };

    public String getPassword() {
        return this.password;
    };

    public void setPassword(String password) { this.password = password; };

    public UserRoles getRole() {
        return this.role;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
