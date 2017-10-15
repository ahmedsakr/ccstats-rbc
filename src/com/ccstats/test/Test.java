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
        try {
            if (args.length < 2) {
                System.err.println("Invalid number of args.");
                System.exit(1);
            }

            JSONEncryptedStatement io = new JSONEncryptedStatement();
            Statement master = io.read(args[0], args[1]);

            TransactionPool credit = master.getCreditTransactions();
            String[][] billingDates = {
                    {"Sep 01, 2017", "Sep 07, 2017"}, {"Sep 08, 2017", "Sep 14, 2017"}, {"Sep 15, 2017", "Sep 21, 2017"},
                    {"Sep 22, 2017", "Sep 28, 2017"}, {"Sep 29, 2017", "Sep 30, 2017"}, {"Oct 01, 2017", "Oct 07, 2017"},
                    {"Oct 08, 2017", "Oct 14, 2017"}
            };

            TransactionPool transactions;
            for (String[] dates : billingDates) {
                System.out.printf("Transactions statistics for %s -> %s:\n---\n", dates[0], dates[1]);
                transactions = credit.getTransactionsFrom(dates[0], dates[1]);
                System.out.printf("Balance: $%.2f\n", transactions.getBalance());
                System.out.printf("Average / Day: $%.2f\n", transactions.getAverageDay());
                System.out.printf("Standard Deviation: +/- $%.2f\n", transactions.getStandardDeviation());
                System.out.printf("# of transactions: %d\n---\n", transactions.size());
            }
        } catch (IOException | ParseException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
