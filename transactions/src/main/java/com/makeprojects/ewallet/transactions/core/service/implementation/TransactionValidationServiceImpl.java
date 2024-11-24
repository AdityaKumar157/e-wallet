package com.makeprojects.ewallet.transactions.core.service.implementation;

import com.makeprojects.ewallet.shared.database.model.Transaction;
import com.makeprojects.ewallet.transactions.core.service.definition.TransactionValidationService;
import org.springframework.stereotype.Component;

@Component(value = "transaction")
public class TransactionValidationServiceImpl implements TransactionValidationService {

    @Override
    public boolean validate(Transaction obj) {
        boolean amountValidation = obj.getSenderAccount().getBalance() >= obj.getAmount();
        boolean kycValidation = obj.getSenderAccount().isKycComplete() && obj.getReceiverAccount().isKycComplete();
        return amountValidation && kycValidation;
    }
}
