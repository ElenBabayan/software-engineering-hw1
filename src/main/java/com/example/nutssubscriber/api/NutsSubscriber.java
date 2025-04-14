package com.example.nutssubscriber.api;

import com.example.nutssubscriber.service.NutsMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class NutsSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(NutsSubscriber.class);
    private final NutsMessageService nutsMessageService;

    public NutsSubscriber(NutsMessageService nutsMessageService) {
        this.nutsMessageService = nutsMessageService;
    }

    /**
     * Simulate receiving a new Nuts message every 5 seconds.
     */
    @Scheduled(fixedDelay = 5000)
    public void subscribeAndProcessMessage() {
        String jsonPayload = String.format(
                "{\"id\": \"%s\", \"timestamp\": \"%s\", \"type\": \"INFO\", \"content\": \"Hello from Nuts!\"}",
                UUID.randomUUID(), LocalDateTime.now()
        );

        logger.info("Received message payload: {}", jsonPayload);

        // Pass the JSON payload to the service layer for validation & processing
        nutsMessageService.processMessage(jsonPayload);
    }
}