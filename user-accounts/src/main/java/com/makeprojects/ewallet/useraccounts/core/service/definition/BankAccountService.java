package com.makeprojects.ewallet.useraccounts.core.service.definition;

import com.makeprojects.ewallet.shared.core.definition.AutonomousTransactionAccount;
import com.makeprojects.ewallet.shared.core.definition.CRUDService;
import com.makeprojects.ewallet.shared.core.definition.DelegatedTransactionAccount;
import com.makeprojects.ewallet.shared.database.model.BankAccount;
import com.makeprojects.ewallet.useraccounts.dto.DTAccount.DTAccountRequestDTO;
import com.makeprojects.ewallet.useraccounts.dto.DelegatedTransactionAccountDTO;

import java.util.List;
import java.util.UUID;

public interface BankAccountService extends CRUDService<BankAccount> {

    /**
     * adds a bank account and links it with specified Wallet
     * @param accountDTO accountDTO object
     * @param ataObjId AutonomousTransactionAccount UUID which will link created bank account (e.g. wallet)
     * @return created DelegatedTransactionAccount object
     */
    DelegatedTransactionAccount addDTAccount(DTAccountRequestDTO accountDTO, UUID ataObjId);

    /**
     * Gets DTO of all DelegatedTransactionAccount linked with specified wallet ID.
     * @param autoTransacID UUID of AutonomousTransactionAccount
     * @return List of DelegatedTransactionAccount's DTO linked with specified AutonomousTransactionAccount ID
     */
    List<DelegatedTransactionAccountDTO> getDTAccountsInfoByATId(UUID autoTransacID);

    /**
     * Gets BankAccounts linked with specified Wallet.
     * @param ataObj AutonomousTransactionAccount to check linked DelegatedTransactionAccounts
     * @return List of DelegatedTransactionAccount's DTO linked with specified AutonomousTransactionAccount
     */
    List<DelegatedTransactionAccountDTO> getDTAccountsByAT(AutonomousTransactionAccount ataObj);
}
