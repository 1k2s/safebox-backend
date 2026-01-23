package com.senai.safebox.domains.locker.dtos;

import com.senai.safebox.domains.locker.enums.LockerStatus;

public record LockerUpdateDTO(Long id, Long number, LockerStatus status, Boolean isOpen) {
}
