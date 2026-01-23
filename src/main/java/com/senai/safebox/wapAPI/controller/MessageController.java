package com.senai.safebox.wapAPI.controller;


import com.senai.safebox.wapAPI.domain.MessageEntity;
import com.senai.safebox.wapAPI.dtos.request.MessageRequestDTO;
import com.senai.safebox.wapAPI.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wap")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<MessageEntity> createMessage(@RequestBody MessageRequestDTO messageRequestDTO) {


        MessageEntity newMessage = messageService.saveMessage(messageRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newMessage);
    };

}
