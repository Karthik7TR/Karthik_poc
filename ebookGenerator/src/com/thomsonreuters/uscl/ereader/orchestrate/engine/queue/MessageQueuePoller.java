package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.jms.handler.EBookRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EBookRequestValidator;

public class MessageQueuePoller {
	private static final Logger log = LogManager.getLogger(MessageQueuePoller.class);
	private JMSClient jmsClient;
	private JmsTemplate jmsTemplate;
	private EBookRequestValidator eBookRequestValidator;

	@Scheduled(fixedDelay = 60000) // 1 minute
	public void pollJobQueue() {
		EBookRequest eBookRequest = null;
		try {
			String request = jmsClient.receiveSingleMessage(jmsTemplate, StringUtils.EMPTY);
			if (request == null) {
				return;
			}

			log.debug(request); // log raw request
			eBookRequest = unmarshalRequest(request);

			try {
				// validate message content
				eBookRequestValidator.validate(eBookRequest);
			} catch (MessageQueueException e) {
				log.error("request is invalid: " + e.getMessage());
				// cannot process
				return;
			}
		} catch (Exception e) {
			log.error("Problem encountered while polling message queue:", e);
		}

		if (eBookRequest != null) {
			log.debug("starting job on request " + eBookRequest); // log processed request
			// send to business validation
		}
	}

	private EBookRequest unmarshalRequest(String request) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(EBookRequest.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (EBookRequest) unmarshaller.unmarshal(new ByteArrayInputStream(request.getBytes()));

	}

	@Required
	public void setJmsClient(JMSClient jmsClient) {
		this.jmsClient = jmsClient;
	}

	public JMSClient getJMSClient() {
		return jmsClient;
	}

	@Required
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	@Required
	public void setEBookRequestValidator(EBookRequestValidator eBookRequestValidator) {
		this.eBookRequestValidator = eBookRequestValidator;
	}

	public EBookRequestValidator getEBookRequestValidator() {
		return eBookRequestValidator;
	}
}