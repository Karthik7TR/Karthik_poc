package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailServiceImpl;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CWBRetrieveServiceTest {
    private static final String TITLE_ID = "uscl/an/title_id";
    private static final String ENVIRONMENT = "workstation";
    private static final String EMAIL = "test@email.com";

    @InjectMocks
    private CWBRetrieveService generateTocTask;
    @Mock
    private EmailServiceImpl emailService;
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private List<String> splitGuidList;
    private List<String> dupGuidList;

    @Test
    public void testNoDuplicateToc() throws Exception {
        splitGuidList = new ArrayList<>();
        splitGuidList.add("abcd");
        generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicateToc() throws Exception {
        splitGuidList = new ArrayList<>();
        splitGuidList.add("abcd");
        dupGuidList = new ArrayList<>();
        dupGuidList.add("abcd");
        generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
    }

    @Test
    public void testManualSplitDuplcateToc() throws Exception {
        splitGuidList = new ArrayList<>();
        splitGuidList.add("abcd");
        dupGuidList = new ArrayList<>();
        dupGuidList.add("1234");
        generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
    }

    @Test
    public void testAutoSplitDuplcateToc() throws Exception {
        dupGuidList = new ArrayList<>();
        dupGuidList.add("1234");
        generateTocTask.duplicateTocCheck(null, dupGuidList);
    }

    @Test
    public void testAutoSplitNoDuplcateToc() throws Exception {
        generateTocTask.duplicateTocCheck(null, null);
    }

    @SneakyThrows
    @Test
    public void writeEmptyNodeReport() {
        Long jobInstanceId = 123L;
        String subject = String.format("eBook user Notification for title \"%s\", job: %s, Empty Nodes Removed",
                TITLE_ID, jobInstanceId.toString());
        String expectedEmailBody = String.format("Attached is the file of empty nodes removed and/or bad \"no WL pubtag\" from the TOC in this book. Format is comma seperated list of NORT guids and label. Environment: %s",
                ENVIRONMENT);
        File file = tempFolder.newFile();
        List<String> fileNames = Collections.singletonList(file.getAbsolutePath());
        List<InternetAddress> emailRecipients = Collections.singletonList(new InternetAddress(EMAIL));

        generateTocTask.writeEmtpyNodeReport(TITLE_ID, jobInstanceId, ENVIRONMENT, new ArrayList<>(),
                file, emailRecipients, 0, 0);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendWithAttachment(eq(emailRecipients), eq(subject), captor.capture(), eq(fileNames));
        String actualEmailBody = captor.getValue();
        assertEquals(expectedEmailBody, actualEmailBody);
    }
}
