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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Dream excel auto configuration for {@link ExcelExportConfig Excel Export Config}
 *
 * @author DreamJM
 */
@Configuration
@EnableConfigurationProperties(DreamExcelProperties.class)
public class DreamExcelAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DreamExcelAutoConfiguration.class);

    private DreamExcelProperties properties;

    public DreamExcelAutoConfiguration(DreamExcelProperties properties) {
        this.properties = properties;
        FileUtils.setCacheRoot(properties.getCacheDir());
    }

    @Bean
    public ExcelExportConfig excelExportConfig() {
        return properties.getExport();
    }

    /**
     * Suggests to implement and inject {@link ExcelI18n} object by yourself
     *
     * @return ExcelI18n object
     * @throws NoSuchMethodException Reflection Error
     * @see ExcelI18n
     */
    @Deprecated
    @ConditionalOnMissingBean(ExcelI18n.class)
    @Bean
    public ExcelI18n excelI18n() throws NoSuchMethodException {
        if (properties.getI18n() != null && properties.getI18n().getClazz() != null) {
            if (properties.getI18n().getMethod() == null || "".equals(properties.getI18n().getMethod())) {
                logger.warn("Excel i18n method not specified");
                return code -> code;
            }
            Method method = properties.getI18n().getClazz().getMethod(properties.getI18n().getMethod(), String.class);
            return code -> {
                try {
                    return String.valueOf(method.invoke(properties.getI18n().getClazz(), code));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("Excel i18n method invoke error.", e);
                }
                return code;
            };
        }
        return code -> code;
    }

}
