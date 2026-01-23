package com.senai.safebox.brokerMQTT.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservaRequest {

    @JsonProperty("morador")
    private String morador;

    @JsonProperty("senha")
    private String senha;

    @JsonProperty("idBox")
    private Long idBox;

    // Construtores
    public ReservaRequest() {
    }

    public ReservaRequest(String morador, String senha, Long idBox) {
        this.morador = morador;
        this.senha = senha;
        this.idBox = idBox;
    }

    // Getters e Setters
    public String getMorador() {
        return morador;
    }

    public void setMorador(String morador) {
        this.morador = morador;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Long getIdBox() {
        return idBox;
    }

    public void setIdBox(Long idBox) {
        this.idBox = idBox;
    }

    // Validação básica
    @JsonIgnore  // ⬅️ ADICIONE ESTA ANOTAÇÃO
    public boolean isValid() {
        return morador != null && !morador.trim().isEmpty()
                && senha != null && senha.matches("\\d{4}")
                && idBox != null && idBox > 0;
    }

    @Override
    public String toString() {
        return "ReservaRequest{" +
                "morador='" + morador + '\'' +
                ", senha='****'" +
                ", idBox=" + idBox +
                '}';
    }
}