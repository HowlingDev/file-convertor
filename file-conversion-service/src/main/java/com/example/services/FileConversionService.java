package com.example.services;

import com.example.converters.Converter;
import com.example.converters.ImageToPdfConverter;
import com.example.converters.TxtToPdfConverter;
import com.example.converters.ZipToPdfConverter;
import com.example.entities.InboxEntity;
import com.example.entities.OutboxEntity;
import com.example.events.ConvertFileToPdfEvent;
import com.example.events.FileConversionEvent;
import com.example.repositories.InboxRepository;
import com.example.repositories.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;

    private final InboxRepository inboxRepository;

    private final MinioService minioService;

    private final List<Converter> converters;

    @Transactional
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
                try (InputStream inputStream = minioService.download(event.getFileUrl())) {
                    res = converter.convertToPdf(inputStream, event.getFileUrl());
                    try (InputStream fileInputStream = new FileInputStream(res)) {
                        int ind = res.lastIndexOf("/");
                        minioService.upload(fileInputStream, res.substring(ind + 1));
                    }
                } catch (IOException e) {
                    out = OutboxEntity.builder()
                            .event_id(UUID.randomUUID())
                            .payload(objectMapper.writeValueAsString(
                                    FileConversionEvent.builder()
                                            .eventId(UUID.randomUUID())
                                            .message("conversion failed")
                                            .status(FileConversionEvent.Status.FAILED)
                                            .build()
                            ))
                            .build();
                }

            }
            inboxRepository.save(new InboxEntity(event.getEventId()));
            if (out == null) {
                out = OutboxEntity.builder()
                        .event_id(UUID.randomUUID())
                        .payload(objectMapper.writeValueAsString(
                                FileConversionEvent.builder()
                                        .eventId(UUID.randomUUID())
                                        .message(res.substring(res.lastIndexOf("/") + 1))
                                        .status(FileConversionEvent.Status.CONVERTED)
                                        .build()
                        ))
                        .build();
            }
            outboxRepository.save(out);
        }
    }

    public void convertTxt(String fileName) {
        TxtToPdfConverter converter = new TxtToPdfConverter();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            converter.convertToPdf(minioService.download(fileName), fileName);
            System.out.println("файл сконвертирован");
        }
    }

    public void convertPngImage(String fileName) {
        ImageToPdfConverter converter = new ImageToPdfConverter();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            converter.convertToPdf(minioService.download(fileName), fileName);
            System.out.println("файл сконвертирован");
        }
    }

    public void convertJpgImage(String fileName) {
        ImageToPdfConverter converter = new ImageToPdfConverter();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            converter.convertToPdf(minioService.download(fileName), fileName);
            System.out.println("файл сконвертирован");
        }
    }

    public String convertZip(String fileName) {
        ZipToPdfConverter converter = (ZipToPdfConverter) converters.stream()
                .filter(converter1 -> converter1 instanceof ZipToPdfConverter)
                .findFirst().orElseThrow();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            return converter.convertToPdf(minioService.download(fileName), fileName);
        }
        return "fail";
    }
}
