package com.example.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ConvertFileToPdfEvent {
    UUID eventId;
    String fileUrl;
}
