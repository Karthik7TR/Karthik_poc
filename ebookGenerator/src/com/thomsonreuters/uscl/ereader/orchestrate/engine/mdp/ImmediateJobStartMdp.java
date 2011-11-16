package com.thomsonreuters.uscl.ereader.orchestrate.engine.mdp;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.jibx.runtime.JiBXException;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.EngineManagerImpl;

/**
 * Handles job start requests that land on the immediate run queue and starts the job without delay.
 * These jobs are to be run right now, without any concern for throttling or the number of jobs that are already
 * currently running within the Spring Batch engine.
 */
@Deprecated
public class ImmediateJobStartMdp implements MessageListener {
    private static final Logger log = Logger.getLogger(ImmediateJobStartMdp.class);
    @Autowired
    private EngineManagerImpl engineUtils;
    
    public ImmediateJobStartMdp() {
    	super();
    }
    
    @Override
    public void onMessage(Message jmsMessage) {
    	TextMessage textMessage = (TextMessage) jmsMessage;
    	JobRunRequest jobControlRequest = null;
    	try {
    		String jobControlRequestXml = textMessage.getText();
    		jobControlRequest = JobRunRequest.unmarshal(jobControlRequestXml);
    		log.debug(jobControlRequest);
    		JobExecution jobExecution = engineUtils.runJob(jobControlRequest.getJobName(),
    													   jobControlRequest.getThreadPriority());
    		log.debug("Started Job: " + jobExecution);
    	} catch (JMSException eJms) {
    		log.error("Error consuming job control request JMS message", eJms);
    	} catch (JiBXException eJibx) {
    		log.error("Error during JiBX unmarshalling of " + JobRunRequest.class.getName() + ", invalid XML", eJibx);
    	} catch (Exception e) {
    		log.error("Error starting job request: " + jobControlRequest, e);
    	}
    }
}
