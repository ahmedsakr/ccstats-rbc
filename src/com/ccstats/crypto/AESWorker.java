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


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

/**
 *
 * @author Ahmed Sakr
 * @since February 28, 2016.
 */
public class AESWorker {

    private byte[] salt, ivBytes;

    /**
     * Through the power of the advanced encryption standard, a plaintext will be encrypted with a parameter-specified
     * password, an extra protective layer (salt), and a specified key length. Make sure to acquire the salt and ivBytes
     * as they are necessary for decrypting the encrypted result.
     *
     * Firstly, The password is obtained and instantly overridden with the hashed version of the password, allowing
     * for stronger security as the plaintext password will not be used. Second, an arbitrary salt is securely
     * generated. Finally, the encryption standard is carried out and the encrypted text is obtained.
     *
     * @param password the password as a char array.
     * @param text The plaintext bytes to be encrypted.
     * @param keyLength The length in bits of the key.
     *
     * @return The Encrypted text in hexadecimal format.
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public char[] encrypt(char[] password, byte[] text, int keyLength) throws NoSuchAlgorithmException,
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

        AlgorithmParameters ap = cipher.getParameters();
        ivBytes = ap.getParameterSpec(IvParameterSpec.class).getIV();

        byte[] result = cipher.doFinal(text);
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
    public char[] encrypt(String password, String text, int keyLength) throws NoSuchAlgorithmException,
            InvalidKeySpecException , NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException,
            BadPaddingException, IllegalBlockSizeException {
        return encrypt(password.toCharArray(), text.getBytes(StandardCharsets.UTF_8), keyLength);
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
    public char[] encrypt(String password, String text) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, BadPaddingException,
            IllegalBlockSizeException {
        return encrypt(password.toCharArray(), text.getBytes(StandardCharsets.UTF_8), 256);
    }


    /**
     * Decrypting text that is encrypted by the advanced encryption standard. The Salt and Iv bytes are critical
     * for decryption of the text.
     *
     * @param password The char array containing of the plaintext password
     * @param encryptedText The Encrypted text to be targeted and decrypted.
     * @param salt The salt bytes used to encrypt the text.
     * @param ivBytes The Iv Bytes produced and used in the encryption process.
     * @param keyLength The AES Key length in bits.
     *
     * @return The decrypted byte array of the encrypted text.
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws DecoderException
     *
     * @see this#getLatestIvAutoDestroy()
     * @see this#getLatestSaltAutoDestroy()
     */
    public byte[] decrypt(char[] password, char[] encryptedText, byte[] salt, byte[] ivBytes, int keyLength) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, DecoderException {

        password = hash(new String(password).getBytes(StandardCharsets.UTF_8));
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

        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
        return cipher.doFinal(Hex.decodeHex(encryptedText));
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as String
     * objects.
     *
     * @param password The plaintext password as a String.
     * @param encryptedText The encrypted text as a String.
     * @param salt The salt bytes used when at the time of encrypting the text.
     * @param ivBytes The Iv bytes produced by the encryption and needed for decryption
     * @param keyLength The AES Key length in bits.
     *
     * @return The decrypted byte array of the encrypted text.
     *
     * @throws NoSuchPaddingException
     * @throws DecoderException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    public byte[] decrypt(String password, String encryptedText, byte[] salt, byte[] ivBytes, int keyLength) throws
            NoSuchPaddingException, DecoderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return decrypt(password.toCharArray(), encryptedText.toCharArray(), salt, ivBytes, keyLength);
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as String
     * objects, and a default value of 256-bits for the AES Key length value.
     *
     * @param password The plaintext password as a String.
     * @param encryptedText The encrypted text as a String.
     * @param salt The salt bytes used when at the time of encrypting the text.
     * @param ivBytes The Iv bytes produced by the encryption and needed for decryption
     *
     * @return The decrypted byte array of the encrypted text.
     *
     * @throws NoSuchPaddingException
     * @throws DecoderException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    public byte[] decrypt(String password, String encryptedText, byte[] salt, byte[] ivBytes) throws
            NoSuchPaddingException, DecoderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return decrypt(password.toCharArray(), encryptedText.toCharArray(), salt, ivBytes, 256);
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as character
     * arrays, and attempt to extract the latest ivBytes and salt for usage. The Default AES Key length is 256-bits.
     *
     * @param password The plaintext password as a char array.
     * @param encryptedText The encrypted text as a char array.
     *
     * @return The decrypted byte array of the encrypted text.
     *
     * @throws NoSuchPaddingException
     * @throws DecoderException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    public byte[] decrypt(char[] password, char[] encryptedText) throws NoSuchPaddingException, DecoderException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        byte[] ivBytes = getLatestIvAutoDestroy();
        byte[] salt = getLatestSaltAutoDestroy();

        if (ivBytes == null) {
            System.err.println("Attempted to decrypt text with null Iv Bytes, returning null...");
            return null;
        }

        if (salt == null) {
            System.err.println("Attempted to decrypt text with null salt bytes, returning null...");
            return null;
        }

        return decrypt(password, encryptedText, salt, ivBytes, 256);
    }



    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as String objects,
     * and attempt to extract the latest ivBytes and salt for usage. The Default AES Key length is 256-bits.
     *
     * @param password The plaintext password as a char array.
     * @param encryptedText The encrypted text as a char array.
     *
     * @return The decrypted byte array of the encrypted text.
     *
     * @throws NoSuchPaddingException
     * @throws DecoderException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    public byte[] decrypt(String password, String encryptedText) throws NoSuchPaddingException, DecoderException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        byte[] ivBytes = getLatestIvAutoDestroy();
        byte[] salt = getLatestSaltAutoDestroy();

        if (ivBytes == null) {
            System.err.println("Attempted to decrypt text with null Iv Bytes, returning null...");
            return null;
        }

        if (salt == null) {
            System.err.println("Attempted to decrypt text with null salt bytes, returning null...");
            return null;
        }

        return decrypt(password.toCharArray(), encryptedText.toCharArray(), salt, ivBytes, 256);
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


    /**
     * Be careful while using this method: The Iv bytes auto destroy after one call for security measures. You
     * should take similar measures and store it somewhere safe and minimize its appearance.
     *
     * Moreover, calling the encrypt method directly after a prior call will destroy the previously stored salt.
     *
     * @return The Latest Iv bytes used to encrypt text, and automatically null the attribute.
     */
    public byte[] getLatestIvAutoDestroy() {
        byte[] temp = ivBytes;
        ivBytes = null;

        return temp;
    }

}