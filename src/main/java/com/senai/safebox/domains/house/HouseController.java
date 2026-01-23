package com.senai.safebox.domains.house;


import com.senai.safebox.domains.house.dtos.HouseCreateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/house")
public class HouseController {

    HouseService service;

    public HouseController(HouseService houseService) {
        this.service = houseService;
    };


    @GetMapping
    public ResponseEntity<List<HouseEntity>> getHousesAll() {
        List<HouseEntity> houses = service.getAllHouses();

        return houses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(houses);
    };

    @GetMapping("/{id}")
    public ResponseEntity<HouseEntity> getHouseById(@PathVariable("id") Long id) {

        Optional<HouseEntity> house = service.getById(id);

        return house
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    };

    @GetMapping("/number/{number}")
    public ResponseEntity<HouseEntity> getHouseByNumber(@PathVariable("number") String number){

        Optional<HouseEntity> house = service.getByNumber(number);

        return house
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    };


    @PostMapping
    public ResponseEntity<HouseEntity> createHouse(@RequestBody HouseCreateDTO houseCreateDTO) {
        HouseEntity newHouse = service.createHouse(houseCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newHouse);
    };



}
