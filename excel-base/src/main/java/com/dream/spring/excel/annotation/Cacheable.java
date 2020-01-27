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
 * Indicates that the cache capability should be enabled.
 *
 * <p>The exported excel will be cached in the specified local directory
 *
 * @author DreamJM
 * @see ExcelExport
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Cacheable {

    /**
     * Specifies the cache directory(relative to the class root path) of exported excels
     *
     * @return cache directory of exported excels
     */
    String cacheDir();

    /**
     * Specifies the condition expression for cache
     *
     * @return condition expression for cache
     */
    String condition();

    /**
     * Specifies the method calling statement to get the latest timestamp for the excel data.
     *
     * <p>The specified method should exist in the annotated component and has 'public' modifier.
     * <p>'sheet' parameter can be used as input argument, it represents the Collection or Array of data to be exported to excel
     * Example:
     * <pre class="code">
     * timestampMethod = "getTimestamp(sheet)"
     * </pre>
     *
     * @return the method calling statement to get the latest timestamp for the excel data
     */
    String timestampMethod();

    /**
     * Specifies the method calling statement to check if should use the cached excel based on timestamp
     *
     * <p>The specified method should exist in the annotated component, has 'public' modifier and has boolean return value.
     * 'timestamp' parameter can be used as input argument, it represents cached excel data's timestamp.
     * <p>If the specified statement return true, it means that the source data has updated and the cache is invalid.
     * Or else, the cached excel will be returned directly.
     * Example:
     * <pre class="code">
     * checkUpdateMethod = "isDataUpdated(timestamp)"
     * </pre>
     *
     * @return the method calling statement to check if should use the cached excel based on timestamp
     */
    String checkUpdateMethod();
}
