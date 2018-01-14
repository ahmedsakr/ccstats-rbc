package com.ccstats.test;

import com.ccstats.analysis.TransactionPool;
import com.ccstats.analysis.worker.TransactionsExtractor;
import com.ccstats.crypto.io.JSONEncryptedStatement;
import com.ccstats.data.Statement;
import com.ccstats.data.Transaction;
import com.ccstats.input.CreditStatement;
import com.ccstats.input.exceptions.InvalidStatementPathException;
import org.json.simple.parser.ParseException;

import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Test {


    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Invalid number of args.");
            System.exit(1);
        }

        String[][] billingDates = {
            {"Sep 01, 2017", "Sep 07, 2017"}, {"Sep 08, 2017", "Sep 14, 2017"}, {"Sep 15, 2017", "Sep 21, 2017"},
            {"Sep 22, 2017", "Sep 28, 2017"}, {"Sep 29, 2017", "Sep 30, 2017"}, {"Oct 01, 2017", "Oct 07, 2017"},
            {"Oct 08, 2017", "Oct 14, 2017"}, {"Oct 15, 2017", "Oct 21, 2017"}, {"Oct 22, 2017", "Oct 28, 2017"},
            {"Oct 29, 2017", "Nov 04, 2017"}, {"Nov 05, 2017", "Nov 11, 2017"}, {"Nov 12, 2017", "Nov 18, 2017"},
            {"Nov 19, 2017", "Nov 25, 2017"}, {"Nov 26, 2017", "Dec 02, 2017"}, {"Dec 03, 2017", "Dec 09, 2017"},
            {"Dec 10, 2017", "Dec 16, 2017"}, {"Dec 17, 2017", "Dec 23, 2017"}, {"Dec 24, 2017", "Dec 30, 2017"},
            {"Dec 31, 2017", "Jan 06, 2018"}, {"Jan 07, 2018", "Jan 13, 2018"}
        };

        statistics(args[0], billingDates, args[1]);
        //merge(args[0],"/home/asakr/Downloads/9787-statement.html", "Dec 16, 2017", "Jan 13, 2018", args[1]);

    }
    public static void statistics(String statement, String[][] weeks, String password) {
        try {
            JSONEncryptedStatement io = new JSONEncryptedStatement();
            Statement master = io.read(statement, password);

            TransactionPool credit = master.getCreditTransactions();
            TransactionPool transactions;
            for (String[] week : weeks) {
                System.out.printf("Transactions statistics for %s -> %s:\n---\n", week[0], week[1]);
                transactions = credit.getTransactionsFrom(week[0], week[1]);
                System.out.printf("Balance: $%.2f\n", transactions.getBalance());
                System.out.printf("Average / Day: $%.2f\n", transactions.getAverageDay());
                System.out.printf("Standard Deviation: +/- $%.2f\n", transactions.getStandardDeviation());
                System.out.printf("# of transactions: %d\n---\n", transactions.size());
            }

            System.out.println("Total spent since september: " + credit.getBalance());
        } catch (IOException | ParseException | BadPaddingException e) {
            e.printStackTrace();
        }

    }

    public static void merge(String master, String child, String from, String to, String password) {
        try {
            JSONEncryptedStatement io = new JSONEncryptedStatement();
            Statement masterStatement = io.read(master, password);

            TransactionsExtractor extractor = new TransactionsExtractor(new CreditStatement(child));
            Statement childStatement = new Statement(new Statement(extractor.read()).getCreditTransactions().getTransactionsFrom(from, to));
            masterStatement.merge(childStatement);

            io.setStatement(masterStatement);
            io.write(master, password);
        } catch (IOException | ParseException | BadPaddingException | InvalidStatementPathException e) {
            e.printStackTrace();
        }
    }
}
