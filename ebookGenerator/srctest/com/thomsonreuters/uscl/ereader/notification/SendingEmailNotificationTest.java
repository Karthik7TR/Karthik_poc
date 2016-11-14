package com.thomsonreuters.uscl.ereader.notification;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@RunWith(MockitoJUnitRunner.class)
public class SendingEmailNotificationTest {
	@Spy
	@InjectMocks
	private SendingEmailNotification sendingEmailNotification;
	@Mock
	private DocMetadataService docMetadataService;
	@Mock
	private AutoSplitGuidsService autoSplitGuidsService;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private NotificationInfo info;

	@Test
	public void emailBodyContainsBookPartsInfo() {
		// given
		given(docMetadataService.findDistinctSplitTitlesByJobId(1l)).willReturn(asList("title 1", "title 2"));
		given(info.getJobInstanceId()).willReturn(1l);
		given(info.getBookDefinition().getFullyQualifiedTitleId()).willReturn("fullyQualifiedTitle");
		given(info.getBookDefinition().getProviewDisplayName()).willReturn("proviewDisplayName");
		// when
		String bookPartsInfo = sendingEmailNotification.getBookPartsAndTitle(info);
		// then
		assertThat(bookPartsInfo, containsString("Proview display name : proviewDisplayName"));
		assertThat(bookPartsInfo, containsString("Fully Qualified Title : fullyQualifiedTitle"));
		assertThat(bookPartsInfo, containsString("Total parts : 2"));
		assertThat(bookPartsInfo, containsString("title 1"));
		assertThat(bookPartsInfo, containsString("title 2"));
	}

	@Test
	public void metricsInfoIsCorrect() {
		// given
		Map<String, String> guids2Text = new HashMap<>();
		guids2Text.put("id1", "text1");
		guids2Text.put("id2", "text2");
		given(autoSplitGuidsService.getSplitGuidTextMap()).willReturn(guids2Text);
		doReturn(5).when(sendingEmailNotification).getTotalSplitParts(info);
		given(info.getTocNodeCount()).willReturn(1);
		given(info.getThresholdValue()).willReturn(3);
		// when
		String metricsInfo = sendingEmailNotification.getMetricsInfo(info);
		// then
		assertThat(metricsInfo, containsString("**WARNING**: The book exceeds threshold value 3"));
		assertThat(metricsInfo, containsString("Total node count is 1"));
		assertThat(metricsInfo, containsString("Total split parts : 5"));
		assertThat(metricsInfo, containsString("id1  :  text1"));
		assertThat(metricsInfo, containsString("id2  :  text2"));
	}

	@Test
	public void emailSubjectForNormalBookIsCorrect() {
		// given
		given(info.getBookDefinition().getFullyQualifiedTitleId()).willReturn("book/id");
		// when
		String subject = sendingEmailNotification.getSubject(info);
		// then
		assertThat(subject, is("eBook Publishing Successful - book/id"));
	}

	@Test
	public void emailSubjectForSplitBookIsCorrect() {
		// given
		given(info.getBookDefinition().getFullyQualifiedTitleId()).willReturn("book/id");
		given(info.getBookDefinition().isSplitBook()).willReturn(true);
		// when
		String subject = sendingEmailNotification.getSubject(info);
		// then
		assertThat(subject, is("eBook Publishing Successful - book/id (Split Book)"));
	}

	@Test
	public void emailSubjectForBigNumberOfTocNodesContainsWarning() {
		// given
		given(info.getBookDefinition().getFullyQualifiedTitleId()).willReturn("book/id");
		given(info.isBigToc()).willReturn(true);
		// when
		String subject = sendingEmailNotification.getSubject(info);
		// then
		assertThat(subject, is("eBook Publishing Successful - book/id THRESHOLD WARNING"));
	}

	@Test
	public void emailBodyContainsBasicInfo() {
		// given
		given(info.getBookDefinition().getFullyQualifiedTitleId()).willReturn("book/id");
		given(info.getBookDefinition().getProviewDisplayName()).willReturn("Display Name");
		given(info.getEnvironment()).willReturn("test");
		given(info.getJobInstanceId()).willReturn(1l);
		given(info.getJobExecutionId()).willReturn(2l);
		doReturn("current version info").when(sendingEmailNotification).getVersionInfo(true, info);
		doReturn("prev version info").when(sendingEmailNotification).getVersionInfo(false, info);
		// when
		String subject = sendingEmailNotification.getBody(info);
		// then
		assertThat(subject, containsString("eBook Publishing Successful - book/id"));
		assertThat(subject, containsString("Proview Display Name: Display Name"));
		assertThat(subject, containsString("Title ID: book/id"));
		assertThat(subject, containsString("Job Instance ID: 1"));
		assertThat(subject, containsString("Job Execution ID: 2"));
		assertThat(subject, containsString("Environment: test"));
		assertThat(subject, containsString("current version info"));
		assertThat(subject, containsString("prev version info"));
	}

	@Test
	public void emailBodyForSplitBookContainsAdditionalInfo() {
		// given
		given(info.getBookDefinition().isSplitBook()).willReturn(true);
		doReturn("Additional Split Info").when(sendingEmailNotification).getBookPartsAndTitle(info);
		// when
		String subject = sendingEmailNotification.getBody(info);
		// then
		assertThat(subject, containsString("Additional Split Info"));
	}

	@Test
	public void emailBodyContainsMetricsForBigNumberOfTocNodes() {
		// given
		given(info.isBigToc()).willReturn(true);
		doReturn("Additional Metrics Info").when(sendingEmailNotification).getMetricsInfo(info);
		// when
		String subject = sendingEmailNotification.getBody(info);
		// then
		assertThat(subject, containsString("Additional Metrics Info"));
	}

	@Test
	public void currentVersionInfoIsCorrect() {
		// given
		PublishingStats stats = mock(PublishingStats.class);
		given(info.getCurrentPublishingStats()).willReturn(stats);
		given(stats.getGatherDocRetrievedCount()).willReturn(10);
		given(stats.getBookSize()).willReturn(null);
		given(stats.getJobInstanceId()).willReturn(1l);
		// when
		String versionInfo = sendingEmailNotification.getVersionInfo(true, info);
		// then
		assertThat(versionInfo, containsString("Current Version:"));
		assertThat(versionInfo, containsString("Job Instance ID: 1"));
		assertThat(versionInfo, containsString("Gather Doc Retrieved Count: 10"));
		assertThat(versionInfo, containsString("Book Size: -"));
	}

	@Test
	public void previousVersionInfoIsCorrect() {
		// given
		PublishingStats stats = mock(PublishingStats.class);
		given(info.getPreviousPublishingStats()).willReturn(stats);
		given(stats.getGatherDocRetrievedCount()).willReturn(null);
		given(stats.getBookSize()).willReturn(10l);
		// when
		String versionInfo = sendingEmailNotification.getVersionInfo(false, info);
		// then
		assertThat(versionInfo, containsString("Previous Version:"));
		assertThat(versionInfo, containsString("Gather Doc Retrieved Count: -"));
		assertThat(versionInfo, containsString("Book Size: 10"));
	}

	@Test
	public void notificationAddedIfNoPreviousVersionFound() {
		// given
		given(info.getPreviousPublishingStats()).willReturn(null);
		// when
		String versionInfo = sendingEmailNotification.getVersionInfo(false, info);
		// then
		assertThat(versionInfo, containsString("No Previous Version."));
	}
}
