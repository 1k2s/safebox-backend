package com.senai.safebox.domains.reserve;


import com.senai.safebox.domains.reserve.dtos.ReserveCreateDTO;
import com.senai.safebox.domains.reserve.dtos.ReserveDetailsProjection;
import com.senai.safebox.domains.reserve.dtos.ReserveUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reserve")
public class ReserveController {

    private final ReserveService reserveService;

    public ReserveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @GetMapping
    public ResponseEntity<List<ReserveDetailsProjection>> getReserveAll() {

        List<ReserveDetailsProjection> reserves = reserveService.findAllReserves();

        return reserves.isEmpty()
            ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            : ResponseEntity.status(HttpStatus.OK).body(reserves)
        ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReserveEntity> getReserveById(@PathVariable("id") Long id) {
        Optional<ReserveEntity> reserve = reserveService.findById(id);

        return reserve
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build())
        ;
    }

    @PostMapping("/register")
    public ResponseEntity<ReserveEntity> createReserve(@RequestBody ReserveCreateDTO reserveCreateDTO) {

        ReserveEntity newReserve = reserveService.saveReserve(reserveCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newReserve);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping
    public ResponseEntity<ReserveEntity> updateReserve(@RequestBody ReserveUpdateDTO reserveUpdateDTO) {

        ReserveEntity reserve = reserveService.updateReserve(reserveUpdateDTO);

        return ResponseEntity.ok(reserve);
    };

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReserve(@PathVariable Long id) {

        boolean reserveDeleted = reserveService.deleteReserve(id);

        return ResponseEntity.ok().body(reserveDeleted);
    };
}