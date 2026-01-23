package com.senai.safebox.domains.locker;

import com.senai.safebox.domains.locker.dtos.LockerDashboardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LockerRepository extends JpaRepository<LockerEntity, Long> {

    @Query("""
    SELECT new com.senai.safebox.domains.locker.dtos.LockerDashboardProjection(
        l.id,
        l.number,
        l.status,
        r.id,
        r.number,
        res.name,
        res.document,
        res.phone,
        h.number,
        e.name,
        l.isOpen,
        r.dateReserve,
        r.datePickup
    )
    FROM
        LockerEntity l
        LEFT JOIN ReserveEntity r ON r.locker.id = l.id AND r.reserveStatus = 'ACTIVE'
        LEFT JOIN ResidentEntity res ON res.id = r.resident.id
        LEFT JOIN HouseEntity h ON h.id = res.house.id
        LEFT JOIN EnterpriseEntity e ON e.id = r.enterprise.id
    ORDER BY
        l.number
    """)
    List<LockerDashboardProjection> findLockersWithReservesView();

    @Query("""
    SELECT new com.senai.safebox.domains.locker.dtos.LockerDashboardProjection(
        l.id,
        l.number,
        l.status,
        r.id,
        r.number,
        res.name,
        res.document,
        res.phone,
        h.number,
        e.name,
        l.isOpen,
        r.dateReserve,
        r.datePickup
    )
    FROM LockerEntity l
    LEFT JOIN ReserveEntity r ON r.locker.id = l.id AND r.reserveStatus = 'ACTIVE'
    LEFT JOIN ResidentEntity res ON res.id = r.resident.id
    LEFT JOIN HouseEntity h ON h.id = res.house.id
    LEFT JOIN EnterpriseEntity e ON e.id = r.enterprise.id
    WHERE l.id = :id
    """)
    Optional<LockerDashboardProjection> findLockerWithReserveByIdView(Long id);
}
