package com.makeprojects.ewallet.useraccounts.dto.DTAccount;

import com.makeprojects.ewallet.shared.core.enums.AccountEnums.Banks;
import com.makeprojects.ewallet.shared.core.enums.AccountEnums.AccountStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DTAccountRequestDTO {
    private String accountHolderName;
    private Banks bankName;
    private String accountNumber;
    private String ifscCode;
    private String upiId; // Optional
    private AccountStatus accountStatus;
}

