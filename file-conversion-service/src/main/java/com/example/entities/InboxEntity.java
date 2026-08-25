package com.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox")
@Getter
@Setter
@NoArgsConstructor
public class InboxEntity {

    @Id
    @Column(name = "event_id")
    private UUID event_id;

    @Column(name = "processed_at", columnDefinition = "TIMESTAMPTZ NOT NULL DEFAULT now()")
    private OffsetDateTime processedAt;

    public InboxEntity(UUID event_id) {
        this.event_id = event_id;
        this.processedAt = OffsetDateTime.now();
    }
}
