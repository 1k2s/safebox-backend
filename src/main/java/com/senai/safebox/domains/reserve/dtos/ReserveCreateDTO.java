package com.senai.safebox.domains.reserve.dtos;

import com.senai.safebox.domains.reserve.enums.ReserveStatus;
import com.senai.safebox.domains.reserve.enums.SyncStatus;

public record ReserveCreateDTO(String numberReserve, Long idResident, Long idLocker, Long idEnterprise) {
}
