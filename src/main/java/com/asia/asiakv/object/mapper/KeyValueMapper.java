package com.asia.asiakv.object.mapper;

import com.asia.asiakv.object.dto.KeyValueDto;
import com.asia.asiakv.object.entity.KeyValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper responsible for converting Spring Data {@link KeyValue} objects into {@link KeyValueDto}.
 */
@Mapper(componentModel = "spring")
public interface KeyValueMapper {
    /**
     * Converts {@link KeyValue} entity into {@link KeyValueDto}
     *
     * @param keyValue the Spring entity {@link KeyValue}
     * @return a populated {@link KeyValueDto}
     */
    @Mapping(target = "timestamp", expression = "java(keyValue.getTimestamp().toEpochMilli())")
    KeyValueDto toDTO(KeyValue keyValue);
}
