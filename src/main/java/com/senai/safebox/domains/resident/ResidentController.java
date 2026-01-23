package com.senai.safebox.domains.resident;


import com.senai.safebox.domains.resident.dtos.ResidentUpdateDTO;
import com.senai.safebox.domains.resident.dtos.ResidenteCreateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController //definindo que a classe irá receber requisições HTTP, porta de entrada da API
@RequestMapping("/resident") //definindo o endpoint
public class ResidentController {

    /*Injeção de dependencia*/
    private final ResidentService service;

    public ResidentController(ResidentService residentService) {
        this.service = residentService;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public ResponseEntity<List<ResidentEntity>> getResidentsAll() {

        List<ResidentEntity> residents = service.findAllResidents();

        return residents.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.status(HttpStatus.OK).body(residents);
    };

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<ResidentEntity> getResidentById(@PathVariable("id") Long id) {

        Optional<ResidentEntity> resident = service.findById(id);


        /*USANDO A FORMA PADRÃO, VERIFICANDO APENAS COM OS METODOS DO OPTIONAL*/
        //if (idResident.isPresent()) {
        //  ResidentEntity residentEntity = idResident.get();
        //  return ResponseEntity.ok(residentEntity);
        //}
        //return ResponseEntity.notFound().build();

        /*USANDO EXPRESSÃO LAMBDA PARA RETORNAR*/
        //return idResident.
        //  map(res -> ResponseEntity.ok(res))
        //  .orElse(ResponseEntity.notFound().build());


        /*USANDO REFERÊNCIA DE METODO(LAMBDA MELHORADA)*/
        return resident
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/document/{document}")
    public ResponseEntity<ResidentEntity> getResidentByDocument(@PathVariable("document") String document) {

        Optional<ResidentEntity> resident = service.findByDocument(document);


        /*USANDO REFERÊNCIA DE METODO(LAMBDA MELHORADA)*/
        return resident
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<ResidentEntity> createResident(@RequestBody ResidenteCreateDTO residenteCreateDTO) {

        ResidentEntity newResident = service.saveResident(residenteCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newResident);
    };


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping
    public ResponseEntity<ResidentEntity> updateResident(@RequestBody ResidentUpdateDTO residentUpdateDTO) {

        ResidentEntity resident = service.updateResident(residentUpdateDTO);
        return ResponseEntity.ok(resident);
    };


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteResident(@PathVariable Long id) {

        boolean residentDeleted = service.deleteResident(id);

        return ResponseEntity.ok().body(residentDeleted);
    };

}
