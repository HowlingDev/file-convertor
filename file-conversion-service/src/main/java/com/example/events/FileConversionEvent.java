package com.example.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public class FileConversionEvent {

    private UUID eventId;
    private String message;
    private Status status;

    public enum Status {
        CONVERTED,
        FAILED
    }
}
