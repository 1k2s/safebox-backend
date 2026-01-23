package com.senai.safebox.domains.reserve.dtos;

import com.senai.safebox.domains.enterprise.EnterpriseEntity;
import com.senai.safebox.domains.resident.ResidentEntity;

public record ReserveUpdateDTO(ResidentEntity resident, Long numberReserve, int cofre, EnterpriseEntity enterprise, String date_reserve, String date_pickup) {
}
