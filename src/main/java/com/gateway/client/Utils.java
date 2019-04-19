/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.client;

import org.apache.commons.lang.RandomStringUtils;

public class Utils {

    /**
     * Generates a random 10-digit alphanumeric number to use as a unique identifier (order ID and transaction ID, for instance)
     * The unique ID need not have a prefix - it's being added here simply for debugging purposes
     *
     * @return random identifier
     */
    public static String createUniqueId(Prefixes prefix) {
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

    public enum Prefixes {
        TRANS("trans-"),
        THREEDS("3ds-"),
        ORDER("order-"),
        APM("APM_"),
        MD("md-");

        private final String prefix;

        Prefixes(String s) {
            this.prefix = s;

        }

        @Override
        public String toString() {
            return prefix;
        }
    }
}
