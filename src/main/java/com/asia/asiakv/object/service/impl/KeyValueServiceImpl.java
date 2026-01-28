package com.asia.asiakv.object.service.impl;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.asia.asiakv.object.entity.KeyCurrentVersion;
import com.asia.asiakv.object.entity.KeyValue;
import com.asia.asiakv.object.mapper.KeyValueMapper;
import com.asia.asiakv.object.repository.KeyCurrentVersionRepository;
import com.asia.asiakv.object.repository.KeyValueRepository;
import com.asia.asiakv.object.service.KeyValueService;
import com.asia.asiakv.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyValueServiceImpl implements KeyValueService {
    private final KeyCurrentVersionRepository keyCurrentVersionRepository;
    private final KeyValueRepository keyValueRepository;
    private final KeyValueMapper keyValueMapper;

    @Override
    @Transactional
    public KeyValueDto createOrUpdate(String key, JsonNode value) {

        // Find the key or create new if not exist
        KeyCurrentVersion current = findKeyOrAddNew(key);

        // Increment version
        long version = current.increment();

        // Persist history
        KeyValue  keyValue = keyValueRepository.save(new KeyValue(key, value, version));
        log.debug("Persisted key={}, version={}, value={}", key, version, value);

        return keyValueMapper.toDTO(keyValue);
    }

    /**
     * Find the key's latest version or add new if not exists
     *
     * @param key
     * @return key's latest version or new key (version 0)
     */
    private KeyCurrentVersion findKeyOrAddNew(String key) {
        return keyCurrentVersionRepository
                .findByKeyForUpdate(key)
                .map(existingKey -> {
                    log.info("Key exists, updating key: {}", key);
                    return existingKey;
                })
                .orElseGet(() -> {
                    log.info("Persisting new key: {}", key);
                    return keyCurrentVersionRepository.save(new KeyCurrentVersion(key, 0L));
                });

    }

    @Override
    @Transactional(readOnly = true)
    public Page<KeyValueDto> findAll(Pageable pageable) {
        return keyValueRepository
                .findAllLatest(pageable)
                .map(keyValueMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public KeyValueDto findByKeyAndTimestamp(String key, long timestamp) {
        Optional<KeyValue> keyValue =
                timestamp <=  0 ?
                keyValueRepository.findLatest(key) :
                keyValueRepository.findLatestAtTimestamp(key, Instant.ofEpochMilli(timestamp));

        return keyValue
                .map(keyValueMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Key %s not found".formatted(key)));
    }
}
