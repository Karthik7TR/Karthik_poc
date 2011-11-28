package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

/**
 * Handle receiving messages from the high and normal priority job request queues.
 */
@Component
public class JobQueueManagerImpl implements JobQueueManager {
	//private static final Logger log = Logger.getLogger(JobQueueManagerImpl.class);
	
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name="highPriorityJobRunRequestQueue")
	private Queue highPriorityJobRunRequestQueue;
	@Resource(name="normalPriorityJobRunRequestQueue")
	private Queue normalPriorityJobRunRequestQueue;
	
	
	public JobRunRequest getHighPriorityJobRunRequest() throws Exception {
		return getJobRunRequest(highPriorityJobRunRequestQueue);
	}

	public JobRunRequest getNormalPriorityJobRunRequest() throws Exception {
		return getJobRunRequest(normalPriorityJobRunRequestQueue);		
	}
	
	/**
	 * Return the message at the front of the JMS queue.
	 * @param queue the queue destination to check.
	 * @return the message at the front of the queue, or null if the queue is emtpy.
	 * @throws Exception if there is a JMS fetch error, or a parse error with the received message.
	 */
	private JobRunRequest getJobRunRequest(Queue queue) throws Exception {
		JobRunRequest jobRunRequest = null;
		// Synchronous receive, but the timeout value is set so it will timeout and return quickly if there is nothing on the JMS queue.
		// See jmsTemplate definition in Spring bean xml file.
		Message message = jmsTemplate.receive(queue);
		if (message != null) {
			TextMessage textMessage = (TextMessage) message;
			String xmlRequest = textMessage.getText();
			jobRunRequest = JobRunRequest.unmarshal(xmlRequest);
		}
		return jobRunRequest;
	}
}
