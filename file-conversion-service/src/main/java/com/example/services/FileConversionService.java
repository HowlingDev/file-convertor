package com.example.services;

import com.example.converters.Converter;
import com.example.converters.ImageToPdfConverter;
import com.example.converters.TxtToPdfConverter;
import com.example.converters.ZipToPdfConverter;
import com.example.events.ConvertFileToPdfEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FileConversionService {

    MinioService minioService;

    List<Converter> converters;

    @KafkaListener(topics = "convert-to-pdf-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handleConvertFileToPdfEvent(ConvertFileToPdfEvent event) throws Exception {
        for (Converter converter: converters) {
            int dotIndex = event.getFileUrl().lastIndexOf(".");
            String extension = event.getFileUrl().substring(dotIndex + 1);
            if (converter.supports(extension)) {
                String res = converter.convertToPdf(minioService.download(event.getFileUrl()), event.getFileUrl());
                System.out.println("файл сконвертирован");
            }
        }
    }

    public void convertTxt(String fileName) throws Exception {
        TxtToPdfConverter converter = new TxtToPdfConverter();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            converter.convertToPdf(minioService.download(fileName), fileName);
            System.out.println("файл сконвертирован");
        }
    }

    public void convertPngImage(String fileName) throws Exception {
        ImageToPdfConverter converter = new ImageToPdfConverter();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            converter.convertToPdf(minioService.download(fileName), fileName);
            System.out.println("файл сконвертирован");
        }
    }

    public void convertJpgImage(String fileName) throws Exception {
        ImageToPdfConverter converter = new ImageToPdfConverter();
        int dotIndex = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotIndex + 1);
        if (converter.supports(extension)) {
            converter.convertToPdf(minioService.download(fileName), fileName);
            System.out.println("файл сконвертирован");
        }
    }

    public String convertZip(String fileName) throws Exception {
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
