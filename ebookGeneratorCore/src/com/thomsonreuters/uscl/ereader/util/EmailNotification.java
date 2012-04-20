/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.util;

import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;


/**
 * 
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a>c139353
 */
/**
 * @author c139353
 *
 */
public class EmailNotification
{
    private static final String from = "no-reply-eReader@thomsonreuters.com";
    private static final String host = "relay.int.westgroup.com";

    /**
     * @param msg
     * @param fileNames
     * @param toText
     * @throws MessagingException
     */
    public static void addAttachments(
        final Message msg, final List<String> fileNames, final String toText)
        throws MessagingException
    {
        MimeBodyPart p1 = new MimeBodyPart();
        p1.setText(toText);

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(p1);

        for (String str : fileNames)
        {
            MimeBodyPart p2 = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(str);
            p2.setDataHandler(new DataHandler(fds));
            p2.setFileName(fds.getName());
            mp.addBodyPart(p2);
        }

        msg.setContent(mp);
    }

    /**
     * 
     * @param toEmail
     * @param toSubject
     * @param toText
     */
    public static void send(final String toEmail, final String toSubject, final String toText)
    {
        if ((toEmail != null) && !toEmail.isEmpty())
        {
            try
            {
                if ((toSubject != null) && toSubject.isEmpty())
                {
                    throw new MessagingException("No Subject provided");
                }

                if ((toEmail != null) && toEmail.isEmpty())
                {
                    throw new MessagingException("No text provided.");
                }

                Properties props = new Properties();

                props.put("mail.smtp.host", host);

                Session session = Session.getInstance(props);

                String[] emails = toEmail.split(",");

                for (String emailAddress : emails)
                {
                    Message msg = prepareMessage(emailAddress.trim(), toSubject, from, session);

                    msg.setText(toText);

                    Transport.send(msg);
                }
            }
            catch (MessagingException mex)
            {
                mex.printStackTrace();
            }
        }
    }

    /**
     * @param toEmail
     * @param toSubject
     * @param toText
     * @param fileNames
     */
    public static void sendWithAttachment(
        final String toEmail, final String toSubject, final String toText,
        final List<String> fileNames)
    {
        if ((toEmail != null) && !toEmail.isEmpty())
        {
            try
            {
                if ((toSubject != null) && toSubject.isEmpty())
                {
                    throw new MessagingException("No Subject provided");
                }

                if ((toEmail != null) && toEmail.isEmpty())
                {
                    throw new MessagingException("No text provided.");
                }

                Properties props = new Properties();
                props.put("mail.smtp.host", host);

                Message msg = prepareMessage(toEmail, toSubject, from, Session.getInstance(props));
                addAttachments(msg, fileNames, toText);
                Transport.send(msg);
            }
            catch (MessagingException mex)
            {
               mex.printStackTrace();
            }
        }
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
        final String toEmail, final String toSubject, final String from, final Session session)
        throws MessagingException, AddressException
    {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        msg.setSubject(toSubject);
        msg.setSentDate(new Date());

        return msg;
    }
}
