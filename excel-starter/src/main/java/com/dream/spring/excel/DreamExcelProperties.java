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

package com.dream.spring.excel;

import com.dream.spring.excel.bean.ExcelExportConfig;
import com.dream.spring.excel.bean.ExcelI18n;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Excel configuration properties
 *
 * @author DreamJM
 */
@ConfigurationProperties(prefix = "dream.excel")
public class DreamExcelProperties {

    private ExcelExportConfig export = new ExcelExportConfig();

    /**
     * Suggests to implement and inject {@link ExcelI18n} object by yourself instead of use i18n properties
     */
    @Deprecated
    private I18nProperties i18n = new I18nProperties();

    /**
     * Cache Directory for excel cache function
     */
    private String cacheDir = ".";

    public ExcelExportConfig getExport() {
        return export;
    }

    public I18nProperties getI18n() {
        return i18n;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public static class I18nProperties {

        /**
         * i18n class
         */
        private Class<?> clazz;

        /**
         * i18n method
         * <ul>
         * <li>should have 'public' and 'static' modifier</li>
         * <li>should have 'String' type return value</li>
         * <li>should have a method that accept one String input argument</li>
         * </ul>
         */
        private String method;

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}
