package com.senai.safebox.domains.reserve;

import com.senai.safebox.domains.enterprise.EnterpriseEntity;
import com.senai.safebox.domains.enterprise.EnterpriseRepository;
import com.senai.safebox.domains.locker.LockerEntity;
import com.senai.safebox.domains.locker.LockerRepository;
import com.senai.safebox.domains.locker.enums.LockerStatus;
import com.senai.safebox.domains.reserve.dtos.ReserveDetailsProjection;
import com.senai.safebox.domains.reserve.dtos.ReserveUpdateDTO;
import com.senai.safebox.domains.reserve.dtos.ReserveCreateDTO;
import com.senai.safebox.domains.reserve.enums.ReserveStatus;
import com.senai.safebox.domains.reserve.enums.SyncStatus;
import com.senai.safebox.domains.resident.ResidentEntity;
import com.senai.safebox.domains.resident.ResidentRepository;
import com.senai.safebox.domains.user.exceptions.UserNotFoundException;
import com.senai.safebox.brokerMQTT.dto.ReservaRequest;
import com.senai.safebox.brokerMQTT.service.MqttService;
import com.senai.safebox.wapAPI.service.MessageService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReserveService {

    /*Injeção de dependencia via constructor(Passando o gerenciamento para o framework)*/
    private final ReserveRepository reserveRepository;
    private final LockerRepository lockerRepository;
    private final ResidentRepository residentRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final MqttService mqttService;
    private final MessageService messageService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReserveService(ReserveRepository reserveRepository, LockerRepository lockerRepository, ResidentRepository residentRepository, EnterpriseRepository enterpriseRepository, MqttService mqttService, MessageService messageService) {
        this.reserveRepository = reserveRepository;
        this.lockerRepository = lockerRepository;
        this.residentRepository = residentRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.mqttService = mqttService;
        this.messageService = messageService;
    }


    public List<ReserveDetailsProjection> findAllReserves() {
        return reserveRepository.findAllReservesView(); //O metodo findAll Já retorna uma lista.
    };

    public Optional<ReserveEntity> findById(Long id){
        return reserveRepository.findById(id);
    };


    @Transactional
    public ReserveEntity saveReserve(ReserveCreateDTO reserveCreateDTO) {

        LockerEntity locker = lockerRepository.findById(reserveCreateDTO.idLocker())
                .orElseThrow(() -> new UserNotFoundException("Locker não localizado!"));

        ResidentEntity resident = residentRepository.findById(reserveCreateDTO.idResident())
                .orElseThrow(() -> new RuntimeException("Morador não localizado!"));

        EnterpriseEntity enterprise = enterpriseRepository.findById(reserveCreateDTO.idEnterprise())
                .orElseThrow(() -> new RuntimeException("Empresa não localizada!"));


        ReserveEntity newReserve = new ReserveEntity(
                reserveCreateDTO.numberReserve(),
                resident, locker,
                enterprise,
                LocalDateTime.now().format(formatter)
        );

        locker.setStatus(LockerStatus.OCCUPIED);

        reserveRepository.save(newReserve);
        lockerRepository.save(locker);

        /*MQTT*/
        try {
            String senha = resident.getDocument();

            ReservaRequest reservaRequest = new ReservaRequest(
                    resident.getName(),
                    senha.substring(senha.length() - 4),
                    locker.getNumber()
            );

            /*boolean enviado = mqttService.enviarReserva(reservaRequest);

            if (enviado) {
                newReserve.setSyncStatus(SyncStatus.SYNCED);
            } else {
                newReserve.setSyncStatus(SyncStatus.FAILED);
            }*/
        } catch (Exception e) {
            System.out.print("Erro ao sincronizar com MQTT: " + e.getMessage());
            newReserve.setSyncStatus(SyncStatus.FAILED);
        }



        return reserveRepository.save(newReserve);
    };

    @Transactional
    public void completePickup(Long reserveId) {

        ReserveEntity reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada")
        );

        if (reserve.getReserveStatus() != ReserveStatus.ACTIVE) {
            throw new RuntimeException("Esta reserva não está ativa");
        }

        LockerEntity locker = reserve.getLocker();

        reserve.setDatePickup(LocalDateTime.now().format(formatter));
        reserve.setReserveStatus(ReserveStatus.COMPLETED);

        locker.setOpen(false);
        locker.setStatus(LockerStatus.AVAILABLE);

        reserveRepository.save(reserve);
        lockerRepository.save(locker);
    }


    public ReserveEntity updateReserve(ReserveUpdateDTO reserveUpdateDTO) {

        return null;
    }

    public boolean deleteReserve(Long id) {
        Optional<ReserveEntity> reserveEntity = reserveRepository.findById(id);

        return reserveEntity.map(reserve -> {
            reserveRepository.deleteById(reserve.getId());
            return true;
        }).orElse(false);
    };
}
