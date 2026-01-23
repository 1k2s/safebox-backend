package com.senai.safebox.config;

import com.senai.safebox.domains.house.exceptions.HouseNotFoundException;
import com.senai.safebox.domains.resident.exceptions.CPFAlreadyRegisteredException;
import com.senai.safebox.domains.user.exceptions.UserAlreadyRegisteredException;
import com.senai.safebox.domains.user.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Anotattion que lida com exceções, o Rest significa que as respostas serão convertidas em JSON
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Informando ao spring que esse metodo será chamado sempre que HouseNotFoundException for lançada
    @ExceptionHandler(HouseNotFoundException.class)
    public ResponseEntity<String> handleHouseNotFound(HouseNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) //Status 404
                .body(exception.getMessage());       //mensagem
    }

    @ExceptionHandler(CPFAlreadyRegisteredException.class)
    public ResponseEntity<String> handleCpfAlreadyRegistered(CPFAlreadyRegisteredException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //Status 409
                .body(exception.getMessage());       //mensagem
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<String> UserAlreadyRegistered(UserAlreadyRegisteredException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //Status 409
                .body(exception.getMessage());       //mensagem
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> UserNotFound( UserNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

}
