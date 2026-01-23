package com.senai.safebox.domains.locker;

import com.senai.safebox.domains.locker.dtos.LockerDashboardProjection;
import com.senai.safebox.domains.locker.dtos.LockerUpdateDTO;
import com.senai.safebox.domains.locker.dtos.LockerCreateDTO;
import com.senai.safebox.domains.locker.dtos.UpdateLockerReleaseDTO;
import com.senai.safebox.domains.reserve.ReserveRepository;
import com.senai.safebox.brokerMQTT.service.MqttService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class LockerService {

    /*Injeção de dependencia via constructor(Passando o gerenciamento para o framework)*/
    private final LockerRepository lockerRepository;
    private final ReserveRepository reserveRepository;
    private final MqttService mqttService;

    public LockerService(LockerRepository lockerRepository, ReserveRepository reserveRepository, MqttService mqttService) {
        this.lockerRepository = lockerRepository;
        this.reserveRepository = reserveRepository;
        this.mqttService = mqttService;
    }

    public List<LockerEntity> findAllLockers() {
        return lockerRepository.findAll(); //O metodo findAll Já retorna uma lista.
    };

    public List<LockerDashboardProjection> findLockersWithreserve() {
      return lockerRepository.findLockersWithReservesView();
    };

    public Optional<LockerDashboardProjection> findLockersWithreserveById(Long id) {
        return lockerRepository.findLockerWithReserveByIdView(id);
    };



    public Optional<LockerEntity> findById(Long id){
        return lockerRepository.findById(id);
    };

    public LockerEntity saveLocker(LockerCreateDTO lockerCreateDTO) {


        LockerEntity newLocker = new LockerEntity(lockerCreateDTO.number(), lockerCreateDTO.status());

        return lockerRepository.save(newLocker);
    };

    public LockerEntity updateLocker(LockerUpdateDTO lockerUpdateDTO) {

        LockerEntity locker = lockerRepository.findById(lockerUpdateDTO.id())
                .orElseThrow(() -> new RuntimeException("Locker não localizado!"));

        locker.setNumber(lockerUpdateDTO.number());
        locker.setStatus(lockerUpdateDTO.status());
        locker.setOpen(lockerUpdateDTO.isOpen());

        return lockerRepository.save(locker);
    }


    public boolean updateLockerRelease(Long id, UpdateLockerReleaseDTO updateLockerReleaseDTO) {
        LockerEntity locker = lockerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locker não localizado!"));

        try {

            boolean desbloqueado = mqttService.desbloquearBox(locker.getNumber());

            if (!desbloqueado) {
                throw new RuntimeException("Não foi possível desbloquear o cofre. Verifique a conexão!");
            }

        } catch (Exception e) {
            System.out.print(" Erro ao desbloquear cofre: " + e.getMessage());
            throw new RuntimeException("Falha ao abrir o cofre: " + e.getMessage());
        }

        locker.setOpen(updateLockerReleaseDTO.isOpen());

        lockerRepository.save(locker);

        return true;
    }


    public boolean deleteLocker(Long id) {
        Optional<LockerEntity> lockerEntity = lockerRepository.findById(id);

        return lockerEntity.map(locker -> {
            lockerRepository.deleteById(locker.getId());
            return true;
        }).orElse(false);
    };
}
