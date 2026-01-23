package com.senai.safebox.domains.locker;


import com.senai.safebox.domains.locker.dtos.LockerCreateDTO;
import com.senai.safebox.domains.locker.dtos.LockerDashboardProjection;
import com.senai.safebox.domains.locker.dtos.LockerUpdateDTO;
import com.senai.safebox.domains.locker.dtos.UpdateLockerReleaseDTO;
import com.senai.safebox.domains.reserve.ReserveService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/locker")
public class LockerController {

    private final LockerService lockerService;
    private final ReserveService reserveService;

    public LockerController(LockerService lockerService, ReserveService reserveService) {
        this.lockerService = lockerService;
        this.reserveService = reserveService;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public ResponseEntity<List<LockerEntity>> getLockerAll() {

        List<LockerEntity> lockers = lockerService.findAllLockers();

        return lockers.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.OK).body(lockers);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<LockerEntity> getLockerById(@PathVariable("id") Long id) {
        Optional<LockerEntity> locker = lockerService.findById(id);

        return locker
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<LockerDashboardProjection>> getDashboardLockers() {

        List<LockerDashboardProjection> lockers = lockerService.findLockersWithreserve();

        return lockers.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.OK).body(lockers);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/register")
    public ResponseEntity<LockerEntity> createLocker(@RequestBody LockerCreateDTO lockerCreateDTO) {

        LockerEntity newLocker = lockerService.saveLocker(lockerCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newLocker);
    }

    @PostMapping("/{lockerId}/pickup")
    public ResponseEntity<?> completePickup(@PathVariable Long lockerId) {

        LockerDashboardProjection locker = lockerService.findLockersWithreserveById(lockerId)
                .orElseThrow(() -> new RuntimeException("locker não localizado!"));

        if (!locker.hasActiveReserve()) {
            throw new RuntimeException("Não existe reserva para este locker!");
        }

        reserveService.completePickup(locker.reserveId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping
    public ResponseEntity<LockerEntity> updateLocker(@RequestBody LockerUpdateDTO lockerUpdateDTO) {

        LockerEntity locker = lockerService.updateLocker(lockerUpdateDTO);

        return ResponseEntity.ok(locker);
    };

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/{id}/toggleRelease")
    public ResponseEntity<UpdateLockerReleaseDTO> updateLockerRelease(@PathVariable Long id, @RequestBody UpdateLockerReleaseDTO updateLockerReleaseDTO) {

        Boolean updatedLocker = lockerService.updateLockerRelease(id, updateLockerReleaseDTO);

        return ResponseEntity.ok().body(updateLockerReleaseDTO);
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteLocker(@PathVariable Long id) {

        boolean lockerDeleted = lockerService.deleteLocker(id);

        return ResponseEntity.ok().body(lockerDeleted);
    };
}