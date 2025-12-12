package com.techpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailValidationResponse {

    private String email;
    private boolean syntaxValid;
    private boolean domainValid;
    private boolean smtpAcceptsRecipient;
    private boolean hasMax;

    private String mxTried;
    private String smtpMessage;
    private int smtpCode;

    // New field: true when all checks (syntax, domain, MX present and SMTP RCPT acceptance) are true
    private boolean valid;

}
