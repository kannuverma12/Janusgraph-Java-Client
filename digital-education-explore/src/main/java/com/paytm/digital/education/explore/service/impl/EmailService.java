package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.dto.Mail;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service("emailService")
@RequiredArgsConstructor
public class EmailService {

    private static final Logger         log = LoggerFactory.getLogger(EmailService.class);

    public void sendMailWithAttachment(Mail mail, String filepath) {
        try {
            InternetAddress[] addresses = new InternetAddress[mail.getToAddressList().length];
            for (int i = 0; i < mail.getToAddressList().length; i++) {
                addresses[i] = new InternetAddress(mail.getToAddressList()[i]);
            }

            // Sender's email ID needs to be mentioned
            String from = mail.getFromAddress();

            // Get system properties
            Properties properties = System.getProperties();

            // Set the smtp server host name using 'mail.smtp.host' property
            properties.setProperty("mail.smtp.host", "mail.mkt.paytm");

            /*
             * If the SMTP server is not running on default port (25) you will also need to set
             * mail.smtp.port property, not required if server is running on default port 25
             */
            properties.setProperty("mail.smtp.port", "587");

            // Get the default Session object.
            Session session = Session.getDefaultInstance(properties);

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From address
            message.setFrom(new InternetAddress(from));

            // Set To address
            message.addRecipients(Message.RecipientType.TO, addresses);

            // Set Subject
            message.setSubject(mail.getSubject());
            message.setSentDate(new java.util.Date());

            BodyPart bodtPart = new MimeBodyPart();
            bodtPart.setContent(mail.getBody(), "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodtPart);

            if (StringUtils.isNotBlank(filepath)) {
                bodtPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filepath);
                bodtPart.setDataHandler(new DataHandler(source));
                bodtPart.setFileName(filepath.contains("/")
                                ? filepath.substring(filepath.lastIndexOf('/') + 1)
                                : filepath);
                multipart.addBodyPart(bodtPart);
            }
            // Now set the text in the email s
            message.setContent(multipart);
            // message.setContent(mail.getBody(), "text/html; charset=utf-8");
            message.saveChanges();

            Transport.send(message);
        } catch (MessagingException ex) {
            log.error("Error in sending email  mail : {}, filepath : {}", ex, mail, filepath);
        }
    }
}
