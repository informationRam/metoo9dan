package com.idukbaduk.metoo9dan.homework.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsNotNoneValidator implements ConstraintValidator<IsNotNone, String> {

    @Override
    public void initialize(IsNotNone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.equalsIgnoreCase("none");
    }
}
