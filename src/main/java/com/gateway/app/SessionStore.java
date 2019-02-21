/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import java.util.HashMap;
import java.util.Map;

import com.gateway.client.HostedSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class SessionStore implements SessionPersistence {
    private static final Logger s_log = LoggerFactory.getLogger(SessionStore.class);
    /**
     *
     */
    private static Map<String, HostedSession> sessionStore = new HashMap<>();


    public HostedSession getSession(String session) {
        return sessionStore.get(session);
    }

    /**
     * Saves hosted session updated data
     *
     * @return new value of session version
     */
    @Override
    public void saveSession(HostedSession session) {
        sessionStore.put(session.getId(), session);

    }
}