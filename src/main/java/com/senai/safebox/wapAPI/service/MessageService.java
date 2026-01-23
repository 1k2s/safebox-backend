package com.senai.safebox.wapAPI.service;

import com.senai.safebox.wapAPI.client.MessageClient;
import com.senai.safebox.wapAPI.domain.MessageEntity;
import com.senai.safebox.wapAPI.dtos.request.MessageRequestDTO;
import com.senai.safebox.wapAPI.dtos.response.MessageResponseDTO;
import com.senai.safebox.wapAPI.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageClient messageClient;

    /*Injeção de dependência via constructor*/
    public MessageService(MessageRepository messageRepository, MessageClient messageClient) {
        this.messageRepository = messageRepository;
        this.messageClient = messageClient;
    }


    public MessageEntity saveMessage(MessageRequestDTO messageRequestDTO) {


        /*Enviando requisição para api*/
        MessageResponseDTO response = messageClient.sendMessage(messageRequestDTO);

        /*Formatando a data*/
        String dataFormatada = Instant.ofEpochSecond(response.message().timestamp())
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        MessageEntity newMessageEntity = new MessageEntity(response.message().recipient(), response.message().message(), response.success(), dataFormatada);

        return messageRepository.save(newMessageEntity);

    }

}
