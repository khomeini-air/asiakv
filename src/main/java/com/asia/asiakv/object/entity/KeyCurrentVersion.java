package com.asia.asiakv.object.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "key_current_version")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyCurrentVersion {
    @Id
    private String key;

    @Column(name = "current_version", nullable = false)
    private Long currentVersion;

    public long increment() {
        return ++currentVersion;
    }
}
