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


import java.time.LocalDate;


/**
 *
 * @author Ahmed Sakr
 * @since December 12, 2015.
 */
public class Transaction {

    private String description;
    private LocalDate date;
    private double amount;
    private boolean authorized;


    /**
     * Constructor for the Transaction class.
     * The Transaction class records a single transaction initiated by the client.
     * Two transaction types may be recorded, Debit and credit transactions.
     *
     * A Debit is the consumption of the credit.
     * A Credit is the grant of credit, paying for any current debits.
     *
     * @param description The Description of the transaction.
     * @param date The date the transaction has been recorded.
     * @param amount The amount due from the transaction.
     * @param debit The type of the transaction (Debit/Credit)
     * @param authorized The transaction's current status (authorized or posted)
     */
    public Transaction(String description, LocalDate date, double amount, boolean debit, boolean authorized) {
        this.description = description.trim();
        this.date = date;
        this.authorized = authorized;

        if (debit) {
            this.amount = amount;
        } else {
            this.amount = - amount;
        }
    }



    /**
     *
     * @return The Description of the transaction.
     */
    public String getDescription() {
        return description;
    }


    /**
     *
     * @return The Date the transaction has been recorded.
     */
    public LocalDate getDate() {
        return date;
    }


    /**
     *
     * @return The amount deducted from the credit limit.
     */
    public double getAmount() {
        return amount;
    }


    /**
     *
     * @return The status of the transaction.
     */
    public boolean isAuthorized() {
        return authorized;
    }


    /**
     * Overriding the toString() Object method to return all attributes of the transaction.
     *
     * @return The attributes of the transaction.
     */
    @Override
    public String toString() {
        String status = isAuthorized() ? "Authorized": "Posted";
        String type = getAmount() >= 0 ? "Credit": "Debit";

        return String.format("[Status: %s, Type: %s, Description: %s, Amount: %s, Date: %s.]"
                , status, type, getDescription(), Math.abs(getAmount()), getDate().toString());
    }


    /**
     * Overriding the equals() Object method to check if the transaction being compared has the exact same
     * attributes as this instance. Two Transactions are categorized equal if both the amount and description match.
     *
     * @param other The other object.
     * @return True if the object is the same instance or different but exact attributes
     *         False otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Transaction)) {
            return false;
        } else if (other != this) {
            Transaction transaction = (Transaction) other;
            boolean equalAmount = this.getAmount() == transaction.getAmount();
            boolean equalName = this.getDescription().equals(transaction.getDescription());
            return equalAmount && equalName;
        }

        return super.equals(other);
    }

}