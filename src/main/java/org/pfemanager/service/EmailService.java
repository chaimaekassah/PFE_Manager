package org.pfemanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

@ApplicationScoped
public class EmailService {

    private final String EMAIL_FROM = "kshchaimae@gmail.com";
    private final String PASSWORD    = "ktee jfcd ssoc keni"; // 16 caractères sans espaces

    public void envoyerLienReinitialisation(String emailDest, String lien) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDest));
            message.setSubject("PFE Manager - Réinitialisation de votre mot de passe");
            message.setContent(
                    "<div style='font-family:Arial;'>" +
                            "<h2>Réinitialisation de mot de passe</h2>" +
                            "<p>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
                            "<a href='" + lien + "' style='background:#1a3c6e;color:white;padding:10px 20px;" +
                            "text-decoration:none;border-radius:5px;'>Réinitialiser mon mot de passe</a>" +
                            "<p>Ce lien expire dans <strong>30 minutes</strong>.</p>" +
                            "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>" +
                            "</div>",
                    "text/html; charset=utf-8"
            );
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur envoi email : " + e.getMessage(), e);
        }
    }
}