package com.techpulse.service.impl;

import com.techpulse.dto.EmailValidationResponse;
import com.techpulse.service.IEmailValidationService;
import com.techpulse.util.SmtpValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attribute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


@Service
public class EmailValidationServiceImpl implements IEmailValidationService {

    private static final Logger log = LoggerFactory.getLogger(EmailValidationServiceImpl.class);

    @Override
    public EmailValidationResponse validateEmail(String email) throws NamingException {

        log.info("EmailValidationServiceImpl.validateEmail invoked with email={}", email);

        EmailValidationResponse response = new EmailValidationResponse();
        response.setEmail(email);
        // sensible defaults
        response.setSyntaxValid(false);
        response.setDomainValid(false);
        response.setHasMax(false);
        response.setSmtpAcceptsRecipient(false);
        response.setMxTried(null);
        response.setSmtpMessage(null);
        response.setSmtpCode(-1);
        response.setValid(false);

        if (email == null || email.trim().isEmpty()) {
            response.setSmtpMessage("Email is empty or null");
            response.setSmtpCode(-3);
            response.setValid(false);
            log.warn("Email is null or empty");
            return response;
        }

        //check syntax validity
        boolean isSyntaxValid = EmailValidator.getInstance().isValid(email);
        response.setSyntaxValid(isSyntaxValid);
        log.info("Syntax valid: {}", isSyntaxValid);

        if (!isSyntaxValid) {
            // detect some common domain issues to give a clearer message
            int atIdx = email.indexOf('@');
            if (atIdx < 0 || atIdx == email.length() - 1) {
                response.setSmtpMessage("Invalid email syntax: missing or empty domain part");
            } else {
                String dom = email.substring(atIdx + 1);
                if (dom.contains("..")) {
                    response.setSmtpMessage("Invalid email syntax: domain contains consecutive dots");
                } else if (dom.startsWith(".") || dom.endsWith(".")) {
                    response.setSmtpMessage("Invalid email syntax: domain has leading or trailing dot");
                } else {
                    response.setSmtpMessage("Invalid email syntax");
                }
            }
            response.setSmtpCode(-3); // syntax error
            response.setDomainValid(false);
            response.setHasMax(false);
            response.setSmtpAcceptsRecipient(false);
            response.setValid(false);
            log.warn("Syntax invalid for email {}: {}", email, response.getSmtpMessage());
            return response;
        }

        //extract domain (now syntax is valid so safe to extract)
        String domain;
        int atIdx = email.indexOf('@');
        domain = email.substring(atIdx + 1).toLowerCase().trim();

        // Mx lookup - get list of MX records
        List<String> mxHosts;
        try {
            mxHosts = lookupMxRecords(domain);
        } catch (NamingException ne) {
            response.setHasMax(false);
            response.setDomainValid(false);
            response.setSmtpAcceptsRecipient(false);
            // map DNS problem to a distinct code and friendlier message
            String msg = ne.getMessage();
            if (msg == null) msg = ne.toString();
            String lower = msg.toLowerCase();
            if (lower.contains("empty label")) {
                response.setSmtpMessage("Invalid domain: " + msg);
            } else if (lower.contains("name or service not known") || lower.contains("not found") || lower.contains("no such host")) {
                response.setSmtpMessage("Domain not found: " + domain);
            } else {
                response.setSmtpMessage("MX lookup failed: " + msg);
            }
            response.setSmtpCode(-2); // DNS/MX lookup failure
            response.setMxTried(null);
            response.setValid(false);
            log.warn("MX lookup failed for domain {}: {}", domain, response.getSmtpMessage());
            return response;
        }

        if (mxHosts == null || mxHosts.isEmpty()) {
            response.setHasMax(false);
            response.setDomainValid(false);
            response.setSmtpAcceptsRecipient(false);
            response.setSmtpCode(-2);
            response.setSmtpMessage("No MX or A records found for domain: " + domain);
            response.setMxTried(null);
            response.setValid(false);
            log.warn("No MX/A records found for domain {}", domain);
            return response;
        }

        response.setHasMax(true);
        response.setDomainValid(true);
        log.info("MX hosts found for domain {}: {}", domain, mxHosts);

        // Try each MX host until we get an SMTP acceptance or exhaust list
        String lastMessage = null;
        int lastCode = -1;
        boolean accepted = false;

        for (String mx : mxHosts) {
            response.setMxTried(mx);
            try {
                log.info("Trying SMTP host {} for email {}", mx, email);
                SmtpValidator.SmtpResult smtpResult = SmtpValidator.checkEmail(mx, email);
                lastMessage = smtpResult.getMessage();
                lastCode = smtpResult.getCode();
                log.info("SMTP response from {}: code={}, message={}", mx, lastCode, lastMessage);
                if (smtpResult.isSuccess()) {
                    accepted = true;
                    break;
                }
                // if not accepted, continue to next MX
            } catch (IOException ioe) {
                lastMessage = "SMTP IO Error: " + ioe.getMessage();
                lastCode = -1;
                log.warn("SMTP IO error while contacting {}: {}", mx, ioe.getMessage());
                // try next MX
            }
        }

        response.setSmtpAcceptsRecipient(accepted);
        response.setSmtpMessage(lastMessage == null ? "No SMTP response received" : lastMessage);
        response.setSmtpCode(lastCode == Integer.MIN_VALUE ? -1 : lastCode);

        // final combined valid flag: all checks must be true
        boolean finalValid = response.isSyntaxValid() && response.isDomainValid() && response.isHasMax() && response.isSmtpAcceptsRecipient();
        response.setValid(finalValid);
        log.info("Email validation result for {}: valid={}, smtpAcceptsRecipient={}, smtpCode={}", email, finalValid, response.isSmtpAcceptsRecipient(), response.getSmtpCode());

        return response;
    }

    private List<String> lookupMxRecords(String domain) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext dirContext = new InitialDirContext(env);
        Attributes attrs = dirContext.getAttributes(domain, new String[]{"MX"});

        if (attrs == null) return null;

        Attribute mxAttr = attrs.get("MX");
        List<String> mxHosts = new ArrayList<>();

        if (mxAttr == null) {
            // try A record if MX not present
            Attributes aAttrs = dirContext.getAttributes(domain, new String[]{"A"});
            Attribute aAttr = aAttrs.get("A");
            if (aAttr != null) {
                NamingEnumeration<?> en = aAttr.getAll();
                while (en.hasMore()) {
                    Object val = en.next();
                    // use the domain itself as host if A exists
                    mxHosts.add(domain);
                }
            }
            return mxHosts;
        }

        NamingEnumeration<?> enumeration = mxAttr.getAll();
        while (enumeration.hasMore()) {
            String record = enumeration.next().toString();
            // MX record format: "priority host"
            String[] parts = record.split("\\s+");
            String host = parts[parts.length - 1];
            // ensure host ends with a dot for SMTP connect normalization
            if (!host.endsWith(".")) host = host + ".";
            mxHosts.add(host);
        }

        return mxHosts;
    }
}
