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


package com.rbccca.analysis;


import com.rbccca.analysis.data.RBCHTMLDataExtractor;
import com.rbccca.analysis.data.Transaction;
import com.rbccca.input.CreditStatement;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author Ahmed Sakr
 * @since November 28, 2015.
 */
public class StatementProcessing {

    private RBCHTMLDataExtractor extractor;


    /**
     * Constructor for StatementProcessing. Calls on the extractor object to perform needed operations
     * (acquiring transactions) for the object.
     *
     * @param statement The CreditStatement supplied by the user.
     */
    public StatementProcessing(CreditStatement statement) {
        extractor = new RBCHTMLDataExtractor(statement);
    }


    /**
     * Calculates the total amount due from all the posted and authorized transactions.
     *
     * @return The Total Amount
     */
    public double getTotalDue() {
        return getAuthorizedDue() + getPostedDue();
    }


    /**
     * Calculates the amount due from authorized transactions only.
     *
     * @return The Amount due from authorized transactions.
     */
    public double getAuthorizedDue() {
        double due = 0;

        for (Transaction authorized : extractor.getAuthorizedTransactions()) {
            due += authorized.getAmount();
        }

        return due;
    }


    /**
     * Calculates the amount due from posted transactions only.
     *
     * @return The Amount due from posted transactions.
     */
    public double getPostedDue() {
        double due = 0;

        for (Transaction posted : extractor.getPostedTransactions()) {
            due += posted.getAmount();
        }

        return due;
    }


    /**
     *
     * @return The Size of all the transactions.
     */
    public int getTotalSize() {
        return extractor.getTransactions().size();
    }

    /**
     * @return The Size of the authorized transactions.
     */
    public int getAuthorizedSize() {
        return extractor.getAuthorizedTransactions().size();
    }


    /**
     * @return The Size of the posted transactions.
     */
    public int getPostedSize() {
        return extractor.getPostedTransactions().size();
    }



    /**
     * A method that tests if date1 is after or equal to date2.
     *
     * @param date1 The Date to be tested
     * @param date2 The Date that is being tested on.
     *
     * @return true     if date1 is after or equal to date2
     *         false    otherwise.
     */
    private boolean isAfterOrEqual(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) || date2.isEqual(date2);
    }


    /**
     * Acquires all transactions from date1 up till date2 (exclusive).
     *
     * @param date1 The beginning date.
     * @param date2 The (exclusive) ending date.
     *
     * @return All transactions between the dates.
     */
    public ArrayList<Transaction> getTransactionsFrom(LocalDate date1, LocalDate date2) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction :  extractor.getTransactions()) {
            LocalDate date = transaction.getDate();
            if (isAfterOrEqual(date, date1) && date.isBefore(date2)) {
                transactions.add(transaction);
            }
        }

        return transactions;
    }
}