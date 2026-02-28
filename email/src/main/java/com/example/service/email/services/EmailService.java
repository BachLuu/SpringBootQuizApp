package com.example.service.email.services;

import com.example.service.email.dtos.SendEmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(SendEmailRequest request) {
        // Implement logic sending email here (JavaMailSender, etc.)
        // For now, just logging
        log.info("Sending email to: {}", request.to());
        log.info("Subject: {}", request.subject());
        log.info("Template: {}", request.templateCode());
        log.info("Props: {}", request.props());
    }
}
