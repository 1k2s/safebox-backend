package com.senai.safebox.domains.house;


import com.senai.safebox.domains.house.dtos.HouseCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseService {

    /*Injeção de dependência via constructor*/
    HouseRepository repository;

    public HouseService(HouseRepository houseRepository) {
        this.repository = houseRepository;
    }

    public List<HouseEntity> getAllHouses() {
        return repository.findAll();
    };

    public Optional<HouseEntity> getById(Long id) {
      return repository.findById(id);
    };

    public Optional<HouseEntity> getByNumber(String number) {
        return repository.findByNumber(number);
    }

    public HouseEntity createHouse(HouseCreateDTO houseCreateDTO) {

        Optional<HouseEntity> house = repository.findByNumber(houseCreateDTO.number());

        if (house.isPresent()) {
            throw new RuntimeException("Esse número da casa já existe");
        }

        HouseEntity newHouse = new HouseEntity(houseCreateDTO.number());

        return repository.save(newHouse);
    }



}
