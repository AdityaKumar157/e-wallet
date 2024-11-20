package com.makeprojects.ewallet.transactions.util;

import com.makeprojects.ewallet.shared.model.Transaction;

public abstract class TransactionValidation {

    public static boolean isValidTransaction(Transaction transaction) {
        boolean amountValidation = transaction.getSenderAccount().getBalance() >= transaction.getAmount();
        boolean kycValidation = transaction.getSenderAccount().isKycComplete() && transaction.getReceiverAccount().isKycComplete();
        return amountValidation && kycValidation;
    }
}
