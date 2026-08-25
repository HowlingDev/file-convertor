package com.example.schedulers;

import com.example.entities.OutboxEntity;
import com.example.events.FileConversionEvent;
import com.example.repositories.OutboxRepository;
import com.example.services.FileConversionService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, FileConversionEvent> kafkaTemplate;
    private final FileConversionService fileConversionService;
    @Value("${spring.kafka.topics.success-event}")
    private String failedTopic;
    @Value("${spring.kafka.topics.failed-event}")
    private String successTopic;

    @Scheduled(fixedRate = 300000)
    @SchedulerLock(
            name = "OutboxScheduler_sendOutboxEvent",
            lockAtMostFor = "PT30S"
    )
    public void sendOutboxEvent() {
        List<OutboxEntity> entities = outboxRepository.findTop10ByPublishedAtIsNull();
        if (entities.isEmpty()) {
            return;
        }
        entities.forEach(entity -> {
                    FileConversionEvent event = entity.getPayload();
                    if (event.getStatus() == FileConversionEvent.Status.CONVERTED) {
                        kafkaTemplate.send(failedTopic, event);
                    } else {
                        kafkaTemplate.send(successTopic, event);
                    }
                });
        fileConversionService.updateListEntities(entities);
    }
}
