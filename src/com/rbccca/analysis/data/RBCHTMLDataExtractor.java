package com.rbccca.analysis.data;


import com.rbccca.analysis.data.impl.AuthorizedTransaction;
import com.rbccca.analysis.data.impl.PostedTransaction;
import com.rbccca.input.CreditStatement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


/**
 *
 * @author Ahmed Sakr
 * @since December 14, 2015
 */
public class RBCHTMLDataExtractor {

    private CreditStatement statement;
    private ArrayList<AuthorizedTransaction> authorized;
    private ArrayList<PostedTransaction> posted;


    /**
     * Final integers declaring the indices to be used when accessing the statement tables. Mainly
     * to avoid magic numbers that might cause confusion.
     */
    private static final int AUTHORIZED_TRANSACTIONS = 2, POSTED_TRANSACTIONS = 3;
    private static final int TRANSACTION_DATE = 0, TRANSACTION_DESCRIPTION = 1, TRANSACTION_DEBIT_AMOUNT = 2
                                , TRANSACTION_CREDIT_AMOUNT = 3;


    /**
     * Constructor for the RBCHTMLDataExtractor that requires the statement as a parameter.
     * Immediately begins extracting all needed data from the statement.
     *
     * @param statement The CreditStatement instance provided by the user.
     */
    public RBCHTMLDataExtractor(CreditStatement statement) {
        this.statement = statement;

        try {
            extractTransactions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return An ArrayList of all the authorized transactions, each represented as a
     *         AuthorizedTransaction object.
     */
    public ArrayList<AuthorizedTransaction> getAuthorizedTransactions() {
        return authorized;
    }


    /**
     *
     * @return An ArrayList of all the posted transactions, each represented as a
     *         PostedTransaction object.
     */
    public ArrayList<PostedTransaction> getPostedTransactions() {
        return posted;
    }


    /**
     * Commences extraction of all the transactions, and assigns them to their corresponding attributes.
     *
     * @throws IOException
     */
    private void extractTransactions() throws IOException {
        Document doc = Jsoup.parse(new File(statement.getAbsolutePath()), "UTF-8");
        Elements tables = doc.select("table.contentframework");
        Element authorized = tables.get(AUTHORIZED_TRANSACTIONS);
        Element posted = tables.get(POSTED_TRANSACTIONS);

        this.authorized = extractAuthorizedTransactions(authorized);
        this.posted = extractPostedTransactions(posted);
    }


    /**
     * Extracts all the authorized transactions, provided the authorized transactions table as a parameter.
     *
     * @param transactions The Authorized transactions Element
     *
     * @return ArrayList of the AuthorizedTransactions.
     */
    private ArrayList<AuthorizedTransaction> extractAuthorizedTransactions(Element transactions) {
        ArrayList<AuthorizedTransaction> authorizedTransactions = new ArrayList<>();
        Elements rows = transactions.getElementsByTag("tr");
        rows.remove(0); // this row is just for the headers of the table (description, pending debit, pending credit)

        for (Element transaction : rows) {
            Elements data = transaction.getElementsByTag("td");

            // all dates provided in the statement are in the format MMM dd, yyy (i.e Dec 14, 2015)
            DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            LocalDate date = LocalDate.parse(data.get(TRANSACTION_DATE).html(), format);
            String description = data.get(TRANSACTION_DESCRIPTION).html().replace("<br>", "");
            double amount;

            /**
             * The whole if-else block is mainly testing whether the current transaction of the iteration is
             * a credit or a debit  transaction. If the debit amount column has no children (hence only the value),
             * and it is not empty then it must be a debit transaction. Otherwise, the only other option is a
             * credit transaction.
             */
            if (data.get(TRANSACTION_DEBIT_AMOUNT).children().size() == 0
                    && !data.get(TRANSACTION_DEBIT_AMOUNT).html().isEmpty()) {
                amount = Double.valueOf(data.get(TRANSACTION_DEBIT_AMOUNT).html());
            } else {

                /**
                 * The amounts of credit are displayed in green, hence symbolizing a grant.
                 * In order to get the amount, the inner class that makes the text green must be
                 * accessed and its value taken.
                 */
                amount = Double.valueOf(data.get(TRANSACTION_CREDIT_AMOUNT).child(0).html());
            }

            authorizedTransactions.add(new AuthorizedTransaction(description, date, amount,
                    data.get(TRANSACTION_DEBIT_AMOUNT).children().size() == 0
                            && !data.get(TRANSACTION_DEBIT_AMOUNT).html().isEmpty()));
        }

        return authorizedTransactions;
    }

    private ArrayList<PostedTransaction> extractPostedTransactions(Element transactions) {
        ArrayList<PostedTransaction> postedTransactions = new ArrayList<>();
        Elements rows = transactions.getElementsByTag("tr");
        rows.remove(0); // this row is just for the headers of the table (description, pending debit, pending credit)

        for (Element transaction : rows) {
            Elements data = transaction.getElementsByTag("td");

            // all dates provided in the statement are in the format MMM dd, yyy (i.e Dec 14, 2015)
            DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            LocalDate date = LocalDate.parse(data.get(TRANSACTION_DATE).html(), format);
            String description = data.get(TRANSACTION_DESCRIPTION).html().replace("<br>", "");
            double amount;

            /**
             * The whole if-else block is mainly testing whether the current transaction of the iteration is
             * a credit or a debit  transaction. If the debit amount column has no children (hence only the value),
             * and it is not empty then it must be a debit transaction. Otherwise, the only other option is a
             * credit transaction.
             */
            if (data.get(TRANSACTION_DEBIT_AMOUNT).children().size() == 0
                    && !data.get(TRANSACTION_DEBIT_AMOUNT).html().isEmpty()) {
                amount = Double.valueOf(data.get(TRANSACTION_DEBIT_AMOUNT).html());
            } else {

                /**
                 * The amounts of credit are displayed in green, hence symbolizing a grant.
                 * In order to get the amount, the inner class that makes the text green must be
                 * accessed and its value taken.
                 */
                amount = Double.valueOf(data.get(TRANSACTION_CREDIT_AMOUNT).child(0).html());
            }

            postedTransactions.add(new PostedTransaction(description, date, amount,
                    data.get(TRANSACTION_DEBIT_AMOUNT).children().size() == 0
                            && !data.get(TRANSACTION_DEBIT_AMOUNT).html().isEmpty()));
        }

        return postedTransactions;
    }
}