package com.senai.safebox.domains.user.enums;

public enum UserRoles {

    ADMIN("admin"),
    USER("user");

    private String name;

    UserRoles(String role) {
        this.name = role;
    };

    public String getName() {
      return this.name;
    };

}
