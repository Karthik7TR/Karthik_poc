package com.thomsonreuters.uscl.ereader.orchestrate.engine.queue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.jms.client.impl.JmsClientImpl;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.dao.BundleArchiveDao;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/WEB-INF/spring/properties/default-spring.properties")
@ContextConfiguration({
    "classpath:com/thomsonreuters/uscl/ereader/orchestrate/engine/queue/ArchiverIntegrationTest-context.xml",
    "file:**/WebContent/WEB-INF/spring/service.xml",
    "file:**/WebContent/WEB-INF/spring/jobs.xml",
    "file:**/WebContent/WEB-INF/spring/persistence.xml",
    "file:**/WebContent/WEB-INF/spring/spring-batch.xml",
    "file:**/WebContent/WEB-INF/spring/application.xml",
    "file:**/WebContent/WEB-INF/spring/rest.xml"})
public final class ArchiverIntegrationTest
{
    private static String message =
        "<eBookRequest version='1.0'><messageId>%s</messageId><bundleHash>9fef91b329e827f0af4bdf0f604c909d</bundleHash><dateTime>2016-02-10T19:42:03.522Z</dateTime><srcFile>C:/apps/titleName.gz</srcFile></eBookRequest>";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private EBookRequestQueuePoller eBookRequestQueuePoller;

    @Autowired
    private ThreadPoolTaskExecutor springBatchBundleTaskExecutor;

    @Autowired
    private BundleArchiveDao bundleArchiveDao;

    @Test
    public void testRun() throws InterruptedException
    {
        final JMSClient jmsClient = new JmsClientImpl();

        final String messageUuid = uuidWithoutDashes();
        jmsClient.sendMessageToQueue(jmsTemplate, String.format(message, messageUuid), null);

        eBookRequestQueuePoller.pollMessageQueue();

        waitTillJobDone();

//		validate(mesageUuid);

        final String request = jmsClient.receiveSingleMessage(jmsTemplate, StringUtils.EMPTY);
        assertNull(request);
    }

//	@Test
//	@Transactional
//	public void testValidate() {
//		validate(messageUuid);
//	}

    private void validate(final String mesageUuid)
    {
        validateRetrieveBundleTask(mesageUuid);
    }

    private void validateRetrieveBundleTask(final String mesageUuid)
    {
        try
        {
            final EBookRequest dup = bundleArchiveDao.findByRequestId(mesageUuid);
            assertNotNull(dup);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    private void waitTillJobDone() throws InterruptedException
    {
        final ThreadPoolExecutor executor = springBatchBundleTaskExecutor.getThreadPoolExecutor();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    private String uuidWithoutDashes()
    {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
