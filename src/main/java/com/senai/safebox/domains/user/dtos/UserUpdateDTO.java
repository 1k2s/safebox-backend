package com.senai.safebox.domains.user.dtos;

import com.senai.safebox.domains.user.enums.UserRoles;

public record UserUpdateDTO(Long id, String username, UserRoles role, String password) {
}
