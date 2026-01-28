package com.asia.asiakv.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum EntitySortField {
    KEY("key"),
    TIMESTAMP("timestamp");

    private final String fieldName;

    EntitySortField(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonValue
    public String toJson() {
        return this.fieldName;
    }

    @JsonCreator
    public static EntitySortField fromString(String value) {
        for (EntitySortField field : EntitySortField.values()) {
            if (field.fieldName.equalsIgnoreCase(value)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Invalid sort field: " + value);
    }
}
