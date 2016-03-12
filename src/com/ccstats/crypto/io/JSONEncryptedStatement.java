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


import com.ccstats.crypto.AESWorker;
import com.ccstats.data.Statement;
import com.ccstats.data.Transaction;
import org.json.simple.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;


/**
 *
 * @author Ahmed Sakr
 * @since January 2, 2016.
 */
public class JSONEncryptedStatement {

    private Statement statement;


    /**
     * Default constructor that requires no parameters for the construction of the object, usually useful when
     * the object is being constructed for reading purposes.
     */
    public JSONEncryptedStatement() {

    }


    /**
     * Constructor that takes a plain serialized statement as a parameter, usually useful when the
     * object is being constructed for writing purposes.
     *
     * @param statement The Serialized statement object.
     */
    public JSONEncryptedStatement(Statement statement) {
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
     * @param password The plaintext password to be used for encrypting the data.
     *
     * @throws IOException
     */
    public void write(String absolutePath, String password) throws IOException {

        if (statement == null) {
            return;
        }

        AESWorker worker = new AESWorker();
        JSONObject main = new JSONObject();
        JSONObject authorizedTransactions = new JSONObject(), postedTransactions = new JSONObject();

        try {
            String date, description, amount, type;
            JSONObject transactionObj;
            Transaction transaction;

            if (statement.getAuthorizedTransactions() != null) {
                for (int i = 0; i < statement.getAuthorizedTransactions().size(); i++) {
                    transaction = statement.getAuthorizedTransactions().get(i);
                    transactionObj = new JSONObject();

                    date = new String(worker.encrypt(password, transaction.getDate().toString()));
                    description = new String(worker.encrypt(password, transaction.getDescription()));
                    amount = new String(worker.encrypt(password, String.valueOf(transaction.getAmount())));
                    type = new String(worker.encrypt(password, transaction.getDate().toString()));

                    transactionObj.put("date", date);
                    transactionObj.put("description", description);
                    transactionObj.put("amount", amount);
                    transactionObj.put("type", type);

                    authorizedTransactions.put(String.format("transaction-%d", i + 1), transactionObj);
                }
            }

            if (statement.getPostedTransactions() != null) {
                for (int i = 0; i < statement.getPostedTransactions().size(); i++) {
                    transaction = statement.getAuthorizedTransactions().get(i);
                    transactionObj = new JSONObject();

                    date = new String(worker.encrypt(password, transaction.getDate().toString()));
                    description = new String(worker.encrypt(password, transaction.getDescription()));
                    amount = new String(worker.encrypt(password, String.valueOf(transaction.getAmount())));
                    type = new String(worker.encrypt(password, transaction.getDate().toString()));

                    transactionObj.put("date", date);
                    transactionObj.put("description", description);
                    transactionObj.put("amount", amount);
                    transactionObj.put("type", type);

                    postedTransactions.put(String.format("transaction-%d", i + 1), transactionObj);
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException |
                InvalidParameterSpecException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        main.put("authorized_transactions", authorizedTransactions);
        main.put("posted_transactions", postedTransactions);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absolutePath, false))) {
            writer.write(main.toJSONString());
        }
    }


    /**
     * An override of the original write method with a char array password instead of a String object.
     *
     * @param absolutePath The absolute path of the .json output file. (including the file name)
     * @param password The plaintext password to be used for encrypting the data.
     *
     * @throws IOException
     */
    public void write(String absolutePath, char[] password) throws IOException {
        write(absolutePath, new String(password));
    }


    /**
     * An override of the original write method with the option to provide the filename and the absolute parent path
     * as different parameters as needed.
     *
     * @param filename The .json file name.
     * @param absoluteParentPath The absolute path of the parent folder that will hold the .json file.
     * @param password The plaintext password to be used for encrypting the data.
     *
     * @throws IOException
     */
    public void write(String filename, String absoluteParentPath, char[] password) throws IOException {
        write(absoluteParentPath + "\\" + filename, password);
    }



    /**
     * An override of the original write method with the option to provide the filename and the absolute parent path
     * as different parameters as needed. The password is also a String object instead of the accustomed char
     * array.
     *
     * @param filename The .json file name.
     * @param absoluteParentPath The absolute path of the parent folder that will hold the .json file.
     * @param password The plaintext password to be used for encrypting the data.
     *
     * @throws IOException
     */
    public void write(String filename, String absoluteParentPath, String password) throws IOException {
        write(absoluteParentPath + "\\" + filename, password);
    }
}