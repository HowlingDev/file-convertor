package com.example.entities;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Builder
public class OutboxEntity {

    @Id
    @Column(name = "event_id")
    private UUID event_id;

    @Column(name = "payload", columnDefinition = "JSONB NOT NULL")
    private String payload;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ NOT NULL DEFAULT now()")
    private OffsetDateTime created_at;

    @Column(name = "published_at")
    private OffsetDateTime published_at;
}
