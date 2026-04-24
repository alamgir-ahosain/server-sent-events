package com.alamgir.sse.dto.request;


import com.alamgir.sse.dto.enums.ALERT_TYPE;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AlertCreateRequest {

    @JsonAlias("username")
    @NotBlank
    @Email
    private String email;

    @NotNull
    private ALERT_TYPE type;

    @NotBlank
    private String description;
}


