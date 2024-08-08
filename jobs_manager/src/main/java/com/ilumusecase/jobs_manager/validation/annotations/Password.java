package com.ilumusecase.jobs_manager.validation.annotations;

import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.validation.validators.PasswordValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;



@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Weak password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
