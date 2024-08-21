package com.ilumusecase.jobs_manager.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.validation.validators.JsonStringValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = JsonStringValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonString {
    String message() default "Weak password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
