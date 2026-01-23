package com.senai.safebox.domains.locker.dtos;

import com.senai.safebox.domains.locker.enums.LockerStatus;

public record LockerDashboardProjection(
        Long lockerId,
        Long lockerNumber,
        LockerStatus status,
        Long reserveId,
        String reserveNumber,
        String residentName,
        String residentDocument,
        String residentPhone,
        String houseNumber,
        String enterpriseName,
        Boolean isOpen,
        String reserveDate,
        String pickupDate
) {

    public boolean hasActiveReserve() {
        return reserveId != null;
    }
}
