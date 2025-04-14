package com.example.nutssubscriber.service;

import com.example.nutssubscriber.data.NutsMessage;
import com.example.nutssubscriber.data.NutsMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class NutsMessageService {

    private static final Logger logger = LoggerFactory.getLogger(NutsMessageService.class);
    private final NutsMessageRepository nutsMessageRepository;
    private final JsonParser jsonParser;

    public NutsMessageService(NutsMessageRepository nutsMessageRepository) {
        this.nutsMessageRepository = nutsMessageRepository;
        this.jsonParser = new JsonParser();
    }

    /**
     * Validate and process a received JSON message.
     * @param jsonPayload The message payload as a JSON string.
     */
    public void processMessage(String jsonPayload) {
        if (!StringUtils.hasText(jsonPayload)) {
            logger.warn("Received empty or null payload. Ignoring.");
            return;
        }

        // Attempt to parse the JSON. If invalid, log and return.
        ParsedMessage parsed;
        try {
            parsed = jsonParser.parse(jsonPayload);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JSON message: {}. Error: {}", jsonPayload, e.getMessage());
            return;
        }

        // Further validation – e.g., we consider 'content' a required field
        if (!StringUtils.hasText(parsed.content)) {
            logger.warn("JSON payload missing content. Ignoring message: {}", jsonPayload);
            return;
        }

        // Build entity to persist
        NutsMessage message = new NutsMessage();
        message.setContent(parsed.content);
        message.setTimestamp(parsed.timestamp != null ? parsed.timestamp : LocalDateTime.now());

        NutsMessage savedMessage = nutsMessageRepository.save(message);
        logger.info("Message persisted with id: {} and content: {}", savedMessage.getId(), savedMessage.getContent());
    }

    /**
     * Illustrative helper class or method to parse JSON.
     */
    private static class JsonParser {
        ParsedMessage parse(String json) {
            if (!json.contains("\"content\":")) {
                throw new IllegalArgumentException("Missing 'content' field");
            }
            ParsedMessage pm = new ParsedMessage();
            pm.content = extractValue(json, "content");
            pm.timestamp = parseTimestamp(extractValue(json, "timestamp"));
            return pm;
        }

        private String extractValue(String json, String fieldName) {
            // Example: find `"fieldName": "someValue"`
            String key = "\"" + fieldName + "\":";
            int start = json.indexOf(key);
            if (start < 0) return null;
            int valStart = json.indexOf("\"", start + key.length());
            int valEnd = json.indexOf("\"", valStart + 1);
            if (valStart < 0 || valEnd < 0) return null;
            return json.substring(valStart + 1, valEnd);
        }

        private LocalDateTime parseTimestamp(String timestampStr) {
            if (!StringUtils.hasText(timestampStr)) {
                return null;
            }
            return LocalDateTime.parse(timestampStr);
        }
    }

    /**
     * DTO-like structure holding parsed data fields from the JSON message.
     */
    private static class ParsedMessage {
        String content;
        LocalDateTime timestamp;
    }
}
