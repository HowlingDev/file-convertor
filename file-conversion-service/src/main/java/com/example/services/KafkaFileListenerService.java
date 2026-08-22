package com.example.services;

import com.example.events.ConvertFileToPdfEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaFileListenerService {

    private FileConversionService fileConversionService;

    @KafkaListener(
            topics = "convert-to-pdf-topic",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleConvertFileToPdfEvent(ConvertFileToPdfEvent event) {
        fileConversionService.convertFileToPdf(event);
    }
}
