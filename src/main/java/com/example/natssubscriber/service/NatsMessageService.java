package com.example.natssubscriber.service;

import com.example.natssubscriber.data.NatsMessage;
import com.example.natssubscriber.data.NatsMessageRepository;
import com.example.natssubscriber.data.ParsedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class NatsMessageService {

    private static final Logger logger = LoggerFactory.getLogger(NatsMessageService.class);
    private final NatsMessageRepository natsMessageRepository;
    private final ObjectMapper objectMapper;

    public NatsMessageService(NatsMessageRepository natsMessageRepository, ObjectMapper objectMapper) {
        this.natsMessageRepository = natsMessageRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Process a received JSON message payload.
     * @param jsonPayload the raw JSON message string
     */

    public void processMessage(String jsonPayload) {
        if (!StringUtils.hasText(jsonPayload)) {
            logger.warn("Received empty or null payload. Ignoring.");
            return;
        }

        ParsedMessage parsed;
        try {
            parsed = objectMapper.readValue(jsonPayload, ParsedMessage.class);
        } catch (Exception e) {
            logger.error("Failed to parse JSON payload: {}", jsonPayload, e);
            return;
        }

        if (!StringUtils.hasText(parsed.getContent())) {
            logger.warn("Payload missing required 'content'. Ignoring: {}", jsonPayload);
            return;
        }

        NatsMessage message = new NatsMessage();
        message.setContent(parsed.getContent());
        message.setTimestamp(parsed.getTimestamp() != null ? parsed.getTimestamp() : LocalDateTime.now());

        NatsMessage saved = natsMessageRepository.save(message);
        logger.info("Message persisted with ID {}: {}", saved.getId(), saved.getContent());
    }

}
