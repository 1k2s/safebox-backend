package com.senai.safebox.wapAPI.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageRequestDTO(
        @JsonProperty("chatId")
        String recipient,

        @JsonProperty("contentType")
        String contentType,

        @JsonProperty("content")
        String message
) {}