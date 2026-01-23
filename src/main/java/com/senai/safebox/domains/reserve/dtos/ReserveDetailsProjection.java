package com.senai.safebox.domains.reserve.dtos;

import com.senai.safebox.domains.reserve.enums.ReserveStatus;
import com.senai.safebox.domains.reserve.enums.SyncStatus;

public record ReserveDetailsProjection(
        Long id,
        String reserveNumber,
        String residentName,
        String residentDocument,
        Long lockerNumber,
        String houseNumber,
        String enterpriseName,
        ReserveStatus reserveStatus,
        SyncStatus syncStatus,
        String reserveDate,
        String pickupDate
) {

}
