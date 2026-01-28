package com.asia.asiakv.object.service;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KeyValueService {
    /**
     * Create new key if not exist, or else update it and increment its version.
     *
     * @param key {@link String} key
     * @param value {@link JsonNode} value
     * @return new key (version 1) or updated key (incremented version)
     */
    KeyValueDto createOrUpdate(String key, JsonNode value);

    /**
     * Find the latest version of all key-value with pagination.
     *
     * @param page {@link Pageable} page
     * @return {@link KeyValueDto} with its current page.
     */
    Page<KeyValueDto> findAll(Pageable page);

    /**
     * Find the latest version of the given key before the timestamp.
     * If the timestamp is <=0 , return the latest version.
     *
     * @param key {@link String} key
     * @param timestamp {@code long} timestamp
     * @return
     */
    KeyValueDto findByKeyAndTimestamp(String key, long timestamp);
}
