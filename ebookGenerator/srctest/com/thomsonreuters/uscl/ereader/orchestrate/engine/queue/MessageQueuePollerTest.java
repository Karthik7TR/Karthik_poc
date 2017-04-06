package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.client.impl.JmsClientImpl;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

public final class MessageQueuePollerTest
{
    private JMSClient mockJmsClient;
    private JmsTemplate mockJmsTemplate;

    private XppBundleQueuePoller poller;

    @Before
    public void setUp()
    {
        poller = new XppBundleQueuePoller();

        mockJmsClient = EasyMock.createMock(JmsClientImpl.class);
        mockJmsTemplate = EasyMock.createMock(JmsTemplate.class);

        poller.setJmsClient(mockJmsClient);
        poller.setJmsTemplate(mockJmsTemplate);
    }

    private String version = "1.0";
    private String messageId = "ed4abfa40ee548388d39ecad55a0daaa";
    private String bundleHash = "ed4abfa40ee548388d39ecad55a0daaa";
    private long dateTime = new Date().getTime();
    private String srcFile = "/apps/eBookBuilder/prodcontent/xpp/tileName.gz";

    @Test
    public void testHappyPath()
    {
        EasyMock.expect(mockJmsClient.receiveSingleMessage(mockJmsTemplate, "")).andReturn(createRequest());
        EasyMock.replay(mockJmsClient);

        poller.pollMessageQueue();
    }

    private String createRequest()
    {
        return "<eBookRequest version=\""
            + version
            + "\">"
            + "<messageId>"
            + messageId
            + "</messageId>"
            + "<bundleHash>"
            + bundleHash
            + "</bundleHash>"
            + "<dateTime>"
            + dateTime
            + "</dateTime>"
            + "<srcFile>"
            + srcFile
            + "</srcFile>"
            + "</eBookRequest >";
    }
}
