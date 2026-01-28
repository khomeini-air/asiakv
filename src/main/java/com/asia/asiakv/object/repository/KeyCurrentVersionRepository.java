package com.asia.asiakv.object.repository;

import com.asia.asiakv.object.entity.KeyCurrentVersion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeyCurrentVersionRepository extends JpaRepository<KeyCurrentVersion, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM KeyCurrentVersion k WHERE k.key = :key")
    Optional<KeyCurrentVersion> findByKeyForUpdate(@Param("key") String key);
}
