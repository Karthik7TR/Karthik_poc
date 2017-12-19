package com.thomsonreuters.uscl.ereader.util;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @deprecated use {@link com.thomsonreuters.uscl.ereader.common.notification.service.EmailService}
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a>c139353
 */
@Deprecated
public final class EmailNotification {
    private static final Logger LOG = LogManager.getLogger(EmailNotification.class);
    private static final String FROM = "no-reply-eReader@thomsonreuters.com";
    public static final String HOST = "relay.int.westgroup.com";
    private static final Integer TIMEOUT = 60000; //ms

    private EmailNotification() {
    }

    /**
     * @param msg
     * @param fileNames
     * @param toText
     * @throws MessagingException
     */
    public static void addAttachments(final Message msg, final List<String> fileNames, final String toText)
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

    public static void send(
        final Collection<InternetAddress> recipients,
        final String subject,
        final String body,
        final boolean isHtml) {
        final String csvRecipients = convertToCsv(recipients);
        send(csvRecipients, subject, body, isHtml);
    }

    public static void send(final Collection<InternetAddress> recipients, final String subject, final String body) {
        final String csvRecipients = convertToCsv(recipients);
        send(csvRecipients, subject, body, false);
    }

    public static void send(final String csvRecipients, final String subject, final String body) {
        send(csvRecipients, subject, body, false);
    }

    public static void send(final String csvRecipients, final String subject, final String body, final boolean isHtml) {
        LOG.debug("Recipients: " + csvRecipients);
        if ((csvRecipients != null) && !csvRecipients.isEmpty()) {
            try {
                if ((subject != null) && subject.isEmpty()) {
                    throw new MessagingException("No subject provided");
                }
                if (StringUtils.isBlank(csvRecipients)) {
                    throw new MessagingException("No recipients provided");
                }

                final Properties props = new Properties();

                props.put("mail.smtp.host", HOST);
                props.put("mail.smtp.timeout", TIMEOUT);

                final Session session = Session.getInstance(props);

                final String[] emails = csvRecipients.split(",");

                for (final String emailAddress : emails) {
                    final Message msg = prepareMessage(emailAddress.trim(), subject, FROM, session);

                    msg.setText(body);
                    if (isHtml) {
                        msg.setContent(body, "text/html; charset=utf-8");
                    }
                    Transport.send(msg);
                }
            } catch (final MessagingException mex) {
                LOG.error(mex.getMessage(), mex);
            }
        }
    }

    public static void sendWithAttachment(
        final Collection<InternetAddress> recipients,
        final String subject,
        final String body,
        final List<String> fileNames) {
        final String csvRecipients = convertToCsv(recipients);
        sendWithAttachment(csvRecipients, subject, body, fileNames);
    }

    /**
     * @param toEmail
     * @param toSubject
     * @param toText
     * @param fileNames
     */
    public static void sendWithAttachment(
        final String toEmail,
        final String toSubject,
        final String toText,
        final List<String> fileNames) {
        if ((toEmail != null) && !toEmail.isEmpty()) {
            try {
                if ((toSubject != null) && toSubject.isEmpty()) {
                    throw new MessagingException("No Subject provided");
                }

                if (toEmail.isEmpty()) {
                    throw new MessagingException("No text provided.");
                }

                final Properties props = new Properties();
                props.put("mail.smtp.host", HOST);

                final Message msg = prepareMessage(toEmail, toSubject, FROM, Session.getInstance(props));
                addAttachments(msg, fileNames, toText);
                Transport.send(msg);
            } catch (final MessagingException mex) {
                LOG.error(mex.getMessage(), mex);
            }
        }
    }

    private static String convertToCsv(final Collection<InternetAddress> recipients) {
        final StringBuffer csvRecipients = new StringBuffer();
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

    /**
     *
     * @param toEmail
     * @param toSubject
     * @param from
     * @param session
     *
     * @return
     *
     * @throws MessagingException
     * @throws AddressException
     */
    private static Message prepareMessage(
        final String toEmail,
        final String toSubject,
        final String from,
        final Session session) throws MessagingException, AddressException {
        final Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        msg.setSubject(toSubject);
        msg.setSentDate(new Date());

        return msg;
    }
}
