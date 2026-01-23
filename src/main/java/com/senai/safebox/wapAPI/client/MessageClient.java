package com.senai.safebox.wapAPI.client;

import com.senai.safebox.wapAPI.dtos.request.MessageRequestDTO;
import com.senai.safebox.wapAPI.dtos.response.MessageDTO;
import com.senai.safebox.wapAPI.dtos.response.MessageResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


//diz ao spring que a injeção de dependência dessa classe deverá ser de sua responsabilidade
@Component
public class MessageClient {

    // Modo utilizado para fazer consumo de APIs(existem 3)
    private final RestClient restClient;

    private static final String API_BASE_URL = "http://localhost:3000";

    private static final String SEND_MESSAGE_ENDPOINT = "/client/sendMessage/safebox";

    // Token de autenticação
    private static final String AUTH_TOKEN = "safebox";


    //Constructor da classe inicializando a classe de requisição
    public MessageClient() {
        this.restClient = RestClient.builder()
                .baseUrl(API_BASE_URL)
                .defaultHeader("x-api-key", AUTH_TOKEN)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)// Define que por padrão vamos enviar e aceitar JSON
                .build();
    }


    /*Metodo de configuração do envio da mensagem*/
    public MessageResponseDTO sendMessage(MessageRequestDTO messageRequestDTO) {
        try {
            MessageResponseDTO response = restClient
                    .post()
                    .uri(SEND_MESSAGE_ENDPOINT)
                    .body(messageRequestDTO)
                    .retrieve()// recupera a resposta
                    .body(MessageResponseDTO.class);// Deserializa o JSON da resposta para um objeto WhatsAppResponse utilizando o Jackson

          /*  System.out.println("Resposta recebida da API!");
            System.out.println("   " + response);*/

            return response;

        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            System.out.println(messageRequestDTO.toString());

            return new MessageResponseDTO(false, new MessageDTO(messageRequestDTO.message(), LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), messageRequestDTO.recipient()));
        }

    }

}
