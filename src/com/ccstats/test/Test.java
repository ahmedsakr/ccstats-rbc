package com.ccstats.test;

import com.ccstats.analysis.TransactionPool;
import com.ccstats.analysis.worker.TransactionsExtractor;
import com.ccstats.data.Statement;
import com.ccstats.input.CreditStatement;
import com.ccstats.input.exceptions.InvalidStatementPathException;

import java.io.IOException;


public class Test {


    public static void main(String[] args) {
        try {

            CreditStatement file = new CreditStatement("/home/ahmed/Downloads/1119-statement.html");
            CreditStatement file1 = new CreditStatement("/home/ahmed/Downloads/9787-statement.html");

            TransactionsExtractor extractor = new TransactionsExtractor(file);
            Statement master = new Statement(extractor.read());

            extractor.setSource(file1);
            Statement statement1 = new Statement(extractor.read());
            master.merge(statement1);

            //master.forEach(System.out::println);
            TransactionPool transactions = master.getCreditTransactions().getTransactionsFrom("Sep 01, 2017", "Oct 01, 2017");
            transactions.removeTransactionsByKeyword("ROGERS");
            transactions.removeTransactionsByKeyword("HYDRO");
            transactions.removeTransactionsByKeyword("Amazon *Marketplce");
            transactions.forEach(System.out::println);
            System.out.println(transactions.getBalance());
            System.out.println(transactions.getAverageDay());
        } catch (InvalidStatementPathException | IOException e) {
            e.printStackTrace();
        }
    }
}
