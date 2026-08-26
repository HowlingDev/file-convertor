package com.example.services;

import com.example.converters.Converter;
import com.example.entities.InboxEntity;
import com.example.entities.OutboxEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.events.FileConversionEvent;
import com.example.repositories.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaFileListenerService {

    private final InboxRepository inboxRepository;
    private final MinioService minioService;
    private final List<Converter> converters;
    private final FileConversionService fileConversionService;

    @KafkaListener(
            topics = "${spring.kafka.topics.convert-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleConvertFileToPdfEvent(ConvertFileToPdfEvent event) {
        if (inboxRepository.existsById(event.getEventId())) {
            return;
        }

        OutboxEntity out = null;
        String res = "";

        for (Converter converter : converters) {
            int dotIndex = event.getFileUrl().lastIndexOf(".");
            String extension = event.getFileUrl().substring(dotIndex + 1);
            if (converter.supports(extension)) {
                try {
                    res = converter.convertToPdf(minioService.download(event.getFileUrl()), event.getFileUrl());
                    try (InputStream fileInputStream = new FileInputStream(res)) {
                        int ind = res.lastIndexOf("/");
                        minioService.upload(fileInputStream, res.substring(ind + 1));
                    }
                } catch (Exception e) {
                    out = OutboxEntity.builder()
                            .event_id(UUID.randomUUID())
                            .payload(FileConversionEvent.builder()
                                    .eventId(event.getEventId())
                                    .message("conversion failed")
                                    .status(FileConversionEvent.Status.FAILED)
                                    .build()
                            )
                            .createdAt(OffsetDateTime.now())
                            .build();
                }

            }
        }

        if (out == null) {
            out = OutboxEntity.builder()
                    .event_id(UUID.randomUUID())
                    .payload(FileConversionEvent.builder()
                            .eventId(event.getEventId())
                            .message(res.substring(res.lastIndexOf("/") + 1))
                            .status(FileConversionEvent.Status.CONVERTED)
                            .build()
                    )
                    .createdAt(OffsetDateTime.now())
                    .build();
        }
        fileConversionService.saveResult(new InboxEntity(event.getEventId()), out);
    }
}
