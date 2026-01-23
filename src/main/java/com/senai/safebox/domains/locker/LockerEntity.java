package com.senai.safebox.domains.locker;

import com.senai.safebox.domains.locker.enums.LockerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "locker")
public class LockerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    @NotNull
    private Long number;

    @Column(name = "isOpen")
    @NotNull
    private Boolean isOpen = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private LockerStatus status = LockerStatus.AVAILABLE;

    public LockerEntity(Long number, LockerStatus status) {
        this.number = number;
        this.status = status;
    }

    public LockerEntity(Long number) {
        this.number = number;
    }

    public LockerEntity() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Boolean getOpen() {
        return this.isOpen;
    }

    public void setOpen(Boolean open) {
        this.isOpen = open;
    }

    public LockerStatus getStatus() {
        return status;
    }

    public void setStatus(LockerStatus status) {
        this.status = status;
    }
}

