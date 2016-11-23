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

    private Document doc;


    /**
     * Final integers declaring the indices to be used when accessing the statement tables. Mainly
     * to avoid magic numbers that might cause confusion.
     */
    private static final int AUTHORIZED_TRANSACTIONS = 0, POSTED_TRANSACTIONS = 1;
    private static final int TRANSACTION_DESCRIPTION = 0, TRANSACTION_DEBIT_AMOUNT = 1, TRANSACTION_CREDIT_AMOUNT = 2;

    /**
     * Constructor for the TransactionsExtractor that requires the statement as a parameter.
     * Immediately begins extracting all needed data from the statement.
     *
     * @param statement The CreditStatement instance provided by the user.
     */
    public TransactionsExtractor(CreditStatement statement) throws IOException {
        this.doc = Jsoup.parse(new File(statement.getAbsolutePath()), "UTF-8");
    }


    /**
     * Constructor for the TransactionsExtractor that requires both the statement file and
     * the baseUri as parameters.
     *
     * @param statement The CreditStatement Object.
     * @param baseUri   The Text Encoding.
     */
    public TransactionsExtractor(CreditStatement statement, String baseUri) throws IOException {
        this.doc = Jsoup.parse(new File(statement.getAbsolutePath()), baseUri);
    }


    /**
     * Constructor for the TransactionsExtractor. Requires the html text of the document as a parameter.
     *
     * @param html The HTML text as a String object.
     */
    public TransactionsExtractor(String html) {
        this.doc = Jsoup.parse(html, "UTF-8");
    }


    /**
     * Constructor for the TransactionsExtractor. Requires both the HTML text of the document and
     * the baseUri in which the text is encoded in.
     *
     * @param html    The HTML text of the document as a String object
     * @param baseUri The Text Encoding.
     */
    public TransactionsExtractor(String html, String baseUri) {
        this.doc = Jsoup.parse(html, baseUri);
    }

    /**
     * Overrides the instance source from which the transactions are being parsed from.
     *
     * @param statement A CreditStatement object that holds the path to the source
     */
    public void setSource(CreditStatement statement) throws IOException {
        this.doc = Jsoup.parse(new File(statement.getAbsolutePath()), "UTF-8");
    }

    /**
     * Overrides the instance source from which the transactions are being parsed from.
     *
     * @param statement A CreditStatement object that holds the path to the source
     * @param charset   The charset name
     */
    public void setSource(CreditStatement statement, String charset) throws IOException {
        this.doc = Jsoup.parse(new File(statement.getAbsolutePath()), charset);
    }

    /**
     * Overrides the instance source from which the transactions are being parsed from.
     *
     * @param html A String object that stores the HTML
     */
    public void setSource(String html) {
        this.doc = Jsoup.parse(html, "UTF-8");
    }

    /**
     * Overrides the instance source from which the transactions are being parsed from.
     *
     * @param html A String object that stores the HTML
     * @param charset The charset name
     */
    public void setSource(String html, String charset) {
        this.doc = Jsoup.parse(html, charset);
    }


    /**
     * Parses the source, instantiates transaction objects from the read data, and appends them to
     * a TransactionPool.
     *
     * @return The TransactionPool object containing all read transactions.
     */
    public TransactionPool read() {
        if (this.doc == null) {
            return null;
        }

        return parseTransactions(this.doc);
    }

    /**
     * parses the transactions present in the document.
     *
     * @param doc The JSoup document object
     *
     * @return A TransactionPool object containing all parsed transactions.
     */
    private TransactionPool parseTransactions(Document doc) {
        TransactionPool p = new TransactionPool();
        Elements tables = doc.select("table");

        if (tables.isEmpty()) {
            return null;
        }

        tables.remove(0); // remove header table as it is useless

        boolean hasAuthorized = tables.size() >= 2;
        if (hasAuthorized) {
            Element authorized = tables.get(AUTHORIZED_TRANSACTIONS);
            Element posted = tables.get(POSTED_TRANSACTIONS);

            p.addAll(extractTransactions(authorized, true));
            p.addAll(extractTransactions(posted, false));
        } else if (!tables.isEmpty()) {
            Element posted = tables.first();
            p.addAll(extractTransactions(posted, false));
        }

        return p;
    }


    /**
     * Extracts transactions from the provided table element.
     *
     * @param table The JSoup element representing the table
     *
     * @return A TransactionPool object of the Transactions.
     */
    private TransactionPool extractTransactions(Element table, boolean authorized) {
        TransactionPool transactions = new TransactionPool();
        Elements rows = table.getElementsByTag("tr");
        rows.remove(0); // this row is just for the headers of the table (description, pending debit, pending credit)

        // all dates provided in the statement are in the format MMM dd, yyy (i.e Dec 14, 2015)
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Element transaction : rows) {
            Elements data = transaction.getElementsByTag("td");
            Element debit, credit;
            LocalDate date;
            String description;
            double amount;

            // the date is set as the table header ('th' tag) for every row, and not a 'td' tag
            date = LocalDate.parse(transaction.getElementsByTag("th").get(0).html().trim(), format);
            description = data.get(TRANSACTION_DESCRIPTION).html().trim().replace("<br>", "");
            debit = data.get(TRANSACTION_DEBIT_AMOUNT);
            credit = data.get(TRANSACTION_CREDIT_AMOUNT);

            // categorizing the transaction as either a debit or credit transaction.
            // A Debit transaction takes a negative sign to indicate a decrease in credit.
            if (debit.children().size() == 0 && !debit.html().isEmpty()) {
                amount = Double.valueOf(debit.html().trim().replace("$", ""));
            } else {
                amount = - Double.valueOf(credit.html().trim().replace(",", "").replace("$", ""));
            }

            transactions.add(new Transaction(description, date, amount, authorized));
        }

        return transactions;
    }
}