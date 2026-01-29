package com.asia.asiakv.shared.enums;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntitySortFieldTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeEnumToJsonValue() throws Exception {
        // when
        String json = objectMapper.writeValueAsString(EntitySortField.KEY);

        // then
        assertEquals("\"key\"", json);
    }

    @Test
    void shouldDeserializeFromJsonValue_caseInsensitive() throws Exception {
        // when
        EntitySortField field = objectMapper.readValue("\"TiMeStAmP\"", EntitySortField.class);

        // then
        assertEquals(EntitySortField.TIMESTAMP, field);
    }

    @Test
    void shouldCreateEnumFromValidString() {
        // when
        EntitySortField field = EntitySortField.fromString("key");

        // then
        assertEquals(EntitySortField.KEY, field);
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        // when / then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,() -> EntitySortField.fromString("invalid"));

        assertEquals("Invalid sort field: invalid", ex.getMessage());
    }
}