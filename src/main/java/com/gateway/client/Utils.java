package com.gateway.client;

import org.apache.commons.lang.RandomStringUtils;

public class Utils {

    /**
     * Generates a random 10-digit alphanumeric number to use as a unique identifier (order ID and transaction ID, for instance)
     *
     * @return random identifier
     */
    public static String createUniqueId(String prefix) {
        return prefix + RandomStringUtils.random(10, true, true);
    }

    /**
     * Helper method to determine if a value is null or blank
     *
     * @param value
     * @return boolean
     */
    public static boolean notNullOrEmpty(String value) {
        return (value != null && !value.equals(""));
    }
}
