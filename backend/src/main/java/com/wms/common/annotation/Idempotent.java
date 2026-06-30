package com.wms.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    String value() default "";

    long expire() default 3;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    String keyPrefix() default "idempotent:";

    boolean required() default true;
}
