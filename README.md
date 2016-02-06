**Dependencies**: Jsoup-1.8.3

ccstats-rbc
=====
---

**ccstats-rbc** is a library that grants the user useful statistics for their credit statement,
such as amount of transactions, actual total debit and credit grants, etc. **ccstats-rbc** is meant to be a flexible library and have several ways to integrate to environments. As of this revision, **ccstats-rbc** supports the ability to deliver the HTML data through the critical mediums: String input (useful for browser support) and .html/.htm files.

**ccstats-rbc** extracts all transactions and wraps them in very-well structured objects with several statistical methods pre-programmed in the library. Encrypting statements, Statement comparison, and more are to be programmed and integrated in the near future.

Example
=====
---
```java
try {

    // CreditStatement supports .html and .htm files!
    CreditStatement file = new CreditStatement("C:\\Users\\user_name\\Desktop\\statement.htm");

    // does all the needed work to successfully extract all transactions so they are ready to be used.
    TransactionsExtractor extractor = new TransactionsExtractor(file);

    // receives all transactions, and bundles them into a Statement object that has specific methods
    // regarding statements, in addition to the statistics method that the statement class inherits from.
    Statement statement = new Statement(extractor);

    System.out.printf("I have spent a total of %.2f, and an average of %.2f daily!\n"
        , statement.getTotalDue(), statement.getAverageDay());

    // Gets all transactions that have descriptions that start with "tim hortons" and outputs them all!
    TransactionPool timmies = statement.getTransactionsByDescription("tim hortons", true);
    timmies.forEach(System.out::println);
    System.out.printf("I have spent an average of %.2f on every transaction at tim hortons!"
        , timmies.getAverageTransaction());

    // gets all transactions that are under 10 bucks and outputs them!
    TransactionPool under10bucks = statement.getTransactionsFrom(0.0, 10);
    under10bucks.forEach(System.out::println);

    // gets all transactions in the month of january and outputs them!
    TransactionPool january = statement.getTransactionsFrom("Jan 01, 2016", "January 31, 2016");
    january.forEach(System.out::println);

} catch (InvalidStatementPathException e) {
    e.printStackTrace();
}
```
Releases
=====
---
**WARNING**: Releases **[0.1.00, 0.1.10]** have been deprecated in result of the data-extraction algorithm becoming
obsolete due to RBC discontinuing the old UI. Statements produced from the new UI will not work with these versions.
Please consider installing **v0.1.20** or later.

**0.1**: [[0.1.00](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.1.00),
          [0.1.10](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.1.10),
          [0.1.20](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.1.20),
          [0.1.21](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.1.21),
          [0.1.30](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.1.30),
          [0.1.35](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.1.35)]


Currently this is still an active development project. Stay tuned!

~Ahmed Sakr