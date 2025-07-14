package gencoders.e_tech_store_app.service;

import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceBuilder {
    private JavaMailSender mailSender;

    public EmailServiceBuilder setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        return this;
    }

    public EmailService createEmailService() {
        return new EmailService(mailSender);
    }
}