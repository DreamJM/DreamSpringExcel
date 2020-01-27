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
 * Method for internationalization
 *
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface I18n {

    /**
     * @return i18n class
     */
    Class<?> clazz() default String.class;

    /**
     * i18n method
     * <ul>
     * <li>should have 'public' and 'static' modifier</li>
     * <li>should have 'String' type return value</li>
     * <li>should have a method that accept one String input argument</li>
     * </ul>
     *
     * @return i18n method
     */
    String method() default "valueOf";
}
