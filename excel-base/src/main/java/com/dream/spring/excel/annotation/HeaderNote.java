/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dream.spring.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extra note or other decoration for the header
 *
 * @author DreamJM
 * @see Header
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface HeaderNote {

    /**
     * @return If true, "*" character will prepend to the header name
     */
    boolean necessary() default false;

    /**
     * The note content that will append to the header name
     *
     * <p>If {@link #i18nSupport() i18nSupport} is true, then the content will be used as the code of i18n
     *
     * @return note content
     */
    String content() default "";

    /**
     * @return If true, the note content will be wrapped by brace
     */
    boolean brace() default true;

    /**
     * @return If true, the note will begin in new line
     */
    boolean wrapLine() default true;

    /**
     * @return If true, the specified {@link Sheet#i18n() i18n method} will be used to internationalize {@link #content() content}
     * @see Sheet#i18n()
     */
    boolean i18nSupport() default true;

}
