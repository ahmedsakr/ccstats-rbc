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

    /**
     * Constructor for Statement. Calls the super method of the statistics class.
     *
     * @param transactions The Transactions
     */
    public Statement(TransactionPool transactions) {
        super(transactions);
    }


    /**
     * @return The TransactionPool object of the authorized transactions.
     */
    public TransactionPool getAuthorizedTransactions() {
        TransactionPool authorized = new TransactionPool();
        this.stream().filter(t -> t.isAuthorized()).forEach(authorized::add);
        return authorized;
    }


    /**
     * @return The TransactionPool object of the posted transactions.
     */
    public TransactionPool getPostedTransactions() {
        TransactionPool posted = new TransactionPool();
        this.stream().filter(t -> !t.isAuthorized()).forEach(posted::add);
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