package com.example.Bajaj.dto;

import lombok.Data;

@Data
public class WebhookResponse {
    private String webhookURL;
    private String accessToken;
}
