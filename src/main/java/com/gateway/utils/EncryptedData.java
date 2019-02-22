/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.utils;

/**
 * Data that has been encrypted with the session key, and the additional data relating to the encryption.
 */
public class EncryptedData {
    private final String ciphertext;
    private final String tag;
    private final String nonce;

    /**
     *
     * @param cipherText Base64 encoded ciphertext
     * @param nonce Base64 encoded GCM nonce/iv (initialization vector)
     * @param tag Base64 encoded GCM tag
     */
    public EncryptedData(String cipherText, String tag, String nonce) {
        this.ciphertext = cipherText;
        this.tag = tag;
        this.nonce = nonce;
    }

    /**
     * The Base64 encoded ciphertext, ie. the encrypted data.
     *
     * @return Base64 encoded ciphertext
     */
    public String getCiphertext() {
        return ciphertext;
    }

    /**
     * The authentication tag may be used to determine the authenticity of the ciphertext. The availability and use of
     * this tag is dependent on the encryption algorithm used and whether or it includes authentication.
     *
     * @return Authentication tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * The nonce/initialisation vector that was used to encrypt the data, and may be used in the decryption of the
     * data.
     *
     * @return Nonce/initialisation vector
     */
    public String getNonce() {
        return nonce;
    }
}