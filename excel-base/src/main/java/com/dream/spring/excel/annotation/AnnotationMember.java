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
 * Annotation as fields of {@link AnnotationDef @AnnotationDef} to define the field value of
 * the extra annotation specified by {@link AnnotationDef#clazz()}
 *
 * @author DreamJM
 * @see AnnotationDef
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface AnnotationMember {

    /**
     * @return The field name of the specified annotation({@link AnnotationDef#clazz()})
     */
    String name();

    /**
     * @return The field values to be set
     */
    String[] value();

    /**
     * Defines the class of the annotation when the child field is an annotation
     *
     * @return class of child annotation
     */
    Class<?> annotation() default AnnotationIgnore.class;
}
