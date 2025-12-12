package com.techpulse.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SmtpValidator {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SmtpResult {
        private boolean success;
        private String message;
        private int code;

    }

    public static SmtpResult checkEmail(String mxHost, String email) throws IOException {

        SmtpResult result = new SmtpResult();
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            socket = new Socket();
            // connect with timeout
            socket.connect(new InetSocketAddress(mxHost, 25), 7000);
            socket.setSoTimeout(7000); // read timeout

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Read server greeting (could be multi-line)
            String greeting = readMultiLineResponse(reader);
            int code = parseCodeFromMultiLine(greeting);
            if (code < 0) {
                result.setSuccess(false);
                result.setCode(-1);
                result.setMessage(greeting == null ? "No greeting" : greeting.trim());
                return result;
            }

            // EHLO
            writer.println("EHLO localhost");
            String ehloResponse = readMultiLineResponse(reader);

            // MAIL FROM
            writer.println("MAIL FROM:<validator@localhost>");
            String mailResp = reader.readLine();
            int mailCode = parseCode(mailResp);
            if (mailResp == null || mailCode < 200 || mailCode >= 400) {
                result.setSuccess(false);
                result.setCode(mailCode < 0 ? -1 : mailCode);
                result.setMessage(mailResp == null ? "No MAIL FROM response" : mailResp);
                writer.println("QUIT");
                return result;
            }

            // RCPT TO
            writer.println("RCPT TO:<" + email + ">");
            String rcptResp = reader.readLine();
            int rcptCode = parseCode(rcptResp);
            result.setCode(rcptCode < 0 ? -1 : rcptCode);
            result.setMessage(rcptResp == null ? "No RCPT response" : rcptResp);
            result.setSuccess(rcptCode == 250 || rcptCode == 251);

            // QUIT
            writer.println("QUIT");

        } catch (SocketTimeoutException ste) {
            result.setSuccess(false);
            result.setCode(-1);
            result.setMessage("SMTP Timeout: " + ste.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setCode(-1);
            result.setMessage("SMTP Error :: " + e.getMessage());
        } finally {
            try { if (writer != null) writer.close(); } catch (Exception ignored) {}
            try { if (reader != null) reader.close(); } catch (Exception ignored) {}
            if (socket != null && !socket.isClosed()) {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        return result;
    }

    private static String readMultiLineResponse(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        // read at least one line
        line = reader.readLine();
        if (line == null) return null;
        sb.append(line).append('\n');
        // if first line starts with 3-digit code followed by '-' then it's multi-line
        if (line.length() >= 4 && line.matches("^\\d{3}-.*")) {
            String prefix = line.substring(0, 3);
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
                if (line.startsWith(prefix + " ")) break; // last line
            }
        }
        return sb.toString();
    }

    private static int parseCode(String response) {
        if (response == null) return -1;
        response = response.trim();
        if (response.length() < 3) return -1;
        try {
            return Integer.parseInt(response.substring(0, 3));
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private static int parseCodeFromMultiLine(String multi) {
        if (multi == null) return -1;
        // find the last non-empty line
        String[] lines = multi.split("\\r?\\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String l = lines[i].trim();
            if (l.length() >= 3) {
                int code = parseCode(l);
                if (code >= 0) return code;
            }
        }
        return -1;
    }
}
