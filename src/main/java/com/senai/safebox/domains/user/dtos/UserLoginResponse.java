package com.senai.safebox.domains.user.dtos;

public record UserLoginResponse(
        String token,
        String type,
        String username,
        String role,
        Long expiresIn
) {
    public UserLoginResponse(String token, String username, String role, Long expiresIn) {
      this(token, "Bearer", username, role, expiresIn);
    };

}
