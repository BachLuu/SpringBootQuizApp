package com.example.springbootweb.entities.dtos.email;

import java.util.Map;

public record SendEmailRequest(
        String to,
        String subject,
        String templateCode,
        Map<String, Object> props) {
}