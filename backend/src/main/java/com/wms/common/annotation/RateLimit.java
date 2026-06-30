package com.wms.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    int maxAttempts() default 5;

    long windowSeconds() default 300;

    String key() default "";
}
