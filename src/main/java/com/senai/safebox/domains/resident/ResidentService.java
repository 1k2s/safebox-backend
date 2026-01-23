package com.senai.safebox.domains.resident;

import com.senai.safebox.domains.house.HouseRepository;
import com.senai.safebox.domains.house.HouseEntity;
import com.senai.safebox.domains.house.exceptions.HouseNotFoundException;
import com.senai.safebox.domains.resident.dtos.ResidentUpdateDTO;
import com.senai.safebox.domains.resident.exceptions.CPFAlreadyRegisteredException;
import org.springframework.stereotype.Service;
import com.senai.safebox.domains.resident.dtos.ResidenteCreateDTO;

import java.util.List;
import java.util.Optional;

@Service
public class ResidentService {

    /*Injeção de dependencia via constructor(Passando o gerenciamento para o framework)*/
    private final ResidentRepository residentRepository;
    private final HouseRepository houseRepository;

    public ResidentService(ResidentRepository residentRepository, HouseRepository houseRepository) {
        this.residentRepository = residentRepository;
        this.houseRepository = houseRepository;
    }

    public List<ResidentEntity> findAllResidents() {
        return residentRepository.findAll(); //O metodo findAll Já retorna uma lista.
    };

    public Optional<ResidentEntity> findById(Long id){
        return residentRepository.findById(id);
    };

    public Optional<ResidentEntity> findByDocument(String document){
        return residentRepository.findByDocument(document);
    };

    public ResidentEntity saveResident(ResidenteCreateDTO residenteCreateDTO) {

        Optional<ResidentEntity> resident = residentRepository.findByDocument(residenteCreateDTO.cpf());
        Optional<HouseEntity> house = houseRepository.findByNumber(residenteCreateDTO.numberHouse());

        System.out.println(resident.isPresent());
        System.out.println(house.isPresent());

        if (resident.isPresent()) {
            throw new CPFAlreadyRegisteredException("Cpf já cadastrado no banco de dados");
        }

        if (house.isEmpty()) {
            throw new HouseNotFoundException(("Casa não localizada!"));
        }

        ResidentEntity newResident = new ResidentEntity(residenteCreateDTO.name(), residenteCreateDTO.cpf(), residenteCreateDTO.phone(), house.get());

        return residentRepository.save(newResident);
    };

    public ResidentEntity updateResident(ResidentUpdateDTO residentUpdateDTO) {

        /*Verificando se o Resident existe*/
        ResidentEntity resident = residentRepository.findById(residentUpdateDTO.id())
                .orElseThrow(() -> new RuntimeException("Morador não localizado!"));

        /*Verificando se a House existe*/
        HouseEntity house = houseRepository.findById(residentUpdateDTO.idHouse())
                        .orElseThrow(() -> new RuntimeException("Casa não localizada!"));


        resident.setName(residentUpdateDTO.name());
        resident.setDocument(residentUpdateDTO.cpf());
        resident.setPhone(residentUpdateDTO.phone());
        resident.setHouse(house);

        return residentRepository.save(resident);
    }

    public boolean deleteResident(Long id) {
        Optional<ResidentEntity> residentEntity = residentRepository.findById(id);

        return residentEntity.map(resident -> {
           residentRepository.deleteById(resident.getId());
           return true;
        }).orElse(false);
    };
}