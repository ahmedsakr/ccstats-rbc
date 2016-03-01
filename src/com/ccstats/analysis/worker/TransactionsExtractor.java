/**
 * Copyright (c) 2015 Ahmed Sakr
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

package com.ccstats.analysis.worker;


import com.ccstats.analysis.TransactionPool;
import com.ccstats.data.Transaction;
import com.ccstats.input.CreditStatement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author Ahmed Sakr
 * @since December 14, 2015
 */
public class TransactionsExtractor {

    private TransactionPool authorized, posted, transactions;


    /**
     * Final integers declaring the indices to be used when accessing the statement tables. Mainly
     * to avoid magic numbers that might cause confusion.
     */
    private static final int AUTHORIZED_TRANSACTIONS = 1, POSTED_TRANSACTIONS = 2;
    private static final int TRANSACTION_DESCRIPTION = 0, TRANSACTION_DEBIT_AMOUNT = 1, TRANSACTION_CREDIT_AMOUNT = 2;


    /**
     * Constructor for the TransactionsExtractor that requires the statement as a parameter.
     * Immediately begins extracting all needed data from the statement.
     *
     * @param statement The CreditStatement instance provided by the user.
     */
    public TransactionsExtractor(CreditStatement statement) {
        try {
            extractTransactions(Jsoup.parse(new File(statement.getAbsolutePath()), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Constructor for the TransactionsExtractor that requires both the statement file and
     * the baseUri as parameters.
     *
     * @param statement The CreditStatement Object.
     * @param baseUri   The Text Encoding.
     */
    public TransactionsExtractor(CreditStatement statement, String baseUri) {
        try {
            extractTransactions(Jsoup.parse(new File(statement.getAbsolutePath()), baseUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Constructor for the TransactionsExtractor. Requires the html text of the document as a parameter.
     *
     * @param html The HTML text as a String object.
     */
    public TransactionsExtractor(String html) {
        try {
            extractTransactions(Jsoup.parse(html, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Constructor for the TransactionsExtractor. Requires both the HTML text of the document and
     * the baseUri in which the text is encoded in.
     *
     * @param html    The HTML text of the document as a String object
     * @param baseUri The Text Encoding.
     */
    public TransactionsExtractor(String html, String baseUri) {
        try {
            extractTransactions(Jsoup.parse(html, baseUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return A TransactionPool Object of All transactions. (authorized and posted)
     */
    public TransactionPool getTransactions() {
        return transactions;
    }


    /**
     * @return A TransactionPool Object of all the authorized transactions, each represented as a
     * AuthorizedTransaction object.
     */
    public TransactionPool getAuthorizedTransactions() {
        return authorized;
    }


    /**
     * @return A TransactionPool Object of all the posted transactions, each represented as a
     * PostedTransaction object.
     */
    public TransactionPool getPostedTransactions() {
        return posted;
    }


    /**
     * Commences extraction of all the transactions, and assigns them to their corresponding attributes.
     *
     * @throws IOException
     */
    private void extractTransactions(Document doc) throws IOException {
        Elements tables = doc.select("table");
        Element authorized = tables.get(AUTHORIZED_TRANSACTIONS);
        Element posted = tables.get(POSTED_TRANSACTIONS);

        this.authorized = extractTransactions(authorized, "authorized");
        this.posted = extractTransactions(posted, "posted");

        this.transactions = new TransactionPool();
        this.transactions.addAll(this.authorized);
        this.transactions.addAll(this.posted);

    }


    /**
     * Extracts all the authorized transactions, provided the authorized transactions table as a parameter.
     *
     * @param table The Authorized transactions table Element
     * @return A TransactionPool object of the Transactions.
     */
    private TransactionPool extractTransactions(Element table, String type) {
        TransactionPool transactions = new TransactionPool();
        Elements rows = table.getElementsByTag("tr");
        rows.remove(0); // this row is just for the headers of the table (description, pending debit, pending credit)

        for (Element transaction : rows) {
            Elements data = transaction.getElementsByTag("td");
            Element debit, credit;
            LocalDate date;
            String description;
            double amount;

            // all dates provided in the statement are in the format MMM dd, yyy (i.e Dec 14, 2015)
            DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            // the date is set as the table header ('th' tag) for every row, and not a 'td' tag
            date = LocalDate.parse(transaction.getElementsByTag("th").get(0).html().trim(), format);
            description = data.get(TRANSACTION_DESCRIPTION).html().trim().replace("<br>", "");
            debit = data.get(TRANSACTION_DEBIT_AMOUNT);
            credit = data.get(TRANSACTION_CREDIT_AMOUNT);

            /**
             * The whole if-else block is mainly testing whether the current transaction of the iteration is
             * a credit or a debit  transaction. If the debit amount column has no children (hence only the value),
             * and it is not empty then it must be a debit transaction. Otherwise, the only other option is a
             * credit transaction.
             */
            if (debit.children().size() == 0 && !debit.html().isEmpty()) {
                amount = Double.valueOf(data.get(TRANSACTION_DEBIT_AMOUNT).html().trim().replace("$", ""));
            } else {

                /**
                 * The amounts of credit are displayed in green, hence symbolizing a grant.
                 * In order to get the amount, the inner class that makes the text green must be
                 * accessed and its value taken.
                 */
                amount = Double.valueOf(credit.html().trim().replace(",", "").replace("$", ""));
            }

            transactions.add(new Transaction(description, date, amount,
                    debit.children().size() == 0 && !debit.html().isEmpty(), type.equals("authorized")));
        }

        return transactions;
    }
}