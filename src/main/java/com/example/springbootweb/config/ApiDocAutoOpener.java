package com.example.springbootweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.awt.*;
import java.net.URI;

public class ApiDocAutoOpener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port:8080}")
    private int port;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            if (!Desktop.isDesktopSupported()) return;
            String path = swaggerPath.startsWith("/") ? swaggerPath : ("/" + swaggerPath);
            Desktop.getDesktop().browse(new URI("http://localhost:" + port + path));
        } catch (Exception _) {
        }
    }
}
