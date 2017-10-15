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

package com.ccstats.crypto.io;


import com.ccstats.analysis.TransactionPool;
import com.ccstats.crypto.AESWorker;
import com.ccstats.data.Statement;
import com.ccstats.data.Transaction;
import org.apache.commons.codec.DecoderException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.time.LocalDate;


/**
 *
 * @author Ahmed Sakr
 * @since January 2, 2016.
 */
public class JSONEncryptedStatement {

    private Statement statement;
    private AESWorker worker;


    /**
     * Default constructor that requires no parameters for the construction of the object, usually useful when
     * the object is being constructed for reading purposes.
     */
    public JSONEncryptedStatement() {
        worker = new AESWorker();
    }


    /**
     * Constructor that takes a plain serialized statement as a parameter, usually useful when the
     * object is being constructed for writing purposes.
     *
     * @param statement The Serialized statement object.
     */
    public JSONEncryptedStatement(Statement statement) {
        this();
        this.statement = statement;
    }


    /**
     * Overrides the private statement attribute.
     *
     * @param statement The new statement.
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }


    /**
     * Encrypts and writes all the data from the plain serialized statement to a .json file.
     *
     * @param absolutePath The absolute path of the .json output file. (including the file name)
     * @param password     The plaintext password to be used for encrypting the data.
     */
    public void write(String absolutePath, String password) throws IOException {

        if (statement == null) {
            return;
        }

        JSONObject main = new JSONObject();
        JSONObject transactions = new JSONObject();

        try {
            String date, description, amount, authorized;
            JSONObject transactionObj;
            Transaction transaction;

            if (!statement.isEmpty()) {
                for (int i = 0; i < statement.size(); i++) {
                    transaction = statement.get(i);
                    transactionObj = new JSONObject();

                    date = new String(worker.encrypt(password, transaction.getDate().toString()));
                    description = new String(worker.encrypt(password, transaction.getDescription()));
                    amount = new String(worker.encrypt(password, String.valueOf(transaction.getAmount())));
                    authorized = new String(worker.encrypt(password, String.valueOf(transaction.isAuthorized())));

                    transactionObj.put("date", date);
                    transactionObj.put("description", description);
                    transactionObj.put("amount", amount);
                    transactionObj.put("authorized", authorized);

                    transactions.put(String.format("transaction-%d", i + 1), transactionObj);
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException |
                InvalidParameterSpecException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        main.put("aes-key-length", String.valueOf(worker.getKeyLength()));
        main.put("transactions", transactions);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath, false))) {
            writer.write(main.toJSONString());
        }
    }


    /**
     * An override of the original write method with a char array password instead of a String object.
     *
     * @param absolutePath The absolute path of the .json output file. (including the file name)
     * @param password     The plaintext password to be used for encrypting the data.
     */
    public void write(String absolutePath, char[] password) throws IOException {
        write(absolutePath, new String(password));
    }


    /**
     * An override of the original write method with the option to provide the filename and the absolute parent path
     * as different parameters as needed.
     *
     * @param filename           The .json file name.
     * @param absoluteParentPath The absolute path of the parent folder that will hold the .json file.
     * @param password           The plaintext password to be used for encrypting the data.
     */
    public void write(String filename, String absoluteParentPath, char[] password) throws IOException {
        write(absoluteParentPath + "\\" + filename, password);
    }


    /**
     * An override of the original write method with the option to provide the filename and the absolute parent path
     * as different parameters as needed. The password is also a String object instead of the accustomed char
     * array.
     *
     * @param filename           The .json file name.
     * @param absoluteParentPath The absolute path of the parent folder that will hold the .json file.
     * @param password           The plaintext password to be used for encrypting the data.
     */
    public void write(String filename, String absoluteParentPath, String password) throws IOException {
        write(absoluteParentPath + "\\" + filename, password);
    }


    /**
     * Reads an encrypted .json statement file and attempts to decrypt it. Once decrypted, the transactions can be
     * pooled and returned as a joint statement.
     *
     * @param absolutePath The absolute path to the encrypted statement, including the file name.
     * @param password The password sequence to be used while attempting the decryption.
     *
     * @return A statement object containing all the discovered transactions as a pool.
     */
    public Statement read(String absolutePath, String password) throws IOException, ParseException, BadPaddingException {
        TransactionPool transactions = new TransactionPool();
        JSONParser parser = new JSONParser();

        JSONObject main = (JSONObject) parser.parse(new FileReader(absolutePath));
        JSONObject encryptedTransactions = (JSONObject) main.get("transactions");

        worker.setKeyLength(Integer.valueOf((String) main.get("aes-key-length")));
        try {

            JSONObject current;
            String date, description, amount, authorized;
            for (Object o : encryptedTransactions.values()) {
                current = (JSONObject) o;
                date = new String(worker.decrypt(password, (String) current.get("date")));
                description = new String(worker.decrypt(password, (String) current.get("description")));
                amount = new String(worker.decrypt(password, (String) current.get("amount")));
                authorized = new String(worker.decrypt(password, (String) current.get("authorized")));

                transactions.add(new Transaction(description, LocalDate.parse(date), Double.valueOf(amount),
                        Boolean.valueOf(authorized)));
            }

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | DecoderException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }


        return new Statement(transactions);
    }


    /**
     * An override to the write method with the minor change of allowing the password to be given as a char
     * array.
     *
     * @param absolutePath The absolute path to the encrypted statement, including the file name.
     * @param password The password sequence to be used while attempting the decryption.
     *
     * @return A statement object containing all the discovered transactions as a pool.
     */
    public Statement read(String absolutePath, char[] password) throws ParseException, BadPaddingException, IOException {
        return read(absolutePath, new String(password));
    }


    /**
     * An override to the write method with the following changes: allowing the filename and absolute parent path
     * to be input separately, and the password as a char array.
     *
     * @param fileName the alias of the encrypted statement.
     * @param absoluteParentPath The absolute path to the encrypted statement's parent folder.
     * @param password The password sequence to be used while attempting the decryption.
     *
     * @return A statement object containing all the discovered transactions as a pool.
     */
    public Statement read(String fileName, String absoluteParentPath, char[] password) throws ParseException,
            BadPaddingException, IOException {
        return read(absoluteParentPath + "\\" + fileName, password);
    }


    /**
     * An override to the write method with the minor change of allowing the password to be a String object.
     *
     * @param fileName the alias of the encrypted statement.
     * @param absoluteParentPath The absolute path to the encrypted statement's parent folder.
     * @param password The password sequence to be used while attempting the decryption.
     *
     * @return A statement object containing all the discovered transactions as a pool.
     */
    public Statement read(String fileName, String absoluteParentPath, String password) throws ParseException,
            BadPaddingException, IOException {
        return read(absoluteParentPath + "\\" + fileName, password);
    }
}
