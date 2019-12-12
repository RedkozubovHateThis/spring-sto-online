package io.swagger.response.payment.request.embed;

import lombok.Data;

@Data
public class BankInfo {
    private String bankName;
    private String bankCountryCode;
    private String bankCountryName;
}
