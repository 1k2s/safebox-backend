package com.senai.safebox.brokerMQTT.controller;

import com.senai.safebox.brokerMQTT.dto.ReservaRequest;
import com.senai.safebox.brokerMQTT.service.MqttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para gerenciar boxes
 *
 * Endpoints disponíveis:
 * - POST /api/box/reservar : Criar nova reserva
 * - GET  /api/box/reserveStatus   : Verificar reserveStatus do sistema
 * - POST /api/box/enviar-reserveStatus : Envia mensagem e aguarda resposta
 */
@RestController
@RequestMapping("/api/box")
@CrossOrigin(origins = "*")// Permite CORS (ajuste conforme necessário)
public class BoxController {

    private static final Logger log = LoggerFactory.getLogger(BoxController.class);

    @Autowired
    private MqttService mqttService;

    /**
     * Endpoint para criar uma reserva de box
     *
     * POST http://localhost:8080/api/box/reservar
     *
     * Body (JSON):
     * {
     *   "morador": "João Silva",
     *   "senha": "1234",
     *   "idBox": 1
     * }
     *
     * Resposta sucesso:
     * {
     *   "sucesso": true,
     *   "mensagem": "Reserva enviada com sucesso!",
     *   "dados": { ... }
     * }
     *
     * Resposta erro:
     * {
     *   "sucesso": false,
     *   "mensagem": "Erro ao enviar reserva",
     *   "erro": "Detalhes do erro"
     * }
     */

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> criarReserva(
            @RequestBody ReservaRequest reserva) {

        log.info("📨 Recebida requisição de reserva: {}", reserva);

        Map<String, Object> response = new HashMap<>();

        try {
            // Validação básica
            if (!reserva.isValid()) {
                response.put("sucesso", false);
                response.put("mensagem", "Dados inválidos");
                response.put("erro", "Verifique: morador, senha (4 dígitos) e idBox");
                return ResponseEntity.badRequest().body(response);
            }

            // Envia via MQTT
            boolean enviado = mqttService.enviarReserva(reserva);

            if (enviado) {
                Map<String, Object> dados = new HashMap<>();
                dados.put("morador", reserva.getMorador());
                dados.put("idBox", reserva.getIdBox());

                response.put("sucesso", true);
                response.put("mensagem", "Reserva enviada com sucesso!");
                response.put("dados", dados);
                return ResponseEntity.ok(response);
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao enviar reserva");
                response.put("erro", "Verifique conexão MQTT");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Erro no controller: {}", e.getMessage(), e);
            response.put("sucesso", false);
            response.put("mensagem", "Erro interno no servidor");
            response.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para enviar mensagem e aguardar resposta
     *
     * POST http://localhost:8080/api/box/enviar-status
     *
     * Body (JSON):
     * {
     *   "mensagem": "Teste de reserveStatus"
     * }
     *
     * Ou com query param:
     * POST http://localhost:8080/api/box/enviar-status?mensagem=teste
     *
     * Resposta:
     * {
     *   "sucesso": true,
     *   "mensagemEnviada": "Teste de reserveStatus",
     *   "resposta": "Resposta do ESP32",
     *   "tempoEspera": 5000
     * }
     */

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/enviar-reserveStatus")
    public ResponseEntity<Map<String, Object>> enviarStatus(
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(required = false) String mensagem) {

        log.info("📨 Recebida requisição enviar-reserveStatus");

        Map<String, Object> response = new HashMap<>();

        try {
            // Pega mensagem do body ou query param
            String msg = (body != null && body.containsKey("mensagem"))
                    ? body.get("mensagem")
                    : mensagem;

            if (msg == null || msg.trim().isEmpty()) {
                msg = "ping"; // Mensagem padrão
            }

            log.info("📤 Enviando para reserveStatus/reserva: {}", msg);

            // Envia e aguarda resposta (timeout de 5 segundos)
            String resposta = mqttService.enviarEaguardarResposta(msg, 5000);

            if (resposta != null) {
                response.put("sucesso", true);
                response.put("mensagemEnviada", msg);
                response.put("resposta", resposta);
                response.put("tempoEspera", 5000);
                return ResponseEntity.ok(response);
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Timeout - Nenhuma resposta recebida");
                response.put("mensagemEnviada", msg);
                response.put("tempoEspera", 5000);
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao enviar reserveStatus: {}", e.getMessage(), e);
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao processar requisição");
            response.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para verificar reserveStatus do sistema
     *
     * GET http://localhost:8080/api/box/status
     *
     * Resposta:
     * {
     *   "reserveStatus": "online",
     *   "mqttConectado": true,
     *   "mensagem": "Sistema operacional"
     * }
     */

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/reserveStatus")
    public ResponseEntity<Map<String, Object>> verificarStatus() {
        Map<String, Object> response = new HashMap<>();

        boolean conectado = mqttService.isConectado();

        response.put("reserveStatus", conectado ? "online" : "offline");
        response.put("mqttConectado", conectado);
        response.put("mensagem", conectado ? "Sistema operacional" : "MQTT desconectado");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para desbloqueio remoto de box
     *
     * POST http://localhost:8080/api/box/desbloquear
     *
     * Body (JSON):
     * {
     *   "idBox": 1
     * }
     *
     * Resposta sucesso:
     * {
     *   "sucesso": true,
     *   "mensagem": "Comando de desbloqueio enviado",
     *   "idBox": 1
     * }
     */

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/desbloquear")
    public ResponseEntity<Map<String, Object>> desbloquearBox(
            @RequestBody Map<String, Object> body) {

        log.info("🔓 Recebida requisição de desbloqueio remoto");

        Map<String, Object> response = new HashMap<>();

        try {
            // Valida se idBox foi enviado
            if (!body.containsKey("idBox")) {
                response.put("sucesso", false);
                response.put("mensagem", "Campo 'idBox' é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }

            Long idBox = null;
            try {
                idBox = (long) Integer.parseInt(body.get("idBox").toString());
            } catch (NumberFormatException e) {
                response.put("sucesso", false);
                response.put("mensagem", "Campo 'idBox' deve ser um número");
                return ResponseEntity.badRequest().body(response);
            }

            log.info("🔓 Desbloqueando box ID: {}", idBox);

            // Envia comando de desbloqueio via MQTT
            boolean enviado = mqttService.desbloquearBox(idBox);

            if (enviado) {
                response.put("sucesso", true);
                response.put("mensagem", "Comando de desbloqueio enviado");
                response.put("idBox", idBox);
                return ResponseEntity.ok(response);
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Falha ao enviar comando");
                response.put("erro", "Verifique conexão MQTT");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            log.error("❌ Erro ao desbloquear box: {}", e.getMessage(), e);
            response.put("sucesso", false);
            response.put("mensagem", "Erro interno no servidor");
            response.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint de teste/ping
     *
     * GET http://localhost:8080/api/box/ping
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}