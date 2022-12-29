package com.superapp.redsocial.notification.domain.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SALogger {

    private static final Logger logger = LoggerFactory.getLogger("SALogger");

    public static void error(String className, String methodName, String message) {
        logger.error(getMessage(className, methodName, message));
    }
    public static void warn(String className, String methodName, String message) {
        logger.warn(getMessage(className, methodName, message));
    }
    public static void info(String className, String methodName, String message) {
        logger.info(getMessage(className, methodName, message));
    }

    private static String getMessage(String className, String methodName, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
        return new StringBuilder().append(dateFormat.format(new Date())).append(" MX - ")
                .append(className).append(" - ")
                .append(methodName).append(" - ")
                .append(message).toString();
    }

}

