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

    public JSONEncryptedStatement() {

    }

    public JSONEncryptedStatement(Statement statement) {
        this.statement = statement;
    }


    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public void write(String absolutePath, String password) throws IOException {

        if (statement == null) {
            return;
        }

        AESWorker worker = new AESWorker();
        JSONObject main = new JSONObject();
        JSONObject authorizedTransactions = new JSONObject(), postedTransactions = new JSONObject();

        try {
            if (statement.getAuthorizedTransactions() != null) {
                for (int i = 0; i < statement.getAuthorizedTransactions().size(); i++) {
                    Transaction transaction = statement.getAuthorizedTransactions().get(i);
                    JSONObject transactionObj = new JSONObject();
                    transactionObj.put("date", worker.encrypt(password, transaction.getDate().toString()));
                    transactionObj.put("description", worker.encrypt(password, transaction.getDescription()));
                    transactionObj.put("amount", worker.encrypt(password, String.valueOf(transaction.getAmount())));
                    transactionObj.put("type", worker.encrypt(password, transaction.getDate().toString()));

                    authorizedTransactions.put(String.format("transaction-%d", i + 1), transactionObj);
                }
            }

            for (int i = 0; i < statement.getPostedTransactions().size(); i++) {
                Transaction transaction = statement.getPostedTransactions().get(i);
                JSONObject transactionObj = new JSONObject();
                transactionObj.put("date", new String(worker.encrypt(password, transaction.getDate().toString())));
                transactionObj.put("description", new String(worker.encrypt(password, transaction.getDescription())));
                transactionObj.put("amount",new String(worker.encrypt(password, String.valueOf(transaction.getAmount()))));
                transactionObj.put("type", new String(worker.encrypt(password, transaction.getDate().toString())));

                postedTransactions.put(String.format("transaction-%d", i + 1), transactionObj);
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


    public void write(String absolutePath, char[] password) throws IOException {
        write(absolutePath, new String(password));
    }


    public void write(String filename, String absoluteParentPath, char[] password) throws IOException {
        write(absoluteParentPath + "\\" + filename, password);
    }

    public void write(String filename, String absoluteParentPath, String password) throws IOException {
        write(absoluteParentPath + "\\" + filename, password);
    }
}
