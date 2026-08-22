package com.example;

import com.example.events.ConvertFileToPdfEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final KafkaTemplate<String, ConvertFileToPdfEvent> kafkaTemplate;

    @GetMapping("/kafka/get/txt")
    public void getTxtFileWithKafka() {
        kafkaTemplate.send("convert-to-pdf-topic",
                new ConvertFileToPdfEvent(UUID.randomUUID(), "testfile.txt"));
    }

}
