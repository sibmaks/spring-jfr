package com.github.sibmaks.spring.jfr;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(JavaFlightRecorderCondition.class)
public @interface JavaFlightRecorderConditional {
    String[] requiredClasses() default {};

    JavaFlightRecorderProperty[] properties() default {};
}
