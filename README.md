ccstats-rbc
===========
---

**ccstats-rbc** is a library that grants the user useful statistics for their credit statement,
such as amount of transactions, actual total debit and credit grants, etc. **ccstats-rbc** is meant to be a flexible library and have several ways to integrate to environments. As of this revision, **ccstats-rbc** supports the ability to deliver the HTML data through the critical mediums: String input (useful for browser support) and .html/.htm files.

**ccstats-rbc** extracts all transactions and wraps them in very-well structured objects with several statistical methods pre-programmed in the library. Encrypting statements, Statement comparison, and more are to be programmed and integrated in the near future.


Installing
=====
---

Head over to the [release](https://github.com/ahmedsakr/ccstats-rbc/releases) section and download the latest jar.
If you wish to import the dependencies using maven, add the following to your `pom.xml`:

```xml
<project>
    <!-- your other settings -->

    <!-- dependencies for this project-->
    <dependencies>
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.8.3</version>
        </dependency>

        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.6</version>
        </dependency>

        <dependency>
            <groupId>com.googlecode.json-simple</groupId>
            <artifactId>json-simple</artifactId>
            <version>1.1.1</version>
        </dependency>
    </dependencies>
</project>
```

Otherwise, you will have to manually download the libraries and import them to your project.
**Dependencies**: Jsoup-1.8.3, commons-codec-1.10, json-simple-1.1.1


Example
=======

**Example 1**: A programming approach to utilizing the library

```java
try {

    // CreditStatement supports .html and .htm files!
    CreditStatement file = new CreditStatement("C:\\Users\\user_name\\Desktop\\statement.htm");

    // does all the needed work to successfully extract all transactions so they are ready to be used.
    TransactionsExtractor extractor = new TransactionsExtractor(file);

    // receives all transactions, and bundles them into a Statement object that has specific methods
    // regarding statements, in addition to the statistics method that the statement class inherits from.
    Statement statement = new Statement(extractor.read());

    System.out.printf("I have a balance of %.2f.\n", statement.getBalance());

    // Gets all transactions that have descriptions that start with "tim hortons" and outputs them all!
    TransactionPool timmies = statement.getTransactionsByDescription("tim hortons", true);
    timmies.forEach(System.out::println);
    System.out.printf("I have spent an average of %.2f on every transaction at tim hortons!"
        , timmies.getAverageTransaction());

    // gets all transactions that are under 10 bucks and outputs them!
    TransactionPool under10bucks = statement.getTransactionsFrom(0.0, 10);
    under10bucks.forEach(System.out::println);

    // gets all transactions in the month of january and outputs them!
    TransactionPool january = statement.getTransactionsFrom("Jan 01, 2016", "Jan 31, 2016");
    january.forEach(System.out::println);

} catch (InvalidStatementPathException e) {
    e.printStackTrace();
}
```

**Example 2:** A sample encrypted .json output file generated

```json
{
  "transactions":
  {
    "transaction-37":
    {
      "date":"b9a95bee74e7bbf805cbfda604fb4789c6ea4532bc7a4f6f6b9ff5e15bd1a53689d06bbdb0ecbada2935433cea777b6daf2724f3",
      "amount":"c2a699b044785e7c3ca27320b7eb2b81808f74d369a4cf1d064f6888a3f8bc9875ece49394ba64a5d58a85c26b88c255d380e937",
      "authorized":"3ae685e17dbdd00f87ad741c085f7c0f03a676de7ae3ee2e21eb3db0e5a2853a338399b37fa902b0dc2f2fccc301362bf2a7dc0e",
      "description":"c5c861ce549ca1530bbe6d5ad55dc814eb9d5c679724435d6020aa19414cf3c69a049709a8a1b2532cff3e2737d9d5a66451f546cd5b8d89dc55e7a723738f2d56c269b75410ad2415000a0c58580838f996a63e589c526ec813746e5aa3d0e223a4eefe"
    },
    
    "transaction-36":
    {
      "date":"2d29217017aad8b969104b5afbf6d29d7e490a8615a0da2971bd0f707f133dfd0636f5194f1d14fc16f85ffd91c8fe270bdbb4df",
      "amount":"3965eb1c0617606b5699944f5d6715d85c83248169a8d7ab0f4845ed216cb876c67ee38c16b6f71843bbc206b81cbb35c8f06218",
      "authorized":"76cc0b5a01dfcf80ae6c712d960acf18ff4fa1b4e09ed45a63e46a5816f3577a4b13ef8aedea8b754c3d28811a5f59b3f8ee03bf",
      "description":"140ed49bd3c0c67c98879ead9d394c9fea78524a05813cadd1569c781a11892c85378667160dbc98d148322bb93d0d41e5968a6c48e4e97eba11e983f802a45e41b0c9bd"
    }
    
    ...
  }  
}
```

Releases
=====

**Latest build**: [v0.2.5](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.2.5)

**Version Date**: November the 23rd, 2016

Notices
=====
1) Releases **[0.1.00, 0.1.42]** have been deprecated in result of the data-extraction algorithm becoming
obsolete due to RBC discontinuing the old UI. Statements produced from the new UI will not work with these versions.
Please consider installing **v0.1.43** or later.

2) Release **0.1.35** has been detected to fail at collecting transactions correctly. A critical update
has been applied and is now effective as of **0.1.40**.

License
====
This library is licensed under the [Apache 2.0 GNU](http://www.apache.org/licenses/LICENSE-2.0) License.