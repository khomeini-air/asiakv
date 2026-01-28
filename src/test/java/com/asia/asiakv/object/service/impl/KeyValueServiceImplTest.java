package com.asia.asiakv.object.service.impl;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.asia.asiakv.object.entity.KeyCurrentVersion;
import com.asia.asiakv.object.entity.KeyValue;
import com.asia.asiakv.object.mapper.KeyValueMapper;
import com.asia.asiakv.object.repository.KeyCurrentVersionRepository;
import com.asia.asiakv.object.repository.KeyValueRepository;
import com.asia.asiakv.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeyValueServiceImplTest {
    @Mock
    private KeyCurrentVersionRepository keyCurrentVersionRepository;

    @Mock
    private KeyValueRepository keyValueRepository;

    @Mock
    private KeyValueMapper keyValueMapper;

    @InjectMocks
    private KeyValueServiceImpl service;

    @InjectMocks
    private ObjectMapper objectMapper;

    @Test
    void createOrUpdate_shouldCreateKey_whenKeyDoesNotExist() throws Exception {
        String key = "mykey";

        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "value1" }
                """);

        KeyCurrentVersion current = new KeyCurrentVersion(key, 0L);
        KeyValue saved = new KeyValue(key, valueNode, 1L);
        KeyValueDto dto = new KeyValueDto(key, valueNode, 1L, 123L);

        when(keyCurrentVersionRepository.findByKeyForUpdate(key))
                .thenReturn(Optional.empty());

        when(keyCurrentVersionRepository.save(any(KeyCurrentVersion.class)))
                .thenReturn(current);

        when(keyValueRepository.save(any(KeyValue.class)))
                .thenReturn(saved);

        when(keyValueMapper.toDTO(saved))
                .thenReturn(dto);

        KeyValueDto result = service.createOrUpdate(key, valueNode);

        assertThat(result).isEqualTo(dto);

        verify(keyCurrentVersionRepository).save(any(KeyCurrentVersion.class));
        verify(keyValueRepository).save(any(KeyValue.class));
    }

    @Test
    void createOrUpdate_shouldIncrementVersion_whenKeyExists() throws Exception {
        String key = "mykey";

        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "value2" }
                """);

        KeyCurrentVersion current = new KeyCurrentVersion(key, 5L);
        KeyValue saved = new KeyValue(key, valueNode, 6L);
        KeyValueDto dto = new KeyValueDto(key, valueNode, 6L, 123L);

        when(keyCurrentVersionRepository.findByKeyForUpdate(key))
                .thenReturn(Optional.of(current));

        when(keyValueRepository.save(any(KeyValue.class)))
                .thenReturn(saved);

        when(keyValueMapper.toDTO(saved))
                .thenReturn(dto);

        KeyValueDto result = service.createOrUpdate(key, valueNode);

        assertThat(result.getVersion()).isEqualTo(6L);

        verify(keyCurrentVersionRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        when(keyValueRepository.findAllLatest(pageable))
                .thenReturn(Page.empty());

        Page<KeyValueDto> result = service.findAll(pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldMapEntitiesToDtos() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "value" }
                """);

        KeyValue entity = new KeyValue("key", valueNode, 1L);
        KeyValueDto dto = new KeyValueDto("key", valueNode, 1L, 123L);

        when(keyValueRepository.findAllLatest(pageable))
                .thenReturn(new PageImpl<>(List.of(entity)));

        when(keyValueMapper.toDTO(entity))
                .thenReturn(dto);

        Page<KeyValueDto> result = service.findAll(pageable);

        assertThat(result.getContent()).containsExactly(dto);
    }

    @Test
    void findByKeyAndTimestamp_shouldReturnLatest_whenTimestampZero() throws Exception {
        String key = "mykey";

        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "latest" }
                """);

        KeyValue entity = new KeyValue(key, valueNode, 2L);
        KeyValueDto dto = new KeyValueDto(key, valueNode, 2L, 123L);

        when(keyValueRepository.findLatest(key))
                .thenReturn(Optional.of(entity));

        when(keyValueMapper.toDTO(entity))
                .thenReturn(dto);

        KeyValueDto result = service.findByKeyAndTimestamp(key, 0);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void findByKeyAndTimestamp_shouldReturnValueAtTimestamp() throws Exception {
        String key = "mykey";
        long timestamp = 1_700_000_000L;

        JsonNode valueNode = objectMapper.readTree("""
                    { "v": "old" }
                """);

        KeyValue entity = new KeyValue(key, valueNode, 1L);
        KeyValueDto dto = new KeyValueDto(key, valueNode, 1L, timestamp);

        when(keyValueRepository.findLatestAtTimestamp(eq(key), any()))
                .thenReturn(Optional.of(entity));

        when(keyValueMapper.toDTO(entity))
                .thenReturn(dto);

        KeyValueDto result = service.findByKeyAndTimestamp(key, timestamp);

        assertThat(result.getVersion()).isEqualTo(1L);
    }


    @Test
    void findByKeyAndTimestamp_shouldThrow_whenKeyNotFound() {
        when(keyValueRepository.findLatest("missing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.findByKeyAndTimestamp("missing", 0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
