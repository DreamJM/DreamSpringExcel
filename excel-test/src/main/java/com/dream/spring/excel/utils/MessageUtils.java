package com.dream.spring.excel.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author DreamJM
 */
@Component
public class MessageUtils {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtils.class);

    private static MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    public static String get(String msgKey, Object... params) {
        try {
            return messageSource.getMessage(msgKey, params, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            logger.error("Message Key " + msgKey + " not found", ex);
            return null;
        }
    }
}
