package com.senai.safebox.wapAPI.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageDTO (

        @JsonProperty("body")
        String message,

        @JsonProperty("timestamp")
        Long timestamp,

        @JsonProperty("to")
        String recipient
) {}
