package com.makeprojects.ewallet.useraccounts.dto;

import lombok.*;

@Data
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
    private String username;
    private String password;
}
