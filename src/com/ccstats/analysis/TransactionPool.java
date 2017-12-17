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

package com.ccstats.analysis;


import com.ccstats.data.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;


/**
 *
 * @author Ahmed Sakr
 * @since December 17, 2015.
 */
public class TransactionPool extends ArrayList<Transaction> {

    private HashMap<Transaction, TransactionFrequency> frequencies = new HashMap<>();

    /**
     * Default constructor. Allows for no parameter construction of the class.
     */
    public TransactionPool() {

    }


    /**
     * Constructs a new TransactionPool object, and transfers all elements in the ArrayList to the object.
     *
     * @param transactions The ArrayList with elements to be appended to the object.
     */
    public TransactionPool(ArrayList<Transaction> transactions) {
        this.addAll(transactions);
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

        add(i, transaction);

        return true;
    }


    /**
     * Overriding this add method as well to ensure that the list is always sorted.
     *
     * @param i The position in the pool where the transaction element is to be placed.
     * @param transaction The transaction element being inserted into the pool.
     */
    @Override
    public void add(int i, Transaction transaction) {
        super.add(i, transaction);
        updateFrequency(transaction);
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
            add(transaction);
        }

        return true;
    }


    /**
     * Acquires all transactions that are of type debit. A Debit transaction is usually a payment
     * induced by the customer to pay off previous credit transactions.
     *
     * @return The TransactionPool of the debit transactions.
     */
    public TransactionPool getDebitTransactions() {
        TransactionPool pool = new TransactionPool();

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
     * @return The TransactionPool of the credit transactions.
     */
    public TransactionPool getCreditTransactions() {
        TransactionPool pool = new TransactionPool();

        for (Transaction transaction : this) {
            if (transaction.getAmount() >= 0) {
                pool.add(transaction);
            }
        }

        return pool;
    }


    /**
     * Computes the Current outstanding balance of this transaction Pool.
     *
     * @return The Total Amount
     */
    public double getBalance() {
        double amount = 0;

        for (Transaction transaction : this) {
            amount += transaction.getAmount();
        }

        return amount;
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
     * @return The TransactionPool object of all transactions between the required dates.
     * @see this#isAfterOrEqual(LocalDate, LocalDate)
     * @see this#isBeforeOrEqual(LocalDate, LocalDate)
     */
    public TransactionPool getTransactionsFrom(String date1, String date2) {
        LocalDate date3 = LocalDate.parse(date1, DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        LocalDate date4 = LocalDate.parse(date2, DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        TransactionPool pool = new TransactionPool();

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
     * @return The TransactionPool object of the transactions list.
     */
    public TransactionPool getTransactionsFrom(double leastAmount, double highestAmount) {
        TransactionPool pool = new TransactionPool();
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
        if (this.size() == 0) {
            return 0;
        }

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
        if (this.size() == 0) {
            return null;
        }

        LocalDate latest = this.get(0).getDate();
        LocalDate earliest = this.get(this.size() - 1).getDate();

        return new LocalDate[]{earliest, latest};
    }


    /**
     *
     * @return The Average amount spent on a transaction.
     */
    public double getAverageTransactionAmount() {
        if (this.size() == 0) {
            return 0;
        }

        return getBalance() / this.size();
    }


    /**
     * Calculates the average amount spent per day from the date range that has transactions only.
     *
     * @return The average amount spent per day.
     * @see this#getBalance()
     * @see this#getDaysSize()
     */
    public double getAverageDay() {
        if (this.size() == 0) {
            return 0;
        }

        return getBalance() / getDaysSize();
    }


    /**
     * Acquires the average money spent, or projected to be spent, in a week.
     *
     * @return The average amount spent per week.
     * @see this#getAverageDay()
     */
    public double getAverageWeek() {
        return getAverageDay() * 7;
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
     * @see this#getTransactionsFrom(String, String)
     */
    public double getAverageFrom(String date1, String date2) {
        TransactionPool pool = getTransactionsFrom(date1, date2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        long days = ChronoUnit.DAYS.between(LocalDate.parse(date1, formatter), LocalDate.parse(date2, formatter));

        return pool.getBalance() / (days + 1);
    }


    /**
     * Acquires all transactions that revolve around a specified keyword.
     *
     * If startWith is set to true, all transactions that startWith the keyword are acquired.
     * However, if startWith is false then only the transactions that are equal (not case sensitive)
     * to the keyword are acquired.
     *
     * @param keyword The keyword used to select transactions
     * @param contains whether the transaction's description should be equal or startWith the keyword.
     *
     * @return The TransactionPool object of the transactions.
     */
    public TransactionPool getTransactionsByDescription(String keyword, boolean contains) {
        TransactionPool pool = new TransactionPool();
        for (Transaction transaction : this) {
            if (transaction.getDescription().equalsIgnoreCase(keyword)
                    || (contains && transaction.getDescription().contains(keyword))) {
                pool.add(transaction);
            }
        }

        return pool;
    }


    /**
     * Removes all transactions that contain the specified keyword.
     *
     * @param keyword The keyword which is used to find the matching transactions by comparing their descriptions.
     *
     * @return  the status of the removal.
     */
    public boolean removeTransactionsByKeyword(String keyword) {
        final String key = keyword.toLowerCase();
        Object[] removables = this.stream().filter(a -> a.getDescription().toLowerCase().contains(key)).toArray();
        return this.removeAll(Arrays.asList(removables));
    }


    /**
     * Removes all transactions that are equivalent to the transaction argument.
     *
     * @param transaction The transaction that is used as the comporator for targets.
     * @return  the status of the removal.
     *
     */
    public boolean removeTransactionsByEquivalence(Transaction transaction) {
        Object[] removables = this.stream().filter(a -> a.equals(transaction)).toArray();
        return this.removeAll(Arrays.asList(removables));
    }

    /**
     * Computes and gathers all transactions that are equal to the provided transaction object. In other words,
     * all transactions that have the same description and amount.
     *
     * @param transaction The Transaction used to find other equal transactions
     *
     * @return A TransactionPool object for all the transactions.
     * @see Transaction#equals(Object)
     */
    public TransactionPool getTransactionsEqualTo(Transaction transaction) {
        TransactionPool pool = new TransactionPool();
        for (Transaction tran : this) {
            if (tran.equals(transaction)) {
                pool.add(tran);
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
        if (this.size() == 0) {
            return null;
        }

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
        if (this.size() == 0) {
            return null;
        }

        Transaction leastExpensive = this.get(0);

        for (Transaction transaction : this) {
            if (transaction.getAmount() <= leastExpensive.getAmount()) {
                leastExpensive = transaction;
            }
        }

        return leastExpensive;
    }


    /**
     * Computes the standard deviation of the transactions' amount.
     * The Standard Deviation indicates how spread out and random the data (every transaction's amount) are in
     * this pool compared to the average value.
     *
     * @return The Standard Deviation.
     * @see this#getAverageTransactionAmount()
     */
    public double getStandardDeviation() {
        if (this.size() == 0 || this.size() == 1) {
            return 0;
        }

        double weightedSum = 0.0;
        double averageTransaction = getAverageTransactionAmount();

        for (Transaction transaction : this) {
            weightedSum += Math.pow(transaction.getAmount() - averageTransaction, 2);
        }

        return Math.sqrt(weightedSum / (this.size() - 1));
    }


    /**
     * Updates the recurring transactions HashMap accordingly. If no entry exists for the transaction, one
     * is created and initialized.
     *
     * @param transaction The transaction to be inserted or updated in the recurring transactions HashMap.
     */
    private void updateFrequency(Transaction transaction) {
        for (Transaction transaction2 : frequencies.keySet()) {
            if (transaction.equals(transaction2)) {
                frequencies.get(transaction2).increment();
                return;
            }
        }

        frequencies.put(transaction, new TransactionFrequency(transaction, 1));
    }


    /**
     * Searches the frequency HashMap for the transaction that holds the most amount of recurrences, that is a public
     * attribute in the TransactionFrequency Object.
     *
     * @return The Transaction that is the most common in this TransactionPool instance.
     */
    public Transaction getMostCommonTransaction() {
        if (this.size() == 0) {
            return null;
        }

        Transaction mostCommon = frequencies.keySet().iterator().next();

        for (Transaction transaction : frequencies.keySet()) {

            TransactionFrequency freq1 = frequencies.get(transaction);
            TransactionFrequency freq2 = frequencies.get(mostCommon);

            if (freq1.getFrequency() >= freq2.getFrequency()) {
                mostCommon = transaction;
            }
        }

        return mostCommon;
    }
}
