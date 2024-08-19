package com.ilumusecase.jobs_manager.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.validation.validators.UsernameValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;


@Constraint(validatedBy = UsernameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {
    String message() default "Invalid username: use letters, digits, 3 to 20 symbols only";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
