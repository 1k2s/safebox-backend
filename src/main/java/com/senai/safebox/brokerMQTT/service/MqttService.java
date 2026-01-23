package com.senai.safebox.brokerMQTT.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.safebox.brokerMQTT.dto.ReservaRequest;
import com.senai.safebox.brokerMQTT.dto.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela comunicação MQTT com ESP32
 *
 * Funções principais:
 * 1. Enviar reservas para o ESP32
 * 2. Receber confirmações de entrega do ESP32
 * 3. Processar callbacks de reserveStatus
 * 4. Enviar mensagens e aguardar respostas
 */
@Service
public class MqttService {
    private static final Logger log = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    private MessageChannel mqttOutboundChannel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Controle de resposta para comunicação síncrona
    private String ultimaResposta = null;
    private final Object lock = new Object();
    private volatile boolean aguardandoResposta = false;

    // ========== ENVIAR RESERVA PARA O ESP32 ==========

    /**
     * Publica uma reserva no tópico MQTT
     *
     * Fluxo:
     * 1. Valida os dados da reserva
     * 2. Converte para JSON
     * 3. Envia para o canal "mqttOutboundChannel"
     * 4. O handler publica no broker MQTT
     * 5. ESP32 recebe e processa
     *
     * @param reserva Dados da reserva (morador, senha, idBox)
     * @return true se enviado com sucesso
     */
    public boolean enviarReserva(ReservaRequest reserva) {
        try {
            // Validação
            if (!reserva.isValid()) {
                log.error("❌ Reserva inválida: {}", reserva);
                return false;
            }

            // Converte para JSON
            String jsonPayload = objectMapper.writeValueAsString(reserva);
            log.info("📤 Enviando reserva para ESP32:");
            log.info("   Morador: {}", reserva.getMorador());
            log.info("   Box ID: {}", reserva.getIdBox());
            log.info("   JSON: {}", jsonPayload);

            // Cria mensagem com headers MQTT
            Message<String> message = MessageBuilder
                    .withPayload(jsonPayload)
                    .setHeader(MqttHeaders.TOPIC, "reserva/box")
                    .setHeader(MqttHeaders.QOS, 1)
                    .build();

            // Envia para o canal (que publica no MQTT)
            boolean enviado = mqttOutboundChannel.send(message);

            if (enviado) {
                log.info("✅ Reserva enviada com sucesso!");
            } else {
                log.error("❌ Falha ao enviar reserva");
            }

            return enviado;

        } catch (Exception e) {
            log.error("❌ Erro ao enviar reserva: {}", e.getMessage(), e);
            return false;
        }
    }

    // ========== ENVIAR MENSAGEM E AGUARDAR RESPOSTA ==========

    /**
     * Envia mensagem para reserveStatus/reserva e aguarda resposta de reserveStatus/reserva/response
     *
     * @param mensagem Mensagem a ser enviada
     * @param timeoutMs Tempo máximo de espera em milissegundos
     * @return Resposta recebida ou null se timeout
     */
    public String enviarEaguardarResposta(String mensagem, long timeoutMs) {
        try {
            synchronized (lock) {
                // Prepara para receber resposta
                ultimaResposta = null;
                aguardandoResposta = true;

                log.info("📤 Enviando mensagem para reserveStatus/reserva: {}", mensagem);

                // Cria mensagem MQTT
                Message<String> message = MessageBuilder
                        .withPayload(mensagem)
                        .setHeader(MqttHeaders.TOPIC, "reserveStatus/reserva")
                        .setHeader(MqttHeaders.QOS, 1)
                        .build();

                // Envia mensagem
                boolean enviado = mqttOutboundChannel.send(message);

                if (!enviado) {
                    log.error("❌ Falha ao enviar mensagem");
                    aguardandoResposta = false;
                    return null;
                }

                log.info("⏳ Aguardando resposta (timeout: {}ms)...", timeoutMs);

                // Aguarda resposta com timeout
                lock.wait(timeoutMs);

                aguardandoResposta = false;

                if (ultimaResposta != null) {
                    log.info("✅ Resposta recebida: {}", ultimaResposta);
                } else {
                    log.warn("⚠️ Timeout - Nenhuma resposta recebida");
                }

                return ultimaResposta;
            }

        } catch (InterruptedException e) {
            log.error("❌ Thread interrompida: {}", e.getMessage());
            Thread.currentThread().interrupt();
            aguardandoResposta = false;
            return null;
        } catch (Exception e) {
            log.error("❌ Erro ao enviar/aguardar resposta: {}", e.getMessage(), e);
            aguardandoResposta = false;
            return null;
        }
    }

    // ========== RECEBER STATUS DO ESP32 ==========

    /**
     * Callback automático quando ESP32 publica no tópico "reserveStatus/box"
     *
     * Este método é acionado automaticamente pelo Spring Integration
     * quando uma mensagem chega no canal "mqttInputChannel"
     *
     * @ServiceActivator escuta o canal e executa este método
     *
     * Fluxo:
     * 1. ESP32 publica JSON no tópico "reserveStatus/box"
     * 2. Adapter MQTT recebe e coloca no canal
     * 3. Este método é acionado automaticamente
     * 4. Processa o reserveStatus e executa ações necessárias
     *
     * @param message Mensagem recebida do MQTT
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void receberStatus(Message<?> message) {
        try {
            String payload = message.getPayload().toString();
            String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);

            log.info("📥 Mensagem recebida:");
            log.info("   Tópico: {}", topic);
            log.info("   Payload: {}", payload);

            // Se é uma resposta do reserveStatus/reser/response
            if ("reserveStatus/reser/response".equals(topic) && aguardandoResposta) {
                synchronized (lock) {
                    ultimaResposta = payload;
                    lock.notify();
                }
                log.info("✅ Resposta de reserveStatus capturada");
                return;
            }

            // Se é um reserveStatus normal do box
            if ("reserveStatus/box".equals(topic)) {
                StatusResponse status = objectMapper.readValue(payload, StatusResponse.class);

                log.info("   Box ID: {}", status.getIdBox());
                log.info("   Morador: {}", status.getMorador());
                log.info("   Status: {}", status.getStatus());
                log.info("   Timestamp: {}", status.getTimestamp());

                // Processa o reserveStatus recebido
                processarStatus(status);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }

    /**
     * Processa o reserveStatus de entrega recebido
     *
     * Aqui você pode:
     * - Atualizar banco de dados
     * - Enviar notificação ao morador
     * - Registrar log de auditoria
     * - Liberar o box no sistema
     *
     * @param status Status recebido do ESP32
     */
    private void processarStatus(StatusResponse status) {
        log.info("⚙️ Processando entrega:");

        if ("entregue".equals(status.getStatus())) {
            // TODO: Aqui você pode:
            // 1. Atualizar reserveStatus no banco de dados
            // 2. Enviar notificação para o morador
            // 3. Gerar relatório de entrega
            // 4. Liberar o box para novas reservas

            log.info("✅ Box {} liberado - Entrega confirmada para {}",
                    status.getIdBox(), status.getMorador());

            // Exemplo: chamar outro serviço
            // reservaRepository.finalizarReserva(reserveStatus.getIdBox());
            // notificacaoService.enviarEmail(reserveStatus.getMorador(), "Entrega confirmada!");
        }
    }

    // ========== DESBLOQUEIO REMOTO ==========

    /**
     * Envia comando de desbloqueio remoto para o ESP32
     *
     * @param idBox ID do box a ser desbloqueado
     * @return true se enviado com sucesso
     */
    public boolean desbloquearBox(Long idBox) {
        try {
            log.info("🔓 Enviando comando de desbloqueio para box {}", idBox);

            // Cria JSON com o comando de desbloqueio
            String jsonPayload = String.format("{\"idBox\":%d,\"comando\":\"desbloquear\"}", idBox);

            log.info("📤 Payload: {}", jsonPayload);

            // Cria mensagem MQTT
            Message<String> message = MessageBuilder
                    .withPayload(jsonPayload)
                    .setHeader(MqttHeaders.TOPIC, "desbloqueio/box")
                    .setHeader(MqttHeaders.QOS, 1)
                    .build();

            // Envia mensagem
            boolean enviado = mqttOutboundChannel.send(message);

            if (enviado) {
                log.info("✅ Comando de desbloqueio enviado com sucesso!");
            } else {
                log.error("❌ Falha ao enviar comando de desbloqueio");
            }

            return enviado;

        } catch (Exception e) {
            log.error("❌ Erro ao desbloquear box: {}", e.getMessage(), e);
            return false;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Verifica se o sistema está conectado ao broker MQTT
     *
     * @return true se conectado
     */
    public boolean isConectado() {
        // Este método pode ser expandido para verificar conexão real
        return mqttOutboundChannel != null;
    }
}