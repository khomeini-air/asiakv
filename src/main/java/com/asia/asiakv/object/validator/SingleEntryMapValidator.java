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

        // Not validating the presence which will be done by @NotNull.
        if (value == null) {
            return true;
        }

        return value.size() == 1;
    }
}
