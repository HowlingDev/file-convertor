package com.example.events;

import java.util.UUID;

public class FileConvertedEvent {

    UUID eventId;
    String pdfUrl;

    public FileConvertedEvent(UUID eventId, String pdfUrl) {
        this.eventId = eventId;
        this.pdfUrl = pdfUrl;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }
}
