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

package com.dream.spring.excel.bean;

/**
 * I18n interface for Excel Import
 *
 * <p>Concrete bean may be inject into spring context to provide global i18n method
 *
 * @author DreamJM
 */
public interface ExcelI18n {

    /**
     * i18n method
     *
     * @param code message code
     * @return i18n result string
     */
    String i18n(String code);

}
