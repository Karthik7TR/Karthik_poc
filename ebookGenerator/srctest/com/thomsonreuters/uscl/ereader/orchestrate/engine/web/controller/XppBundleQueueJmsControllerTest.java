package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

@RunWith(MockitoJUnitRunner.class)
public class XppBundleQueueJmsControllerTest {
    private static final String MATERIAL_NUMBER = "11111111";

    @InjectMocks
    private XppBundleQueueJmsController xppBundleQueueJmsController;

    @Mock
    private JMSClient jmsClient;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private UuidGenerator uuidGenerator;

    @Before
    public void init() {
        given(uuidGenerator.generateUuid()).willReturn("uuid");
    }

    @Test
    public void shouldSendJmsMessage() throws IOException, JAXBException {
        final String srcFile = File.createTempFile("temp", "").getAbsolutePath();

        final XppBundleArchive request = xppBundleQueueJmsController.sendJmsMessage(MATERIAL_NUMBER, srcFile);

        assertEquals(MATERIAL_NUMBER, request.getMaterialNumber());
        assertEquals(srcFile, request.getEBookSrcPath());
        assertNotNull(request.getMessageId());
        assertNotNull(request.getVersion());
        assertNotNull(request.getDateTime());
        assertNotNull(request.getBundleHash());
    }
}
