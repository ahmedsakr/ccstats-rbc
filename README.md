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
    Statement statement = new Statement(extractor);

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
   "authorized_transactions":{
      "transaction-1":{
         "date":"548b2199a3cf674d8a6a9f99b6b13eeb17703f67e2056a2d80e032d6d7b093e29e6303f5fe458718c74463227b1b5b35c10c22a1",
         "amount":"cc0bc0fd6342df35a0d5085d70a5e11445b44e21f3e7031519d3e051178a2698f3515c9cacf60b864d5ceb8777bb7ab3cf37311c",
         "description":"96f3fb69b63c517ce3df6d19ae4b74377e6eec691ad34a2341d73d67975740ed095f2085999de46ba22ce9c0536849ba74346faeee52683b62068b6a0f143c95011b4d58",
         "type":"1289916b48ae36b4380edca0a34df0002cb9d8580e4947e10142abe201dd89f08e0e3358778471bb3863066d8550457aeb73ffdd"
      }
   },
   "posted_transactions":{
      "transaction-1":{
         "date":"dfe1e91bb92acf285fe39f56ccb36e5ca20b8f9d05f936e035ccba654f042f66be284eb6beff68c1d0a813f32019a073c8ae714e",
         "amount":"af632b332a08b75e2b158915383814656ba98e2e366125567bc3a1b2ee7883f1f4b258cd51a9bd5f576e3ddfed37f824a1fa2e51",
         "description":"0f8ef7354a145681cb6849b6a480217506f8b306931a587f30b059337190570924ac5ad1fac5248b979ceec88b2f81caad8cf27c1ea4295157e844e857e4c5b3d2b97f3f",
         "type":"af9a53d83029fbeaaeee557a63e5ecb480fb730512d24a319fc87f53a40e2bc504ae98ed355f3c200a87207fe351ef0fe5bcfb4c"
      },
      "transaction-2":{
         "date":"2e094e85d98e384356c6d4785dd6acfd742eafd7b4179dca3505f92a635758edc96bda9bf199fdd03e71dcdbbaef801cabff9434",
         "amount":"167e5d93b890bbed104b1406e20f9d351d3a6dced1296f30aecde35fa77221b4bc916ee207f8fe6e95b96862cdaa23fd74532a85",
         "description":"db25e8c2cd4e04818177e26f4bbbb5aa307daf3e7343b0bffb3f10596bcb781e840adb543d27897516a981ce21e14a684e0cd7cf2e0d663e3fe72a0b7064381aaaf30d9191585df52fc51e1936c6ced52b474ca9",
         "type":"0199d1c2a914f6a5ac124d5ae61636b5396a7ae33a746a8b8b59445d5d534f8cecd3feab3380a7c896db00708151154e8ca34dea"
      },
      "transaction-3":{
         "date":"9969a72b8fd6da7269d7e2386f6e198818afcf3d741fcf5ba00f3fef65d9faa9213eacd610556bb7b6fcf9e61f8e36a3de7e67aa",
         "amount":"918459ed927d07db0e62e06869d828d29d798c762bb5862e9766756ccf6b856fb3fcbb2a986392295fc53f425d79054bf1036de1",
         "description":"a3a7fcafdc2616f77b6d3b90913197b8584a8aaa132ec8ad50c2f2e3556a5b20a58b95ab71d1b55d081e6b25f105361d941ffa41fe5f34c669cdc08a53d81c29c072ef0dac8216868ecd35a40a54957ccbc3fd15",
         "type":"24a5ed9ca1a9498f0996b56fe14d2bbd3e403a83f5fb2d1ee4e5019b5cdd1d0e846876f4be1fffe8278157d15c37e992015ede09"
      }
   }
}
```

Releases
=====
---

**WARNING**: Releases **[0.1.00, 0.1.42]** have been deprecated in result of the data-extraction algorithm becoming
obsolete due to RBC discontinuing the old UI. Statements produced from the new UI will not work with these versions.
Please consider installing **v0.1.43** or later.

**WARNING**: Release **0.1.35** has been detected to fail at collecting transactions correctly. A critical update
has been applied and is now effective as of **0.1.40**.

**Latest build**: v[0.2.1](https://github.com/ahmedsakr/ccstats-rbc/releases/tag/0.2.1)

License
====
---
This library is licensed under the [Apache 2.0 GNU](http://www.apache.org/licenses/LICENSE-2.0) License.