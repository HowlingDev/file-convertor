package com.example.services;

import com.example.converters.Converter;
import com.example.entities.InboxEntity;
import com.example.entities.OutboxEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.events.FileConversionEvent;
import com.example.repositories.InboxRepository;
import com.example.repositories.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final OutboxRepository outboxRepository;

    private final InboxRepository inboxRepository;

    private final MinioService minioService;

    private final List<Converter> converters;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void convertFileToPdf(ConvertFileToPdfEvent event) {

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
                                            .eventId(UUID.randomUUID())
                                            .message("conversion failed")
                                            .status(FileConversionEvent.Status.FAILED)
                                            .build()
                            )
                            .created_at(OffsetDateTime.now())
                            .build();
                }

            }
        }
        inboxRepository.save(new InboxEntity(event.getEventId()));
        if (out == null) {
            out = OutboxEntity.builder()
                    .event_id(UUID.randomUUID())
                    .payload(FileConversionEvent.builder()
                                    .eventId(UUID.randomUUID())
                                    .message(res.substring(res.lastIndexOf("/") + 1))
                                    .status(FileConversionEvent.Status.CONVERTED)
                                    .build()
                    )
                    .created_at(OffsetDateTime.now())
                    .build();
        }
        outboxRepository.save(out);
    }
}
