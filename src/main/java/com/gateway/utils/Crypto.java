/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

package com.gateway.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Performs cryptographic operations relating to encrypted session data. This is data that may go back to the creator of
 * a session through an untrusted medium such as a payer's browser.
 */
public final class Crypto {
    //Cipher Algorithm Name
    private static final String ALGORITHM = "AES";
    //Cipher Algorithm Mode
    private static final String MODE = "GCM";
    private static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/NoPadding";

    /**
     * Decrypt a text using a 256 bit AES key in GCM mode
     * <p>
     * The data is encrypted by the gateway using AES256 in GCM mode. To decrypt the data the merchant must use the key
     * provided in field session.aes256Key in the CREATE_SESSION response and the parameters in encryptedData group,
     * from the  redirect of the payer's browser to the merchant (after the Challenge)
     *
     * @param encryptedData The encryptedData parameter group containing details about the payer authentication
     * @param aes256key the AES256 secret key
     * @return the plaintext result of the decryption of the cipherText
     * @throws Exception
     */
    public static String decrypt(EncryptedData encryptedData, String aes256key) throws Exception {
        try {
            final int TAG_LENGTH_BIT = Base64.getDecoder().decode(encryptedData.getTag()).length * Byte.SIZE;

            byte[] iv = encryptedData.getNonce().getBytes();
            SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(aes256key), ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.DECRYPT_MODE, key,
                    new GCMParameterSpec(TAG_LENGTH_BIT, Base64.getDecoder().decode(encryptedData.getNonce())));
            cipher.update(Base64.getDecoder().decode(encryptedData.getCiphertext()));
            return cipher.doFinal(Base64.getDecoder().decode(encryptedData.getTag()))
                    .toString();
        } catch (Exception e) {
            System.out.println("Error while decrypting the ciphertext");
            throw e;
        }

    }
}