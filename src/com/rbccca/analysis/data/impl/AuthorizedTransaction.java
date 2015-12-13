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

package com.rbccca.analysis.data.impl;


import com.rbccca.analysis.data.Transaction;

import java.time.LocalDate;


/**
 *
 * @author Ahmed Sakr
 * @since December 13, 2015.
 */
public class AuthorizedTransaction extends Transaction {


    /**
     * Constructor for the AuthorizedTransaction class.
     * The AuthorizedTransaction class records a single transaction initiated by the client that hahs not
     * been processed yet, hence not effective on the amount owed yet. Usually, an authorized transaction is one
     * that has not been settled or held by a conflict.
     *
     * Two transaction types may be recorded, Debit and credit transactions.
     * <p>
     * A Debit is the consumption of the credit.
     * A Credit is the grant of credit, paying for any current debits.
     *
     * @param description The Description of the transaction.
     * @param date        The date the transaction has been recorded.
     * @param amount      The amount due from the transaction.
     * @param debit       The type of the transaction (Debit/Credit)
     */
    public AuthorizedTransaction(String description, LocalDate date, double amount, boolean debit) {
        super(description, date, amount, debit);
    }


    @Override
    public boolean isPosted() {
        return false;
    }
}