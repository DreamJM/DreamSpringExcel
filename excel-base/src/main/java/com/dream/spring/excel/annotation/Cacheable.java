package com.dream.spring.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Cacheable {
    String cacheDir();

    String condition();

    String timestampMethod();

    String checkUpdateMethod();
}
