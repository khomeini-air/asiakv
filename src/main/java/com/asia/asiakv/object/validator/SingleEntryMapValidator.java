package com.asia.asiakv.object.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

/**
 * Validator implementation for the {@link SingleEntryMap} constraint.
 *
 * @see SingleEntryMap
 */
public class SingleEntryMapValidator implements ConstraintValidator<SingleEntryMap, Map<?, ?>> {

    @Override
    public boolean isValid(Map<?, ?> value, ConstraintValidatorContext context) {

        // Null is valid
        if (value == null) {
            return true;
        }

        return value.size() == 1;
    }
}
