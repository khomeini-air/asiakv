package com.asia.asiakv.object.validator;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation of {@link ValidJsonValue}
 *
 * @see ValidJsonValue
 */
public class ValidJsonValueValidator  implements ConstraintValidator<ValidJsonValue, JsonNode> {

    @Override
    public boolean isValid(JsonNode value, ConstraintValidatorContext context) {

        if (value == null || value.isNull()) {
            return false;
        }

        // Accept primitives: string, number, boolean
        if (value.isValueNode()) {
            return true;
        }

        // Accept JSON objects (but not empty ones)
        if (value.isObject()) {
            return !value.isEmpty();
        }

        return false;
    }
}

