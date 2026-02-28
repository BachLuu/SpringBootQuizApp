package com.example.service.email.dtos;

import java.util.Map;

public record SendEmailRequest(
        String to,
        String subject,
        String templateCode,
        Map<String, Object> props) {
}
