/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.job.service;

import static com.thomsonreuters.uscl.ereader.core.job.service.JobRequestMatchers.book;
import static com.thomsonreuters.uscl.ereader.core.job.service.JobRequestMatchers.jobRequest;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;

@RunWith(MockitoJUnitRunner.class)
public class JobNameProviderImplTest {
	@InjectMocks
	private JobNameProviderImpl provider;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldReturnCorrentJobNameForNortSourceType() throws Exception {
		//given
		BookDefinition book = book(SourceType.NORT);
		//when
		String jobName = provider.getJobName(book);
		//then
		assertThat(jobName, is("ebookGeneratorJob"));
	}
	
	@Test
	public void shouldReturnCorrentJobNameForXppSourceType() throws Exception {
		//given
		BookDefinition book = book(SourceType.XPP);
		//when
		String jobName = provider.getJobName(book);
		//then
		assertThat(jobName, is("ebookGeneratorXppJob"));
	}

	@Test
	public void shouldReturnCorrentJobNameForXppJobRequest() throws Exception {
		//given
		JobRequest jobRequest = jobRequest(SourceType.XPP);
		//when
		String jobName = provider.getJobName(jobRequest);
		//then
		assertThat(jobName, is("ebookGeneratorXppJob"));
	}
	
	@Test
	public void shouldThrowExceptionIfIncorrectJobRequest() throws Exception {
		//given
		thrown.expect(IllegalStateException.class);
		JobRequest jobRequest = new JobRequest();
		//when //then
		provider.getJobName(jobRequest);
	}
}
