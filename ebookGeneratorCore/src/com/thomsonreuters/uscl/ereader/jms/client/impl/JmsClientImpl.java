package com.thomsonreuters.uscl.ereader.jms.client.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component("jmsClient")
@Lazy
@Slf4j
public class JmsClientImpl implements JMSClient {

    @Override
    public void sendMessageToQueue(
        final JmsTemplate jmsTemplate,
        final String messageText,
        final Map<String, String> properties) {
        if (jmsTemplate == null) {
            log.info("Message queue has not been initialized");
            return;
        }
        try {
            jmsTemplate.send(new MessageCreator() {
                @Override
                public Message createMessage(final Session session) throws JMSException {
                    try {
                        final TextMessage message = session.createTextMessage();
                        message.setText(messageText);
                        addPropertiesToMessage(properties, message);
                        return message;
                    } catch (final JMSException e) {
                        log.error("Error creating message", e);
                        throw e;
                    }
                }
            });
        } catch (final JmsException e) {
            log.error("Issues sending the message", e);
            throw e;
        }

        log.debug("Message has been sent");
    }

    private void addPropertiesToMessage(final Map<String, String> properties, final Message message)
        throws JMSException {
        if (properties != null) {
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                message.setStringProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public List<String> receiveMessages(final JmsTemplate jmsTemplate, final String searchText) {
        final List<String> messages = jmsTemplate.browse((final Session session, final QueueBrowser browser) -> {
            final List<String> consumedMessages = new ArrayList<>();
            final Enumeration<Message> enumeration = browser.getEnumeration();
            while (enumeration.hasMoreElements()) {
                final TextMessage message = (TextMessage) enumeration.nextElement();
                final String messageBody = message.getText();
                if (messageBody.contains(searchText) && consumeMessage(session, browser, message.getJMSMessageID()) != null) {
                    consumedMessages.add(messageBody);
                }
            }
            return consumedMessages;
        });

        return messages;
    }

    private Message consumeMessage(final Session session, final QueueBrowser browser, final String messageId) throws JMSException {
        try (MessageConsumer consumer = session.createConsumer(
            browser.getQueue(), String.format("JMSMessageID='%s'", messageId))) {
            return consumer.receive(1000);
        }
    }
}
