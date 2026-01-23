package com.senai.safebox.domains.reserve;

import com.senai.safebox.domains.enterprise.EnterpriseEntity;
import com.senai.safebox.domains.locker.LockerEntity;
import com.senai.safebox.domains.reserve.enums.ReserveStatus;
import com.senai.safebox.domains.reserve.enums.SyncStatus;
import com.senai.safebox.domains.resident.ResidentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "reserve")
public class ReserveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="number")
    private String number;

    @ManyToOne
    @JoinColumn(name = "id_resident")
    @NotNull
    private ResidentEntity resident;

    @ManyToOne
    @JoinColumn(name="id_locker")
    @NotNull
    private LockerEntity locker;

    @ManyToOne
    @JoinColumn(name = "id_enterprise")
    @NotNull
    private EnterpriseEntity enterprise;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_status")
    @NotNull
    private ReserveStatus reserveStatus = ReserveStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    @NotNull
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name="date_reserve")
    @NotNull
    private String dateReserve;

    @Column(name="date_pickup")
    private String datePickup;


    public ReserveEntity(String number, ResidentEntity resident, LockerEntity locker, EnterpriseEntity enterprise, String dateReserve) {
        this.number = number;
        this.resident = resident;
        this.locker = locker;
        this.enterprise = enterprise;
        this.dateReserve = dateReserve;
    }

    public ReserveEntity() {
    }



    /*Getters and Setters*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ResidentEntity getResident() {
        return resident;
    }

    public void setResident(ResidentEntity resident) {
        this.resident = resident;
    }

    public LockerEntity getLocker() {
        return locker;
    }

    public void setLocker(LockerEntity locker) {
        this.locker = locker;
    }

    public EnterpriseEntity getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(EnterpriseEntity enterprise) {
        this.enterprise = enterprise;
    }

    public ReserveStatus getReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(ReserveStatus reserveStatus) {
        this.reserveStatus = reserveStatus;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getDateReserve() {
        return dateReserve;
    }

    public void setDateReserve(String dateReserve) {
        this.dateReserve = dateReserve;
    }

    public String getDatePickup() {
        return datePickup;
    }

    public void setDatePickup(String datePickup) {
        this.datePickup = datePickup;
    }
}
