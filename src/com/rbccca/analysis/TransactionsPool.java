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


import com.rbccca.analysis.data.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 *
 * @author Ahmed Sakr
 * @since December 17, 2015.
 */
public class TransactionsPool extends ArrayList<Transaction> {


    /**
     * Default constructor. Allows for no parameter construction of the class.
     */
    public TransactionsPool() {

    }


    /**
     * Constructs a new TransactionsPool object, and transfers all elements in the ArrayList to the object.
     *
     * @param transactions The ArrayList with elements to be appended to the object.
     */
    public TransactionsPool(ArrayList<Transaction> transactions) {
        sortByDate(transactions);
    }


    /**
     * Override is necessary to assure that every time this method is called, the list remains
     * sorted.
     *
     * @param transaction The transaction being appended to the list.
     *
     * @return True always as this method calls add(index, E e) that is void.
     */
    @Override
    public boolean add(Transaction transaction) {
        int i = 0;

        while (i < this.size() && this.get(i).getDate().isAfter(transaction.getDate())) {
            i++;
        }

        this.add(i, transaction);

        return true;
    }



    /**
     * Override is necessary to assure that every time this method is called, the list remains
     * sorted. Every single element in the collection is tested and its ideal place in the
     * transactions list is found, appending it to that index.
     *
     * @param transactions The transactions collection being appended to the list.
     *
     * @return True always as this method calls add(index, E e) that is void.
     */
    @Override
    public boolean addAll(Collection<? extends Transaction> transactions) {
        for (Transaction transaction : transactions) {

            int i = 0;
            while (i < this.size() && this.get(i).getDate().isAfter(transaction.getDate())) {
                i++;
            }

            this.add(i, transaction);
        }

        return true;
    }


    /**
     * Sorts the transactions by date (reverse chronological order).
     */
    private void sortByDate(ArrayList<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            int i = 0;

            while (i < transactions.size() && transactions.get(i).getDate().isAfter(transaction.getDate())) {
                i++;
            }

            this.add(i, transaction);
        }
    }


    /**
     * Acquires all transactions that are of type debit. A Debit transaction is usually a payment
     * induced by the customer to pay off previous credit transactions.
     *
     * @return The TransactionsPool of the debit transactions.
     */
    public TransactionsPool getDebitTransactions() {
        TransactionsPool pool = new TransactionsPool();

        for (Transaction transaction : this) {
            if (transaction.getAmount() < 0) {
                pool.add(transaction);
            }
        }

        return pool;
    }

    /**
     * Acquires all transactions that are of type credit. A Credit transaction is usually a credit
     * usage of the credit card.
     *
     * @return The TransactionsPool of the credit transactions.
     */
    public TransactionsPool getCreditTransactions() {
        TransactionsPool pool = new TransactionsPool();

        for (Transaction transaction : this) {
            if (transaction.getAmount() >= 0) {
                pool.add(transaction);
            }
        }

        return pool;
    }


    /**
     * Calculates the total amount due from all the posted and authorized transactions.
     *
     * @return The Total Amount
     */
    public double getTotalDue() {
        double due = 0;

        for (Transaction transaction : this) {
            due += transaction.getAmount();
        }

        return due;
    }


    /**
     * A method that tests if date1 is after or equal to date2.
     *
     * @param date1 The Date to be tested
     * @param date2 The Date that is being tested on.
     * @return true     if date1 is after or equal to date2
     * false    otherwise.
     */
    private boolean isAfterOrEqual(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) || date1.isEqual(date2);
    }


    /**
     * A method that tests if date1 is before or equal to date2.
     *
     * @param date1 The Date to be tested
     * @param date2 The Date that is being tested on.
     * @return true     if date1 is before or equal to date2
     * false    otherwise.
     */
    private boolean isBeforeOrEqual(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2) || date1.isEqual(date2);
    }


    /**
     * Acquires all transactions from date1 up till date2 (inclusive).
     *
     * @param date1 The beginning date.
     * @param date2 The (exclusive) ending date.
     * @return The TransactionsPool object of all transactions between the required dates.
     */
    public TransactionsPool getTransactionsFrom(String date1, String date2) {
        LocalDate date3 = LocalDate.parse(date1, DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        LocalDate date4 = LocalDate.parse(date2, DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        TransactionsPool pool = new TransactionsPool();

        for (Transaction transaction : this) {
            LocalDate date = transaction.getDate();

            if (isAfterOrEqual(date, date3) && isBeforeOrEqual(date, date4)) {
                pool.add(transaction);
            }
        }

        return pool;
    }


    /**
     * Acquires all transactions between the minimum and maximum amount range.
     *
     * @param leastAmount The minimum amount a transaction must be to be collected.
     * @param highestAmount The maximum amount a transaction must be to be collected.
     *
     * @return The TransactionsPool object of the transactions list.
     */
    public TransactionsPool getTransactionsFrom(double leastAmount, double highestAmount) {
        TransactionsPool pool = new TransactionsPool();
        for (Transaction transaction : this) {
            double amount = transaction.getAmount();

            if (amount >= leastAmount && amount <= highestAmount) {
                pool.add(transaction);
            }
        }

        return pool;
    }


    /**
     * Acquires the days between the earliest date and latest date found in the transactions.
     * Please note this method has been programmed with a precondition that the list of transactions
     * is sorted at all times. The very first element in the array (index = 0) is the latest, and the last
     * element is the earliest transaction found.
     *
     * @return The amount of days between the earliest and latest dates, including both dates which
     * is why + 1 has been added to the return statement.
     */
    public long getDaysSize() {
        LocalDate latest = this.get(0).getDate();
        LocalDate earliest = this.get(this.size() - 1).getDate();

        return ChronoUnit.DAYS.between(earliest, latest) + 1;
    }


    /**
     * Acquires the latest and earliest dates found in the statement, and returns an array of length
     * 2 elements that contains the earliest and latest LocalDate Objects respectively.
     *
     * @return The LocalDate[] of the LocalDates.
     */
    public LocalDate[] getDateRange() {
        LocalDate latest = this.get(0).getDate();
        LocalDate earliest = this.get(this.size() - 1).getDate();

        return new LocalDate[]{earliest, latest};
    }


    /**
     * Calculates the average amount spent per day from the date range that has transactions only.
     *
     * @return The average amount spent per day.
     */
    public double getAverageDay() {
        return getTotalDue() / getDaysSize();
    }


    /**
     * Acquires the average of all transactions between date 1 and date, inclusive.
     * Unlike method getAverageDay(), Even if there exists no transactions on a certain day, the average
     * still accounts for it.
     * <p>
     * For example, say the date range is from Dec 01, 2015 -> Dec 17, 2015. You have not made any transactions
     * since December 15, 2015. The average will still account for 18 days, not 16. Take a look at the method
     * getAverageDay() if you are looking for the average of those days with transactions only.
     *
     * @param date1 The earliest date of transactions to collect
     * @param date2 The latest date of transactions to collect.
     * @return The average amount spent between date1 and date2.
     */
    public double getAverageFrom(String date1, String date2) {
        TransactionsPool pool = getTransactionsFrom(date1, date2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        long days = ChronoUnit.DAYS.between(LocalDate.parse(date1, formatter), LocalDate.parse(date2, formatter));

        return pool.getTotalDue() / (days + 1);
    }


    /**
     * Acquires all transactions that revolve around a specified keyword.
     *
     * If startWith is set to true, all transactions that startWith the keyword are acquired.
     * However, if startWith is false then only the transactions that are equal (not case sensitive)
     * to the keyword are acquired.
     *
     * @param keyword The keyword used to select transactions
     * @param startWith whether the transaction's description should be equal or startWith the keyword.
     *
     * @return The TransactionsPool object of the transactions.
     */
    public TransactionsPool getTransactionsByDescription(String keyword, boolean startWith) {
        TransactionsPool pool = new TransactionsPool();
        for (Transaction transaction : this) {
            if (transaction.getDescription().equalsIgnoreCase(keyword)
                    || (startWith && transaction.getDescription().startsWith(keyword))) {
                pool.add(transaction);
            }
        }

        return pool;
    }


    /**
     * Acquires the most expensive transaction in the list.
     *
     * @return The Transaction object.
     */
    public Transaction getMostExpensive() {
        Transaction mostExpensive = this.get(0);

        for (Transaction transaction : this) {
            if (transaction.getAmount() >= mostExpensive.getAmount()) {
                mostExpensive = transaction;
            }
        }

        return mostExpensive;
    }


    /**
     * Acquires the least expensive transaction in the list.
     *
     * @return The Transaction object.
     */
    public Transaction getLeastExpensive() {
        Transaction leastExpensive = this.get(0);

        for (Transaction transaction : this) {
            if (transaction.getAmount() <= leastExpensive.getAmount()) {
                leastExpensive = transaction;
            }
        }

        return leastExpensive;
    }
}