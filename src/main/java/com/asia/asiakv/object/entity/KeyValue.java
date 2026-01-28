package com.asia.asiakv.object.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 *
 */
@Entity()
@Table(name = "key_value",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_key_version",
                        columnNames = {"key", "version"}
                )
        },
        indexes = {
                @Index(name = "idx_key_timestamp", columnList = "key, timestamp DESC"),
                @Index(name = "idx_key_version", columnList = "key, version")
        })
@Data
@NoArgsConstructor
public class KeyValue {

    public KeyValue(String key, JsonNode value, Long version) {
        this.key = key;
        this.value = value;
        this.version = version;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String key;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode value;

    @Column(nullable = false, updatable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant timestamp;
}
