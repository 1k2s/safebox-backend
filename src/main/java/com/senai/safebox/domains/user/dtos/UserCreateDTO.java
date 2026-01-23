package com.senai.safebox.domains.user.dtos;

import com.senai.safebox.domains.user.enums.UserRoles;


public record UserCreateDTO(String username, String password, UserRoles role) {
}
