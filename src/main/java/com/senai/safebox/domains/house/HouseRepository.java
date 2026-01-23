package com.senai.safebox.domains.house;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseRepository extends JpaRepository<HouseEntity, Long> {

    /*@Query(value = "SELECT h FROM HouseEntity h WHERE h.number = :number")
    Optional<HouseEntity> findByHouseNumber(@Param("number") String number);*/

    Optional<HouseEntity> findByNumber(String number);
}