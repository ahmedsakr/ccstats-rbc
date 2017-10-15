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
    
    // the default value the worker will assume wit be 256 bits for the key length.
    private int keyLength = 256;


    /**
     * Returns the running key length of this worker instance.
     *
     * @return an integer key length value
     */
    public int getKeyLength() {
        return this.keyLength;
    }


    /**
     * Sets the running key length of this worker instance. Any value bigger than
     * the maximum policy will be automatically reduced to the maximum policy.
     * IMPORTANT: Value must be power of two.
     *
     * @param keyLength the new key length in bits
     */
    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

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
     *
     * @return The Encrypted text in hexadecimal format.
     */
    public char[] encrypt(char[] password, byte[] text) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        if (Cipher.getMaxAllowedKeyLength("AES") < this.keyLength) {
            this.keyLength = Cipher.getMaxAllowedKeyLength("AES");
            System.err.printf("WARNING: YOUR MAXIMUM AES KEY LENGTH POLICY IS %d BITS. KEY LENGTH LIMITED TO %d BITS.\n",
                            this.keyLength, this.keyLength);
        }

        // hash the password and acquire a securely and randomly generated salt
        password = hash(new String(password).getBytes(StandardCharsets.UTF_8));
        byte[] salt = new byte[20];
        new SecureRandom().nextBytes(salt);

        // acquire the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 16384, this.keyLength);
        SecretKey key = factory.generateSecret(spec);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // init the cipher and process the encryption
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        AlgorithmParameters ap = cipher.getParameters();
        byte[] ivBytes = ap.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] result = cipher.doFinal(text);

        return Hex.encodeHex(mergeByteArrays(ivBytes, result, salt));
    }


    /**
     * An override of the encrypt method with a default keyLength value of 256-bits.
     *
     * @param password The password used to encrypt the text.
     * @param text The text to be encrypted using the advanced encryption standard.
     *
     * @return The bytes of encrypted text.
     */
    public char[] encrypt(String password, String text) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, BadPaddingException,
            IllegalBlockSizeException {
        return encrypt(password.toCharArray(), text.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * Decrypting text that is encrypted by the advanced encryption standard.
     *
     * @param password The char array containing of the plaintext password
     * @param encryptedBlock The Encrypted text to be targeted and decrypted.
     *
     * @return The decrypted byte array of the encrypted text.
     */
    public byte[] decrypt(char[] password, char[] encryptedBlock) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, InvalidAlgorithmParameterException, DecoderException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        if (Cipher.getMaxAllowedKeyLength("AES") < this.keyLength) {
            this.keyLength = Cipher.getMaxAllowedKeyLength("AES");
            System.err.printf("WARNING: YOUR MAXIMUM AES KEY LENGTH POLICY IS %d BITS. KEY LENGTH LIMITED TO %d BITS.\n",
                        this.keyLength, this.keyLength);
        }

        // hash the password with the MD5 function and decode the encryptedBlock
        password = hash(new String(password).getBytes(StandardCharsets.UTF_8));
        byte[] decoded = Hex.decodeHex(encryptedBlock);

        // The decoded byte array has the IV, encryptedText, and salt bytes stored in that order.
        // The IV bytes are of length 16 and salt is of length 20.
        byte[] encryptedText = new byte[decoded.length - 36], ivBytes = new byte[16], salt = new byte[20];

        // The decoded bytes are ordered in the following form: ivBytes + encryptedText + saltBytes.
        // Extract the bytes into their corresponding array.
        System.arraycopy(decoded, 0, ivBytes, 0, ivBytes.length);
        System.arraycopy(decoded, ivBytes.length, encryptedText, 0, encryptedText.length);
        System.arraycopy(decoded, decoded.length - salt.length, salt, 0, salt.length);

        // generate the key from the acquired data
        SecretKeyFactory factory = SecretKeyFactory .getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 16384, this.keyLength);
        SecretKey key = factory.generateSecret(spec);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // finally, attempt to decrypt the encryptedText
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
        return cipher.doFinal(encryptedText);
    }


    /**
     * An override of the decrypt method with the ability to provide the password and encryptedText as String
     * objects, and a default value of 256-bits for the AES Key length value.
     *
     * @param password The plaintext password as a String.
     * @param encryptedText The encrypted text as a String.
     *
     * @return The decrypted byte array of the encrypted text.
     */
    public byte[] decrypt(String password, String encryptedText) throws NoSuchPaddingException, DecoderException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return decrypt(password.toCharArray(), encryptedText.toCharArray());
    }


    /**
     * Hashes the plain password to provide a more secure experience.
     *
     * @param password the bytes of the plaintext password.
     *
     * @return The hashed password's characters in an array.
     */
    private char[] hash(byte[] password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(password);

        return Hex.encodeHex(md.digest());
    }


    /**
     * Merges all the byte[] varargs.
     *
     * @param arrays The byte[] varargs
     *
     * @return The master byte[] containing all the byte arrays.
     */
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
