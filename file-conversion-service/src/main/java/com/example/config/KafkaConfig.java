package com.example.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topics.convert-event}")
    private String convertTopic;
    @Value("${spring.kafka.topics.success-event}")
    private String failedTopic;
    @Value("${spring.kafka.topics.failed-event}")
    private String successTopic;

    @Bean
    public NewTopic createConvertToPdfTopic() {
        return TopicBuilder.name(convertTopic)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public NewTopic createSuccessfulFileConversionTopic() {
        return TopicBuilder.name(successTopic)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public NewTopic createFailedFileConversionTopic() {
        return TopicBuilder.name(failedTopic)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }
}
