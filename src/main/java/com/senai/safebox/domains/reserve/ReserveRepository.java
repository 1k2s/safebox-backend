package com.senai.safebox.domains.reserve;

import com.senai.safebox.domains.reserve.dtos.ReserveDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<ReserveEntity, Long> {

    boolean existsByNumber(String number);

    @Query("""
        SELECT new com.senai.safebox.domains.reserve.dtos.ReserveDetailsProjection(
            r.id,
            r.number,
            res.name,
            res.document,
            l.number,
            h.number,
            e.name,
            r.reserveStatus,
            r.syncStatus,
            r.dateReserve,
            r.datePickup
        )
        FROM ReserveEntity r
            INNER JOIN r.resident res
            INNER JOIN r.locker l
            INNER JOIN res.house h
            INNER JOIN r.enterprise e
        """)
    List<ReserveDetailsProjection> findAllReservesView();
}
