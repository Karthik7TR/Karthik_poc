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
 * Represents an XML request that is sent to the batch engine JMS input queue and is used to start a job.
 * This object is JiBX marshalled and sent as the XML JMS message payload to one of the
 * job run queues (high or normal priority) that the batch engine is periodically polling for new run requests.
 */
public class JobRunRequest implements Serializable {
	private static final long serialVersionUID = -7285672486471302865L;
	
	private String jobName = "ebookGeneratorJob";			// job name to launch, required to START
	private String bookCode;		// Which book is to be created
	private String bookTitle;		// Natural language name of book that will be created
	private String bookVersion;		// Version of the book
	private String userName;		// Who is requesting that the job be run
	private String userEmail;		// What is the requestor's email address
	
	public static JobRunRequest create(String bookCode, String bookTitle, String bookVersion,
									   String userName, String userEmail) {
		return new JobRunRequest(bookCode, bookTitle, bookVersion, userName, userEmail);
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
	private JobRunRequest(String bookCode, String bookTitle, String bookVersion, String userName, String userEmail) {
		this.bookCode = bookCode;
		this.bookTitle= bookTitle;
		this.bookVersion = bookVersion;
		this.userName = userName;
		this.userEmail = userEmail;
	}
	public String getBookCode() {
		return bookCode;
	}
	public String getBookTitle() {
		return bookTitle;
	}
	public String getBookVersion() {
		return bookVersion;
	}
	public String getJobName() {
		return jobName;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setBookCode(String code) {
		this.bookCode = code;
	}
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
	public void setBookVersion(String version) {
		this.bookVersion = version;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
