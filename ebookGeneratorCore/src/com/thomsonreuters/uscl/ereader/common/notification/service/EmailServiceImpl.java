package com.thomsonreuters.uscl.ereader.common.notification.service;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service("emailService")
@Slf4j
public class EmailServiceImpl implements EmailService {
    private static final String FROM = "no-reply-eReader@thomsonreuters.com";
    private static final String HOST_PROPERTY = "mail.smtp.host";
    private static final String TIMEOUT_PROPERTY = "mail.smtp.timeout";
    private static final String NO_SUBJECT_ERROR_MESSAGE = "No subject provided";
    private static final String NO_RECIPIENTS_ERROR_MESSAGE = "No recipients provided";
    private static final String SMTP = "smtp";

    @Value("${mail.smtp.host}")
    public String host;

    @Value("${mail.smtp.timeout}")
    private Integer timeout;

    @Override
    public void send(@NotNull final NotificationEmail email) {
        send(convertToCsv(email.getRecipients()), email.getSubject(), email.getBody(),
                email.isBodyContentHtmlType());
    }

    @Override
    public void send(final Collection<InternetAddress> recipients, final String subject, final String body) {
        final String csvRecipients = convertToCsv(recipients);
        send(csvRecipients, subject, body, false);
    }

    @Override
    public void send(final String csvRecipients, final String subject, final String body) {
        send(csvRecipients, subject, body, false);
    }

    public void send(final String csvRecipients, final String subject, final String body, final boolean isHtml) {
        log.debug("Recipients: " + csvRecipients);
        try {
            checkSubject(subject);
            checkRecipients(csvRecipients);
            final Properties props = getProperties();
            final Session session = Session.getInstance(props);
            final String[] emails = csvRecipients.split(",");

            for (final String emailAddress : emails) {
                final Message msg = prepareMessage(emailAddress.trim(), subject, session);
                msg.setText(body);
                if (isHtml) {
                    msg.setContent(body, "text/html; charset=utf-8");
                }
                Transport.send(msg);
            }
        } catch (final MessagingException mex) {
            log.error(mex.getMessage(), mex);
        }
    }

    @Override
    public void sendWithAttachment(final Collection<InternetAddress> recipients,
            final String subject, final String body, final List<String> fileNames) {
        sendWithAttachment(convertToCsv(recipients), subject, body, fileNames);
    }

    @Override
    public boolean isUpAndRunning() {
        try {
            Session session = Session.getInstance(getProperties());
            Transport transport = session.getTransport(SMTP);
            transport.connect();
            if (transport.isConnected()) {
                transport.close();
                return true;
            }
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    private void sendWithAttachment(final String toEmail, final String toSubject, final String toText,
        final List<String> fileNames) {
        try {
            if (toEmail != null) {
                checkSubject(toSubject);
                checkRecipients(toEmail);
                final Properties props = getProperties();
                final Message msg = prepareMessage(toEmail, toSubject, Session.getInstance(props));
                addAttachments(msg, fileNames, toText);
                Transport.send(msg);
            }
        } catch (final MessagingException mex) {
            log.error(mex.getMessage(), mex);
        }
    }

    private void addAttachments(final Message msg, final List<String> fileNames, final String toText)
            throws MessagingException {
        final MimeBodyPart p1 = new MimeBodyPart();
        p1.setText(toText);
        final Multipart mp = new MimeMultipart();
        mp.addBodyPart(p1);

        for (final String str : fileNames) {
            final MimeBodyPart p2 = new MimeBodyPart();
            final FileDataSource fds = new FileDataSource(str);
            p2.setDataHandler(new DataHandler(fds));
            p2.setFileName(fds.getName());
            mp.addBodyPart(p2);
        }

        msg.setContent(mp);
    }

    private void checkSubject(final String subject) throws MessagingException {
        checkString(subject, NO_SUBJECT_ERROR_MESSAGE);
    }

    private void checkRecipients(final String csvRecipients) throws MessagingException {
        checkString(csvRecipients, NO_RECIPIENTS_ERROR_MESSAGE);
    }

    private void checkString(final String string, final String errorMessage) throws MessagingException {
        if (StringUtils.isBlank(string)) {
            throw new MessagingException(errorMessage);
        }
    }

    private String convertToCsv(final Collection<InternetAddress> recipients) {
        final StringBuilder csvRecipients = new StringBuilder();
        boolean firstTime = true;
        for (final InternetAddress recipient : recipients) {
            if (!firstTime) {
                csvRecipients.append(",");
            }
            firstTime = false;
            csvRecipients.append(recipient.getAddress());
        }
        return csvRecipients.toString();
    }

    private Message prepareMessage(final String toEmail, final String toSubject, final Session session)
        throws MessagingException {
        final Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        msg.setSubject(toSubject);
        msg.setSentDate(new Date());

        return msg;
    }

    private Properties getProperties() {
        final Properties props = new Properties();
        props.put(HOST_PROPERTY, host);
        props.put(TIMEOUT_PROPERTY, timeout);
        return props;
    }
}
