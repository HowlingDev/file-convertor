package com.example.entities;

import com.example.events.FileConversionEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OutboxEntity {

    @Id
    @Column(name = "event_id")
    private UUID event_id;

    @Column(name = "payload", columnDefinition = "JSONB NOT NULL")
    @JdbcTypeCode(SqlTypes.JSON)
    private FileConversionEvent payload;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ NOT NULL DEFAULT now()")
    private OffsetDateTime created_at;

    @Column(name = "published_at")
    private OffsetDateTime published_at;
}
