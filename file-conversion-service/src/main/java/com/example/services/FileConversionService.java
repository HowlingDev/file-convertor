package com.example.services;

import com.example.entities.InboxEntity;
import com.example.entities.OutboxEntity;
import com.example.repositories.InboxRepository;
import com.example.repositories.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final OutboxRepository outboxRepository;
    private final InboxRepository inboxRepository;

    @Transactional
    public void saveResult(InboxEntity inbox, OutboxEntity outbox) {
        inboxRepository.save(inbox);
        outboxRepository.save(outbox);
    }

    @Transactional
    public void updateListEntities(List<OutboxEntity> entities) {
        for (OutboxEntity entity : entities) {
            entity.setPublishedAt(OffsetDateTime.now());
            outboxRepository.save(entity);
        }
    }
}
