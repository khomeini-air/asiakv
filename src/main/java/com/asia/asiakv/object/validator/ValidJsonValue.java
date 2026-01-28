package com.asia.asiakv.object.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom constraint to ensure the value is a valid JSON.
 *
 * @see ValidJsonValueValidator
 */
@Documented
@Constraint(validatedBy = ValidJsonValueValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJsonValue {
    String message() default "Invalid JSON value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
