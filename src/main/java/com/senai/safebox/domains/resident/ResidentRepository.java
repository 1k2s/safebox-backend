package com.senai.safebox.domains.resident;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResidentRepository extends JpaRepository<ResidentEntity, Long> {

    /*@Query(value = "SELECT * FROM idResident WHERE cpf = :cpf", nativeQuery = true)
    Optional<ResidentEntity> findByDocument(@Param("cpf") String cpf);*/

    Optional<ResidentEntity> findByDocument(String cpf);

}
