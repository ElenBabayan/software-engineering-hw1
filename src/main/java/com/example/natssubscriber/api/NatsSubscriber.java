package com.example.natssubscriber.api;

import com.example.natssubscriber.service.NatsMessageService;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public class NatsSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(NatsSubscriber.class);
    private final NatsMessageService natsMessageService;
    private Connection nc;
    private Dispatcher dispatcher;

    public NatsSubscriber(NatsMessageService natsMessageService) {
        this.natsMessageService = natsMessageService;
    }

    @PostConstruct
    public void initializeNatsConnection() {
        try (Connection nc = Nats.connect("nats://localhost:4222")) {
            dispatcher = nc.createDispatcher();

            String subject = "{ \"content\": \"Hello from Nats, with timestamp\", \"timestamp\": \"2025-04-14T10:15:30\" }";
            Subscription subscription = dispatcher.subscribe(subject, msg -> logger.info("Subscription received message " + msg));

            natsMessageService.processMessage(subscription.getSubject());

            dispatcher.subscribe("nats.subject.messages");

            logger.info("Subscribed to NATS subject 'nats.subject.messages'");
        } catch (Exception e) {
            logger.error("Failed to connect/subscribe to NATS", e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void keepConnectionAlive() {
        if (nc == null || nc.getStatus() != Connection.Status.CONNECTED) {
            logger.warn("NATS connection is not active. Attempting to reconnect...");
            initializeNatsConnection();
        }
    }
}