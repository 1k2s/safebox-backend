package com.senai.safebox.domains.resident.exceptions;

public class CPFAlreadyRegisteredException extends RuntimeException{
    public CPFAlreadyRegisteredException(String message) {
        super(message);
    }
}
