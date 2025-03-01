package com.makeprojects.ewallet.useraccounts.dto.DTAccount;


import com.makeprojects.ewallet.shared.core.enums.AccountEnums.Banks;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@Data
public class DTAccountResponseDTO {
    private UUID bankAccountId;
    private Banks bankName;
    private String accountNumber;
}
