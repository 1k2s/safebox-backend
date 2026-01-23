package com.senai.safebox.domains.enterprise;

import com.senai.safebox.domains.enterprise.dtos.EnterpriseCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnterpriseService {

    EnterpriseRepository enterpriseRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    public List<EnterpriseEntity> findAllEnterprises() {
        return enterpriseRepository.findAll();
    };

    public Optional<EnterpriseEntity> findById(Long id) {
        return enterpriseRepository.findById(id);
    };

    public EnterpriseEntity saveEnterprise(EnterpriseCreateDTO enterpriseCreateDTO){

        EnterpriseEntity enterprise = new EnterpriseEntity(enterpriseCreateDTO.name(), enterpriseCreateDTO.cnpj());

        return enterpriseRepository.save(enterprise);
    };
}
