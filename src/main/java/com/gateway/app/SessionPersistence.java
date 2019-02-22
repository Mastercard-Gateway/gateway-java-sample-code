/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.app;

import com.gateway.client.HostedSession;

/**
 * Session persistence interface
 */
public interface SessionPersistence {
    /**
     * @param sessionId
     * @return
     */
    HostedSession getSession(String sessionId);

    /**
     * Saves hosted session updated data
     *
     * @param session
     * @return new value of session version
     */
    void saveSession(HostedSession session);
}