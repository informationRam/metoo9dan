package com.idukbaduk.metoo9dan.homework.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IsNotNoneValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface IsNotNone {

    String message() default "숙제 내용을 선택해주세요";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
