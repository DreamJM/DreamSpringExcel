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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method will be used to generated an HTTP excel export api.
 *
 * <ul>
 * <li>The generated api will accept all annotations of the annotated method except for the annotations
 * in the package of 'org.springframework.web.bind.annotation'</li>
 * <li>The generated api will use annotation GetMapping as default, or else if the annotated method has annotation of
 * PostMapping, PutMapping or DeleteMapping, then the correlated Mapping will be used instead</li>
 * <li>All the input arguments (Except for the arguments annotated with {@link ParamIgnore ParamIgnore})
 * and there annotations will be used in the generated api method.</li>
 * </ul>
 *
 * @author DreamJM
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ExcelExport {

    /**
     * @return The mapping request path of the generated api
     */
    String value();

    /**
     * The excel name for downloading, and '{timestamp}' will be replaced with System.currentTimeMillis()
     * <p>Example:
     * fileName = "test_{timestamp}"
     * <p>'Content-Disposition' header in response will be:
     * Content-Disposition","attachment;filename=xxx.xlsx"
     *
     * @return excel name for downloading
     */
    String fileName() default "";

    /**
     * @return Represents that the cache will be enable under some condition
     */
    Cacheable[] caches() default {};

    /**
     * @return The extra annotations that should be annotated on the generated api method
     */
    AnnotationDef[] annotations() default {};
}
