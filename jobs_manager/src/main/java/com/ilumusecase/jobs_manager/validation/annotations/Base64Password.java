package com.ilumusecase.jobs_manager.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.validation.validators.Base64PasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = Base64PasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Password {
    String message() default "Weak password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
