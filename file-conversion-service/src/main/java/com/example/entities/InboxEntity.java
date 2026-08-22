package com.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox")
@Data
@NoArgsConstructor
public class InboxEntity {

    @Id
    @Column(name = "event_id")
    private UUID event_id;

    @Column(name = "processed_at", columnDefinition = "TIMESTAMPTZ NOT NULL DEFAULT now()")
    private OffsetDateTime processed_at;

    public InboxEntity(UUID event_id) {
        this.event_id = event_id;
        this.processed_at = OffsetDateTime.now();
    }
}
