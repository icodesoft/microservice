package com.icodesoft.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    private String tokenType;

    private int expiresIn;

    private String accessToken;

    private String refreshToken;
}
