package com.senai.safebox.brokerMQTT.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Configuração do MQTT para comunicação com ESP32
 * <p>
 * Esta classe configura:
 * 1. Conexão com broker HiveMQ
 * 2. Canal para ENVIAR mensagens (outbound)
 * 3. Canal para RECEBER mensagens (inbound)
 * 4. Suporte para comunicação síncrona (enviar e aguardar resposta)*/


@Configuration
public class MqttConfig {

    // ========== CONFIGURAÇÕES DO BROKER ==========
    private static final String BROKER_URL = "ssl://1ad18fc1c34546db8dc862c216b0df80.s1.eu.hivemq.cloud:8883";
    private static final String CLIENT_ID = "SpringBootServer";
    private static final String USERNAME = "backend";
    private static final String PASSWORD = "12345678Aa";

    // ========== TÓPICOS MQTT ==========
    public static final String TOPIC_RESERVA = "reserva/box";        // Spring -> ESP32 (reservas)
    public static final String TOPIC_STATUS = "reserveStatus/box";          // ESP32 -> Spring (reserveStatus de entrega)
    public static final String TOPIC_STATUS_REQUEST = "reserveStatus/reserva";      // Spring -> ESP32 (consultas)
    public static final String TOPIC_STATUS_RESPONSE = "reserveStatus/reserva/response"; // ESP32 -> Spring (respostas)
    public static final String TOPIC_DESBLOQUEAR = "desbloqueio/box";     // Spring -> ESP32 (desbloqueio remoto)

/**
     * PASSO 1: Configurar a conexão com o broker MQTT
     * <p>
     * Este bean cria uma fábrica de clientes MQTT com:
     * - URL do broker (HiveMQ Cloud com SSL)
     * - Credenciais de autenticação
     * - Configurações de timeout e reconexão*/


    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        // Configuração básica
        options.setServerURIs(new String[]{BROKER_URL});
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        // Configurações de conexão
        options.setConnectionTimeout(30);      // Timeout de 30 segundos
        options.setKeepAliveInterval(60);      // Keep-alive de 60 segundos
        options.setAutomaticReconnect(true);   // Reconecta automaticamente
        options.setCleanSession(true);         // Limpa sessão antiga

        factory.setConnectionOptions(options);
        return factory;
    }

    // ========== CANAL DE SAÍDA (SPRING -> ESP32) ==========


/*     * PASSO 2: Canal para ENVIAR mensagens
     * <p>
     * Canal direto usado para publicar mensagens no broker*/


    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

/*
     * PASSO 3: Handler para ENVIAR mensagens ao broker
     * <p>
     * Este handler:
     * - Conecta ao broker usando a factory
     * - Publica mensagens no tópico especificado
     * - É acionado quando mensagens chegam no canal outbound
     *
     * @ServiceActivator escuta o canal "mqttOutboundChannel"
     * Quando você envia algo para esse canal, ele publica no MQTT*/


    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(CLIENT_ID + "_pub", mqttClientFactory());

        messageHandler.setAsync(true);              // Publica de forma assíncrona
        messageHandler.setDefaultTopic(TOPIC_RESERVA); // Tópico padrão
        messageHandler.setDefaultQos(1);            // QoS 1 = entrega garantida

        return messageHandler;
    }

    // ========== CANAL DE ENTRADA (ESP32 -> SPRING) ==========

/**
     * PASSO 4: Canal para RECEBER mensagens
     * <p>
     * Canal direto que recebe mensagens do broker*/


    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

/**
     * PASSO 5: Adapter para RECEBER mensagens do broker
     * <p>
     * Este adapter:
     * - Se inscreve em MÚLTIPLOS tópicos:
     *   * reserveStatus/box - Status de entregas realizadas
     *   * reserveStatus/reserva/response - Respostas para consultas de reserveStatus
     * - Recebe mensagens publicadas pelo ESP32
     * - Envia para o canal "mqttInputChannel"
     * <p>
     * É um listener que fica esperando mensagens chegarem*/


    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        CLIENT_ID + "_sub",
                        mqttClientFactory(),
                        TOPIC_STATUS,           // Tópico 1: reserveStatus de entregas
                        TOPIC_STATUS_RESPONSE   // Tópico 2: respostas de consultas
                );

        adapter.setCompletionTimeout(5000);     // Timeout de 5 segundos
        adapter.setConverter(new DefaultPahoMessageConverter()); // Converte payload
        adapter.setQos(1);                      // QoS 1 = entrega garantida
        adapter.setOutputChannel(mqttInputChannel()); // Envia para este canal

        return adapter;
    }

    // ========== DOCUMENTAÇÃO DOS FLUXOS ==========

/**
     * FLUXO 1 - CRIAR RESERVA:
     * 1. Controller recebe POST /api/box/reservar
     * 2. MqttService.enviarReserva() envia para mqttOutboundChannel
     * 3. mqttOutbound() publica em "reserva/box"
     * 4. ESP32 recebe e processa
     * 5. ESP32 confirma publicando em "reserveStatus/box"
     * 6. mqttInbound() recebe e envia para mqttInputChannel
     * 7. MqttService.receberStatus() processa a confirmação
     *
     * FLUXO 2 - CONSULTAR STATUS:
     * 1. Controller recebe POST /api/box/enviar-reserveStatus
     * 2. MqttService.enviarEaguardarResposta() envia para mqttOutboundChannel
     * 3. mqttOutbound() publica em "reserveStatus/reserva"
     * 4. ESP32 recebe e responde em "reserveStatus/reserva/response"
     * 5. mqttInbound() recebe e envia para mqttInputChannel
     * 6. MqttService.receberStatus() identifica como resposta e notifica thread
     * 7. MqttService retorna resposta para o Controller
     *
     * FLUXO 3 - DESBLOQUEIO REMOTO:
     * 1. Controller recebe POST /api/box/desbloquear
     * 2. MqttService.desbloquearBox() envia para mqttOutboundChannel
     * 3. mqttOutbound() publica em "desbloqueio/box"
     * 4. ESP32 recebe e executa desbloqueio imediato*/


}
