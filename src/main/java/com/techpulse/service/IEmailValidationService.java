package com.techpulse.service;

import com.techpulse.dto.EmailValidationResponse;

import javax.naming.NamingException;

public interface IEmailValidationService {

    public EmailValidationResponse validateEmail(String email) throws NamingException;
}
