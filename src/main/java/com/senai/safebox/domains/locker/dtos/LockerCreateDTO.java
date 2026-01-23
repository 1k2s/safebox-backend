package com.senai.safebox.domains.locker.dtos;

import com.senai.safebox.domains.locker.enums.LockerStatus;

public record LockerCreateDTO(Long number, LockerStatus status) {
}
