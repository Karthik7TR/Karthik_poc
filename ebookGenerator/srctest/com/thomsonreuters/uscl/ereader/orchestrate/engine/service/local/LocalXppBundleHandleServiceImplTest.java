package com.thomsonreuters.uscl.ereader.orchestrate.engine.service.local;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.nio.file.Paths;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public final class LocalXppBundleHandleServiceImplTest {
    private static final String TEST_MATERIAL = "123456";
    private static final String TEST_UUID = "11SOMEUUID22";
    private static final String TEST_DIRECTORY_PATH = "srctest/com/thomsonreuters/uscl/ereader/orchestrate/engine/service/local";
    private static final String TEST_ARCHIVE_PATH = String.format("%s/TESTBUNDLE_%s_12_11_2017.zip", TEST_DIRECTORY_PATH, TEST_MATERIAL);
    private static final String TEST_JMS_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                                                 + "<eBookRequest version=\"1.0\">"
                                                     + "<messageId>11SOMEUUID22</messageId>"
                                                     + "<bundleHash>11SOMEUUID22</bundleHash>"
                                                     + "<dateTime>%s</dateTime>"
                                                     + "<srcFile>/path/to/somewhere</srcFile>"
                                                     + "<materialNumber>123456</materialNumber>"
                                                 + "</eBookRequest>";

    @InjectMocks
    private LocalXppBundleHandleServiceImpl localXppBundleHandleService;
    @Mock
    private JMSClient jmsClient;
    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private UuidGenerator uuidGenerator;

    @Before
    public void onTestSetUp() {
        given(uuidGenerator.generateUuid()).willReturn(TEST_UUID);
    }

    @Test
    public void shouldReturnXppBundleArchive() {
        //given
        final String filePath = Paths.get(TEST_ARCHIVE_PATH).toString().replace("\\", "/");
        //when
        final XppBundleArchive xppBundleArchive = localXppBundleHandleService.createXppBundleArchive(TEST_MATERIAL, filePath);
        //then
        verify(uuidGenerator).generateUuid();
        assertThat(xppBundleArchive.getMessageId(), equalTo(TEST_UUID));
        assertThat(xppBundleArchive.getMaterialNumber(), equalTo(TEST_MATERIAL));
        assertThat(xppBundleArchive.getEBookSrcPath(), equalTo(filePath));
        assertThat(xppBundleArchive.getVersion(), equalTo("1.0"));
        assertThat(xppBundleArchive.getBundleHash(), notNullValue());
        assertThat(xppBundleArchive.getDateTime(), notNullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionCannotGetHashFromFile() {
        //given
        final String filePath = Paths.get(TEST_DIRECTORY_PATH).toString().replace("\\", "/");
        //when
        localXppBundleHandleService.createXppBundleArchive(TEST_MATERIAL, filePath);
    }

    @Test
    public void shouldSendMessageAndReturnIt() {
        //given
        //when
        final String jmsMessage = localXppBundleHandleService.sendXppBundleJmsMessage(createBundleArchive());
        //then
        verify(jmsClient).sendMessageToQueue(jmsTemplate, jmsMessage, null);
        assertThat(jmsMessage, equalTo(String.format(TEST_JMS_MESSAGE, getDateString(jmsMessage))));
    }

    private String getDateString(final String jmsMessage) {
        final Matcher matcher = Pattern.compile("<dateTime>(?<value>.*)</dateTime>").matcher(jmsMessage);
        if (matcher.find()) {
            return matcher.group("value");
        }
        throw new RuntimeException("Field \"dateTime\" not found");
    }

    private XppBundleArchive createBundleArchive() {
        final XppBundleArchive bundleArchive = new XppBundleArchive();
        bundleArchive.setMessageId(TEST_UUID);
        bundleArchive.setBundleHash(TEST_UUID);
        bundleArchive.setMaterialNumber(TEST_MATERIAL);
        bundleArchive.setDateTime(new Date());
        bundleArchive.setEBookSrcPath("/path/to/somewhere");
        bundleArchive.setVersion("1.0");
        return bundleArchive;
    }

    @Test
    public void shouldHandleFileAndReturnCount() {
        //given
        //when
        final int handledBundlesCount = localXppBundleHandleService.processXppBundleDirectory(TEST_DIRECTORY_PATH);
        //then
        verify(uuidGenerator).generateUuid();
        verify(jmsClient).sendMessageToQueue(eq(jmsTemplate), anyString(), eq(null));
        assertThat(handledBundlesCount, equalTo(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionNotDirectory() {
        //given
        //when
        localXppBundleHandleService.processXppBundleDirectory(TEST_ARCHIVE_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionNonExistent() {
        //given
        //when
        localXppBundleHandleService.processXppBundleDirectory(String.format("%s/non_existent_directory", TEST_DIRECTORY_PATH));
    }
}
