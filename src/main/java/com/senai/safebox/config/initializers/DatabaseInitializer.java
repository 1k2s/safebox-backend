package com.senai.safebox.config.initializers;

import com.senai.safebox.domains.enterprise.EnterpriseEntity;
import com.senai.safebox.domains.enterprise.EnterpriseRepository;
import com.senai.safebox.domains.house.HouseEntity;
import com.senai.safebox.domains.house.HouseRepository;
import com.senai.safebox.domains.locker.LockerEntity;
import com.senai.safebox.domains.locker.LockerRepository;
import com.senai.safebox.domains.resident.ResidentEntity;
import com.senai.safebox.domains.resident.ResidentRepository;
import com.senai.safebox.domains.reserve.ReserveEntity;
import com.senai.safebox.domains.reserve.ReserveRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final EnterpriseRepository enterpriseRepository;
    private final HouseRepository houseRepository;
    private final ResidentRepository residentRepository;
    private final ReserveRepository reserveRepository;
    private final LockerRepository lockerRepository;

    public DatabaseInitializer(EnterpriseRepository enterpriseRepository,
                               HouseRepository houseRepository,
                               ResidentRepository residentRepository,
                               ReserveRepository reserveRepository, LockerRepository lockerRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.houseRepository = houseRepository;
        this.residentRepository = residentRepository;
        this.reserveRepository = reserveRepository;
        this.lockerRepository = lockerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inicializa empresas se não existirem
        if (enterpriseRepository.count() == 0) {
            initializeEnterprises();
        }

        // Inicializa casas se não existirem
        if (houseRepository.count() == 0) {
            initializeHouses();
        }

        // Inicializa lockers se não existirem
        if (lockerRepository.count() == 0) {
            initializeLockers();
        }

        // Inicializa moradores se não existirem E se houver casas disponíveis
        if (residentRepository.count() == 0 && houseRepository.count() > 0) {
            initializeResidents();
        }


        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  Banco de dados inicializado!");
        System.out.println("═══════════════════════════════════════\n");
    }

    private void initializeEnterprises() {

        EnterpriseEntity mercadoLivre = new EnterpriseEntity();
        mercadoLivre.setName("Mercado Livre");
        mercadoLivre.setCnpj("1234323434");
        enterpriseRepository.save(mercadoLivre);

        EnterpriseEntity correios = new EnterpriseEntity();
        correios.setName("Correios");
        correios.setCnpj("1234323434");
        enterpriseRepository.save(correios);

        EnterpriseEntity amazon = new EnterpriseEntity();
        amazon.setName("Amazon");
        amazon.setCnpj("1234323434");
        enterpriseRepository.save(amazon);

        EnterpriseEntity ifood = new EnterpriseEntity();
        ifood.setName("Ifood");
        ifood.setCnpj("1234323434");
        enterpriseRepository.save(ifood);

    }

    private void initializeHouses() {
        HouseEntity casa1 = new HouseEntity("101");
        houseRepository.save(casa1);

        HouseEntity casa2 = new HouseEntity("102");
        houseRepository.save(casa2);

        HouseEntity casa3 = new HouseEntity("201");
        houseRepository.save(casa3);

        HouseEntity casa4 = new HouseEntity("202");
        houseRepository.save(casa4);

        HouseEntity casa5 = new HouseEntity("301");
        houseRepository.save(casa5);
    }

    private void initializeLockers() {
        LockerEntity locker1 = new LockerEntity(1L);
        lockerRepository.save(locker1);

        LockerEntity locker2 = new LockerEntity(2L);
        lockerRepository.save(locker2);

        LockerEntity locker3 = new LockerEntity(3L);
        lockerRepository.save(locker3);

        LockerEntity locker4 = new LockerEntity(4L);
        lockerRepository.save(locker4);

        LockerEntity locker5 = new LockerEntity(5L);
        lockerRepository.save(locker5);

        LockerEntity locker6 = new LockerEntity(6L);
        lockerRepository.save(locker6);
    }

    private void initializeResidents() {

        List<HouseEntity> casas = houseRepository.findAll();

        ResidentEntity morador1 = new ResidentEntity(
                "João Silva",
                "12345678900",
                "16994556746",
                casas.get(0) // Casa 101
        );
        residentRepository.save(morador1);

        ResidentEntity morador2 = new ResidentEntity(
                "Maria Santos",
                "23456789011",
                "16999998888",
                casas.get(1) // Casa 102
        );
        residentRepository.save(morador2);

        ResidentEntity morador3 = new ResidentEntity(
                "Pedro Oliveira",
                "34567890122",
                "16999998888",
                casas.get(2) // Casa 201
        );
        residentRepository.save(morador3);

        ResidentEntity morador4 = new ResidentEntity(
                "Ana Costa",
                "45678901233",
                "16999998888",
                casas.get(3) // Casa 202
        );
        residentRepository.save(morador4);

        ResidentEntity morador5 = new ResidentEntity(
                "Carlos Ferreira",
                "56789012344",
                "16999998888",
                casas.get(4) // Casa 301
        );
        residentRepository.save(morador5);
    }
}