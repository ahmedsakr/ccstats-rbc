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


package com.ccstats.data;


import com.ccstats.analysis.TransactionPool;
import com.ccstats.analysis.worker.TransactionsExtractor;


/**
 *
 * @author Ahmed Sakr
 * @since November 28, 2015.
 */
public class Statement extends TransactionPool {


    private TransactionPool authorized, posted;


    /**
     * Constructor for Statement. Calls the super method of the statistics class.
     * Moreover, assigns new statistics objects for the authorized and posted transactions.
     *
     * @param extractor The Transactions extractor.
     */
    public Statement(TransactionsExtractor extractor) {
        super(extractor.getTransactions());

        this.authorized = extractor.getAuthorizedTransactions();
        this.posted = extractor.getPostedTransactions();
    }


    /**
     * Constructor for the statement class. Initializes the class without requiring extraction.
     *
     * @param authorized The authorized transactions pool.
     * @param posted The posted transactions pool.
     */
    public Statement(TransactionPool authorized, TransactionPool posted) {
        this.authorized = authorized;
        this.posted = posted;

        this.addAll(authorized);
        this.addAll(posted);
    }


    /**
     * @return The TransactionPool object of the authorized transactions.
     */
    public TransactionPool getAuthorizedTransactions() {
        return authorized;
    }


    /**
     * @return The TransactionPool object of the posted transactions.
     */
    public TransactionPool getPostedTransactions() {
        return posted;
    }


    /**
     * Merges a second statement into this statement.
     *
     * @param other The Second statement being merged into this statement.
     */
    public void merge(Statement other) {
        this.addAll(other);
    }
}