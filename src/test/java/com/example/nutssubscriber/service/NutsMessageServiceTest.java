package com.example.nutssubscriber.service;


import com.example.nutssubscriber.data.NutsMessage;
import com.example.nutssubscriber.data.NutsMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NutsMessageServiceTest {

    private NutsMessageRepository nutsMessageRepository;
    private NutsMessageService nutsMessageService;

    @BeforeEach
    public void setUp() {
        nutsMessageRepository = Mockito.mock(NutsMessageRepository.class);
        nutsMessageService = new NutsMessageService(nutsMessageRepository);
    }

    @Test
    public void testProcessValidMessage() {
        String validJson = "{ \"content\": \"Hello from Nuts, with timestamp\", \"timestamp\": \"2025-04-14T10:15:30\" }";
        String validMessage = "Hello from Nuts, with timestamp";
        NutsMessage savedMessage = new NutsMessage();
        savedMessage.setId(1L);
        savedMessage.setContent(validMessage);
        savedMessage.setTimestamp(LocalDateTime.now());

        when(nutsMessageRepository.save(any(NutsMessage.class))).thenReturn(savedMessage);

        nutsMessageService.processMessage(validJson);

        // Capture the NutsMessage instance passed to save()
        ArgumentCaptor<NutsMessage> messageCaptor = ArgumentCaptor.forClass(NutsMessage.class);
        verify(nutsMessageRepository, times(1)).save(messageCaptor.capture());
        NutsMessage captured = messageCaptor.getValue();

        assertEquals(validMessage, captured.getContent());
        assertNotNull(captured.getTimestamp(), "Timestamp should be automatically assigned");
    }

    @Test
    public void testProcessEmptyMessage() {
        nutsMessageService.processMessage("    "); // only whitespace
        verify(nutsMessageRepository, never()).save(any());
    }

    @Test
    public void testProcessNullMessage() {
        nutsMessageService.processMessage(null);
        verify(nutsMessageRepository, never()).save(any());
    }

    @Test
    public void testRepositoryThrowsException() {
        String validMessage = "Message that triggers repository exception";

        // Simulate an exception from the repository
        when(nutsMessageRepository.save(any(NutsMessage.class)))
                .thenThrow(new RuntimeException("DB Error"));

        assertDoesNotThrow(() -> nutsMessageService.processMessage(validMessage),
                "Service should handle repository exceptions gracefully");
    }
}
