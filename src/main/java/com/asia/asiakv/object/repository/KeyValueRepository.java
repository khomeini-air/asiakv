package com.asia.asiakv.object.repository;

import com.asia.asiakv.object.entity.KeyValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface KeyValueRepository extends JpaRepository<KeyValue, Long> {
    @Query("SELECT kv FROM KeyValue kv WHERE kv.key = :key ORDER BY kv.version DESC LIMIT 1")
    Optional<KeyValue> findLatest(@Param("key") String key);

    @Query("""
        SELECT kv
        FROM KeyValue kv
        WHERE kv.key = :key AND kv.timestamp <= :timestamp
        ORDER BY kv.timestamp DESC
        LIMIT 1
    """)
    Optional<KeyValue> findLatestAtTimestamp(@Param("key") String key, @Param("timestamp") Instant timestamp);

    @Query("""
        SELECT kv
        FROM KeyValue kv
        JOIN KeyCurrentVersion c
          ON kv.key = c.key
         AND kv.version = c.currentVersion
    """)
    Page<KeyValue> findAllLatest(Pageable pageable);
}
