package com.senai.safebox.domains.house.exceptions;

public class HouseNotFoundException extends RuntimeException {
    public HouseNotFoundException(String message){
        super(message);
    }
}
