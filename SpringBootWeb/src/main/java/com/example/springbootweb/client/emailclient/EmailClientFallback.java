package com.example.springbootweb.client.emailclient;

import org.springframework.stereotype.Component;

import com.example.springbootweb.entities.dtos.email.SendEmailRequest;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailClientFallback implements EmailClient {
    @Override
    public void sendEmail(SendEmailRequest request) {
        log.error("Email service is down! Giữ email lại vào DB để gửi sau.");
        // Logic xử lý khi lỗi (ví dụ: lưu vào database tạm)
    }
}