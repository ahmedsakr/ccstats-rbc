package com.rbccca.analysis;


import com.rbccca.analysis.data.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


/**
 *
 * @author Ahmed Sakr
 * @since December 17, 2015.
 */
public class Statistics {

    private ArrayList<Transaction> transactions;

    public Statistics(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }


    /**
     * @return All the transactions held in this statistics class.
     */
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Calculates the total amount due from all the posted and authorized transactions.
     *
     * @return The Total Amount
     */
    public double getTotalDue() {
        double due = 0;

        for (Transaction transaction : this.transactions) {
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
     * @return All transactions between the dates.
     */
    public ArrayList<Transaction> getTransactionsFrom(String date1, String date2) {
        LocalDate date3 = LocalDate.parse(date1, DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        LocalDate date4 = LocalDate.parse(date2, DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        ArrayList<Transaction> transactions = new ArrayList<>();

        for (Transaction transaction : this.transactions) {
            LocalDate date = transaction.getDate();
            if (isAfterOrEqual(date, date3) && isBeforeOrEqual(date, date4)) {
                transactions.add(transaction);
            }
        }

        return transactions;
    }


    /**
     * Acquires the days between the earliest date and latest date found in the transactions.
     *
     * @return The amount of days between the earliest and latest dates, including both dates which
     * is why + 1 has been added to the return statement.
     */
    public long getDateRange() {
        LocalDate earliest = transactions.get(0).getDate(), latest = transactions.get(0).getDate();

        for (Transaction transaction : this.transactions) {
            if (transaction.getDate().isBefore(earliest)) {
                earliest = transaction.getDate();
            }

            if (transaction.getDate().isAfter(latest)) {
                latest = transaction.getDate();
            }
        }

        System.out.printf("Earliest: %s, Latest: %s", earliest.toString(), latest.toString());

        return ChronoUnit.DAYS.between(earliest, latest) + 1;
    }


    /**
     * Calculates the average amount spent per day from the date range that has transactions only.
     *
     * @return The average amount spent per day.
     */
    public double getAverageDay() {
        return getTotalDue() / getDateRange();
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
        Statistics stats = new Statistics(getTransactionsFrom(date1, date2));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        long days = ChronoUnit.DAYS.between(LocalDate.parse(date1, formatter), LocalDate.parse(date2, formatter));

        return stats.getTotalDue() / (days + 1);
    }
}