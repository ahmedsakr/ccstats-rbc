/**
 * Copyright (c) 2016 Ahmed Sakr
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

package com.ccstats.analysis.data;


/**
 *
 * @author Ahmed Sakr
 * @since February 6, 2016.
 */
public class TransactionFrequency {

    private int frequency;
    private Transaction transaction;


    /**
     * Constructor for the TransactionFrequency Class.
     * Initializes all needed attributes for any object instance.
     *
     * @param transaction The Transaction being tracked for frequency.
     * @param frequency   The amount of times this transaction is present.
     */
    public TransactionFrequency(Transaction transaction, int frequency) {
        this.transaction = transaction;
        this.frequency = frequency;
    }


    /**
     *
     * @return The Frequency.
     */
    public int getFrequency() {
        return frequency;
    }


    /**
     * Increments the frequency of the transaction.
     */
    public void increment() {
        frequency++;
    }


    /**
     *
     * @return The Transaction.
     */
    public Transaction getTransaction() {
        return transaction;
    }


    /**
     * Overrides the default toString() Object method to return a customized String with valuable information
     * regarding the Frequency.
     *
     * @return A Formatted String with the description, amouunt, and frequency of the transaction.
     */
    @Override
    public String toString() {
        return String.format("[Description=%s,Amount=%.2f, frequency=%d]"
                , transaction.getDescription(), Math.abs(transaction.getAmount()), frequency);
    }
}