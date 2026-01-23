package com.senai.safebox.domains.enterprise;


import com.senai.safebox.domains.enterprise.dtos.EnterpriseCreateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {

    EnterpriseService enterpriseService;

    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @GetMapping
    public ResponseEntity<List<EnterpriseEntity>> getEnterpriseAll() {

        List<EnterpriseEntity> enterprise = enterpriseService.findAllEnterprises();

        return enterprise.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.OK).body(enterprise);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnterpriseEntity> getEnterpriseById(@PathVariable("id") Long id) {
        Optional<EnterpriseEntity> enterprise = enterpriseService.findById(id);

        return enterprise
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EnterpriseEntity> createEnterprise(@RequestBody EnterpriseCreateDTO enterpriseCreateDTO) {

        EnterpriseEntity newEnterprise = enterpriseService.saveEnterprise(enterpriseCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newEnterprise);
    }
}
