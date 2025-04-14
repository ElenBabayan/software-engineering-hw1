package com.example.natssubscriber.service;


import com.example.natssubscriber.data.NatsMessage;
import com.example.natssubscriber.data.NatsMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NatsMessageServiceTest {

    private NatsMessageRepository natsMessageRepository;
    private NatsMessageService natsMessageService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        natsMessageRepository = Mockito.mock(NatsMessageRepository.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        natsMessageService = new NatsMessageService(natsMessageRepository, objectMapper);

    }

    @Test
    public void testProcessValidMessage() {
        String validJson = "{ \"content\": \"Hello from Nats, with timestamp\", \"timestamp\": \"2025-04-14T10:15:30\" }";
        String validMessage = "Hello from Nats, with timestamp";
        NatsMessage savedMessage = new NatsMessage();
        savedMessage.setId(1L);
        savedMessage.setContent(validMessage);
        savedMessage.setTimestamp(LocalDateTime.now());

        when(natsMessageRepository.save(any(NatsMessage.class))).thenReturn(savedMessage);

        natsMessageService.processMessage(validJson);

        // Capture the NatsMessage instance passed to save()
        ArgumentCaptor<NatsMessage> messageCaptor = ArgumentCaptor.forClass(NatsMessage.class);
        verify(natsMessageRepository, times(1)).save(messageCaptor.capture());
        NatsMessage captured = messageCaptor.getValue();

        assertEquals(validMessage, captured.getContent());
        assertNotNull(captured.getTimestamp(), "Timestamp should be automatically assigned");
    }

    @Test
    public void testProcessEmptyMessage() {
        natsMessageService.processMessage("    "); // only whitespace
        verify(natsMessageRepository, never()).save(any());
    }

    @Test
    public void testProcessNullMessage() {
        natsMessageService.processMessage(null);
        verify(natsMessageRepository, never()).save(any());
    }

    @Test
    public void testRepositoryThrowsException() {
        String validMessage = "Message that triggers repository exception";

        // Simulate an exception from the repository
        when(natsMessageRepository.save(any(NatsMessage.class)))
                .thenThrow(new RuntimeException("DB Error"));

        assertDoesNotThrow(() -> natsMessageService.processMessage(validMessage),
                "Service should handle repository exceptions gracefully");
    }
}
