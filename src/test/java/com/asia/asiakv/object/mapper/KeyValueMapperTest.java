package com.asia.asiakv.object.mapper;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.asia.asiakv.object.entity.KeyValue;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class KeyValueMapperTest {

    private final KeyValueMapper mapper = Mappers.getMapper(KeyValueMapper.class);

    @Test
    void toDTO_shouldMapAllFieldsCorrectly() {
        // given
        Instant now = Instant.now();
        KeyValue entity = new KeyValue();
        entity.setKey("my-key");
        entity.setValue(null);
        entity.setVersion(3L);
        entity.setTimestamp(now);

        // when
        KeyValueDto dto = mapper.toDTO(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getKey()).isEqualTo("my-key");
        assertThat(dto.getVersion()).isEqualTo(3L);
        assertThat(dto.getTimestamp()).isEqualTo(now.toEpochMilli());
    }
}