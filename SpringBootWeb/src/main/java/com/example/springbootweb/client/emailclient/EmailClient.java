package com.example.springbootweb.client.emailclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.springbootweb.entities.dtos.email.SendEmailRequest;

@FeignClient(name = "email-service", url = "${app.services.email.url}", fallback = EmailClientFallback.class)
public interface EmailClient {
    @PostMapping("/api/v1/emails/send")
    void sendEmail(@RequestBody SendEmailRequest request);
}
