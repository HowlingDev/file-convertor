package com.example.services;

import com.example.events.ConvertFileToPdfEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaFileListenerService {

    private final FileConversionService fileConversionService;

    @KafkaListener(
            topics = "${spring.kafka.topics.convert-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleConvertFileToPdfEvent(ConvertFileToPdfEvent event) {
        fileConversionService.convertFileToPdf(event);
    }
}
