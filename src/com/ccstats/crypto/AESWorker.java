/**
 * Copyright (c) 2016 Ahmed Sakr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ccstats.crypto;


import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

/**
 *
 * @author Ahmed Sakr
 * @since February 28, 2016.
 */
public class AESWorker {

    private byte[] salt;

    /**
     * Through the power of the advanced encryption standard, a plaintext will be encrypted with a parameter-specified
     * password, an extra protective layer (salt), and a specified key length.
     *
     * Make sure to acquire the salt as it is necessary for decrypting the encrypted result.
     *
     * @param password the password as a char array.
     * @param text The plaintext to be encrypted
     * @param keyLength The length in bits of the key.
     *
     * @return The Encrypted text.
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public char[] encrypt(char[] password, String text, int keyLength) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException,
            BadPaddingException, IllegalBlockSizeException {

        password = hash(new String(password).getBytes(StandardCharsets.UTF_8));

        SecureRandom random = new SecureRandom();
        salt = new byte[20];
        random.nextBytes(salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        int maxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
        if (maxKeyLength < keyLength) {
            keyLength = maxKeyLength;
            System.err.printf("WARNING: YOUR MAXIMUM AES KEY LENGTH POLICY IS %d BITS. KEY LENGTH LIMITED TO %d BITS.\n",
                    maxKeyLength, maxKeyLength);
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, keyLength);
        SecretKey key = factory.generateSecret(spec);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] result = cipher.doFinal(text.getBytes());
        return Hex.encodeHex(result);
    }


    /**
     * An override of the encrypt method with the password parameter of type String,
     * and with the option to specify the key length in bits as a parameter.
     *
     * @param password The password used to encrypt the text.
     * @param text The text to be encrypted using the advanced encryption standard.
     * @param keyLength The length of the key in bits.
     *
     * @return The bytes of encrypted text.
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public char[] encrypt(String password, String text, int keyLength) throws NoSuchAlgorithmException
            , InvalidKeySpecException , NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException
            , BadPaddingException, IllegalBlockSizeException {
        return encrypt(password.toCharArray(), text, keyLength);
    }


    /**
     * An override of the encrypt method with a default keyLength value of 256-bits.
     *
     * @param password The password used to encrypt the text.
     * @param text The text to be encrypted using the advanced encryption standard.
     *
     * @return The bytes of encrypted text.
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public char[] encrypt(String password, String text) throws NoSuchAlgorithmException, InvalidKeySpecException
            , NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, BadPaddingException
            , IllegalBlockSizeException {
        return encrypt(password.toCharArray(), text, 256);
    }


    /**
     * Hashes the plain password to provide a more secure experience.
     *
     * @param password the bytes of the plaintext password.
     * @return The hashed password's characters in an array.
     *
     * @throws NoSuchAlgorithmException
     */
    private char[] hash(byte[] password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(password);

        return Hex.encodeHex(md.digest());
    }

    /**
     * Be careful while using this method: The salt auto destroys after one call for security measures. You
     * should take similar measures and store it somewhere safe and minimize its appearance.
     *
     * Moreover, calling the encrypt method directly after a prior call will destroy the previously stored salt.
     *
     * @return The Latest salt used to encrypt text, and automatically null the attribute.
     */
    public byte[] getLatestSaltAutoDestroy() {
        byte[] temp = salt;
        salt = null;

        return temp;
    }

}