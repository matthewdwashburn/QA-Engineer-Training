package com.revature.domain;

/**
 * EmailClient interface - External service we'll mock.
 * 
 * External services like email, payment, SMS should always be mocked
 * in unit tests to avoid:
 * - Sending real emails
 * - Network latency
 * - External service failures
 */
public interface EmailClient {
    
    /**
     * Send an email.
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body content
     * @return true if sent successfully
     */
    boolean send(String to, String subject, String body);
    
    /**
     * Send an email with CC recipients.
     */
    boolean sendWithCc(String to, String[] cc, String subject, String body);
    
    /**
     * Send a template-based email.
     */
    boolean sendTemplate(String to, String templateName, Object context);
    
    /**
     * Check if the email service is available.
     */
    boolean isAvailable();
}

