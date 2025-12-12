package com.techpulse.controller;

import com.techpulse.dto.EmailValidationResponse;
import com.techpulse.response.ApiResponse;
import com.techpulse.service.IEmailValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NamingException;

@RestController
@RequestMapping("/email-validation")
public class EmailValidationController {

    private static final Logger log = LoggerFactory.getLogger(EmailValidationController.class);

    @Autowired
    private IEmailValidationService service;

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse> validateEmail(@RequestParam(name = "email", required = false, defaultValue = "") String email) {
        log.info("EmailValidationController.validateEmail called with email={}", email);

        if (email == null || email.trim().isEmpty()) {
            log.warn("Missing or empty 'email' request parameter");
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Missing required query parameter 'email'", null));
        }

        try {
            // call service
            EmailValidationResponse emailValidationResponse = service.validateEmail(email);
            log.info("EmailValidationService returned: valid={}", emailValidationResponse.isValid());
            return ResponseEntity.ok(
                    new ApiResponse(true, "Email Validation Done Successfully...", emailValidationResponse)
            );
        } catch (NamingException ne) {
            log.error("NamingException during email validation for {}: {}", email, ne.getMessage(), ne);
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Domain lookup error: " + ne.getMessage(), null));
        } catch (Exception ex) {
            log.error("Unexpected error during email validation for {}: {}", email, ex.getMessage(), ex);
            return ResponseEntity.status(500).body(new ApiResponse(false, "Internal error during email validation: " + ex.getMessage(), null));
        }
    }
}
