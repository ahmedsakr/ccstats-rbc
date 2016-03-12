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
        byte[] salt = new byte[20];
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
        byte[] ivBytes = ap.getParameterSpec(IvParameterSpec.class).getIV();

        byte[] result = cipher.doFinal(text);
        return Hex.encodeHex(mergeByteArrays(ivBytes, result, salt));
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
     * An override of the encrypt method with the default AES key length set to be 256 bits.
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
    public char[] encrypt(char[] password, byte[] text) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, InvalidKeyException,
            InvalidKeySpecException {
        return encrypt(password, text, 256);
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
     * Decrypting text that is encrypted by the advanced encryption standard.
     *
     * @param password The char array containing of the plaintext password
     * @param encryptedBlock The Encrypted text to be targeted and decrypted.
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
     */
    public byte[] decrypt(char[] password, char[] encryptedBlock, int keyLength) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, DecoderException {

        password = hash(new String(password).getBytes(StandardCharsets.UTF_8));
        byte[] decoded = Hex.decodeHex(encryptedBlock);

        /**
         * The decoded block has 3 main elements smudged together: ivBytes, encryptedText, and the salt.
         * The ivBytes is of length 16 and the salt is of length 20. Hence, the encryptedText must be the
         * subtraction of the sum of the other bytes.
         */
        byte[] encryptedText = new byte[decoded.length - 36], ivBytes = new byte[16], salt = new byte[20];

        /**
         * The decoded bytes are ordered in the following form: ivBytes + encryptedText + saltBytes.
         * Hence, the proceeding is manipulating the decoded byte's order to grab the ivBytes, encryptedText bytes,
         * and salt bytes and assign them to their correct attributes.
         */
        System.arraycopy(decoded, ivBytes.length, encryptedText, 0, encryptedText.length);
        System.arraycopy(decoded, 0, ivBytes, 0, ivBytes.length);
        System.arraycopy(decoded, decoded.length - salt.length, salt, 0, salt.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        int maxKeyLength = Cipher.getMaxAllowedKeyLength("AES");

        if (maxKeyLength < keyLength) {
            keyLength = maxKeyLength;
            System.err.printf("WARNING: YOUR MAXIMUM AES KEY LENGTH POLICY IS %d BITS. KEY LENGTH LIMITED TO %d BITS.\n",
                    maxKeyLength, maxKeyLength);
        }

        SecretKeyFactory factory = SecretKeyFactory .getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, keyLength);
        SecretKey key = factory.generateSecret(spec);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
        return cipher.doFinal(encryptedText);
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as String
     * objects.
     *
     * @param password The plaintext password as a String.
     * @param encryptedText The encrypted text as a String.
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
    public byte[] decrypt(String password, String encryptedText, int keyLength) throws
            NoSuchPaddingException, DecoderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return decrypt(password.toCharArray(), encryptedText.toCharArray(), keyLength);
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as String
     * objects, and a default value of 256-bits for the AES Key length value.
     *
     * @param password The plaintext password as a String.
     * @param encryptedText The encrypted text as a String.
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
    public byte[] decrypt(String password, String encryptedText) throws
            NoSuchPaddingException, DecoderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return decrypt(password.toCharArray(), encryptedText.toCharArray(), 256);
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as character
     * arrays, and attempt to extract the latest ivBytes and salt for usage. The Default AES Key length is set to
     * 256 bits.
     *
     * @param password The plaintext password as a char array.
     * @param encryptedText The encrypted text as a char array.
     *
     * @return The decrypted byte array of the encrypted text.
     *
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws DecoderException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     */
    public byte[] decrypt(char[] password, char[] encryptedText) throws NoSuchPaddingException, InvalidKeyException,
            DecoderException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeySpecException {
        return decrypt(password, encryptedText, 256);
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


    private byte[] mergeByteArrays(byte[]... arrays) {
        int capacity = 0;
        for (byte[] arr : arrays) {
            capacity += arr.length;
        }

        byte[] result = new byte[capacity];
        int index = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }

        return result;
    }

}