package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * Represents a XML request that is sent to the batch engine JMS input queue and is used to start a job.
 * This object is JiBX marshalled and sent as XML JMS message payload to the
 * job run queue (high or normal priority) that the batch engine is monitoring for new run requests.
 */
public class JobRunRequest implements Serializable {
	private static final long serialVersionUID = -7285672486471302865L;
	
	private String jobName;			// job name to launch, required to START
	private Integer threadPriority;		// Job Thread CPU execution priority (1..10), e.g. Thread.NORM_PRIORITY, required to START 
	
	public static JobRunRequest createStartRequest(String jobName, Integer threadPriority) {
		return new JobRunRequest(jobName, threadPriority);
	}

	public String marshal() throws JiBXException {
		IBindingFactory factory = BindingDirectory.getFactory(JobRunRequest.class);
		IMarshallingContext context = factory.createMarshallingContext();
		context.setIndent(2);
		StringWriter stringWriter = new StringWriter();
		context.setOutput(stringWriter);
		context.marshalDocument(this);
		String xml = stringWriter.toString();
		return xml;
	}
	
	public static JobRunRequest unmarshal(String xml) throws JiBXException {
		StringReader stringReader = new StringReader(xml);
		try {
			IBindingFactory factory = BindingDirectory.getFactory(JobRunRequest.class);
			IUnmarshallingContext context = factory.createUnmarshallingContext();
			JobRunRequest request = (JobRunRequest) context.unmarshalDocument(stringReader);
			return request;
		} finally {
			stringReader.close();
		}
	}
	
	/**
	 * No-arg constructor required for unmarshaling
	 */
	public JobRunRequest() {
		super();
	}
	private JobRunRequest(String jobName, Integer threadPriority) {
		this.jobName = jobName;
		this.threadPriority = threadPriority;
	}

	public String getJobName() {
		return jobName;
	}
	public Integer getThreadPriority() {
		return threadPriority;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public void setThreadPriority(Integer priority) {
		this.threadPriority = priority;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
