package com.senai.safebox.wapAPI.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageResponseDTO(

        @JsonProperty("success")
        boolean success,

        @JsonProperty("message")
        MessageDTO message
){}
