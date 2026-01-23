package com.senai.safebox.brokerMQTT.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para RECEBER reserveStatus do ESP32
 *
 * JSON recebido no tópico "reserveStatus/box":
 * {
 *   "idBox": 1,
 *   "morador": "João Silva",
 *   "reserveStatus": "entregue",
 *   "timestamp": 1234567890
 * }
 */
public class StatusResponse {

    @JsonProperty("idBox")
    private Integer idBox;

    @JsonProperty("morador")
    private String morador;

    @JsonProperty("reserveStatus")
    private String status;

    @JsonProperty("timestamp")
    private Long timestamp;

    // Construtores
    public StatusResponse() {
    }

    public StatusResponse(Integer idBox, String morador, String status, Long timestamp) {
        this.idBox = idBox;
        this.morador = morador;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters e Setters
    public Integer getIdBox() {
        return idBox;
    }

    public void setIdBox(Integer idBox) {
        this.idBox = idBox;
    }

    public String getMorador() {
        return morador;
    }

    public void setMorador(String morador) {
        this.morador = morador;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    // toString para debug
    @Override
    public String toString() {
        return "StatusResponse{" +
                "idBox=" + idBox +
                ", morador='" + morador + '\'' +
                ", reserveStatus='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}