package com.asia.asiakv.object.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom constraint annotation to ensure that a map contains exactly one key-value pair.
 *
 * @see SingleEntryMapValidator
 */
@Documented
@Constraint(validatedBy = SingleEntryMapValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleEntryMap {

    String message() default "Request must contain exactly one key-value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
