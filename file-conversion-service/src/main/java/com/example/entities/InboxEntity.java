package com.example.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox")
@Data
public class InboxEntity {

    @Id
    @Column(name = "event_id")
    private UUID event_id;

    @Column(name = "processed_at", columnDefinition = "TIMESTAMPTZ NOT NULL DEFAULT now()")
    private OffsetDateTime processed_at;

    public InboxEntity(UUID event_id) {
        this.event_id = event_id;
    }
}
