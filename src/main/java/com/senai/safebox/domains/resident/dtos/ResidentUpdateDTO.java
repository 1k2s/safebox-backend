package com.senai.safebox.domains.resident.dtos;

public record ResidentUpdateDTO(Long id, String name, String cpf, String phone, Long idHouse) {
}
