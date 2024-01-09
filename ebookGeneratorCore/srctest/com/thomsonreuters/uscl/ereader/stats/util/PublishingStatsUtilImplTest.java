package com.thomsonreuters.uscl.ereader.stats.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PublishingStatsUtilImplTest {
    @InjectMocks
    private PublishingStatsUtilImpl sut;

    @Test
    public void shouldReturnSuccessfullyPublishedFlagForNovusPathway() {
        //give
        final String publishStatus = "sendEmailNotification : COMPLETED";
        //when
        final boolean publishedSuccessfully = sut.isPublishedSuccessfully(publishStatus);
        //then
        assertThat(publishedSuccessfully, is(true));
    }

    @Test
    public void shouldReturnSuccessfullyPublishedFlagForXppPathway() {
        //give
        final String publishStatus = "sendEmailNotificationXppStep : COMPLETED";
        //when
        final boolean publishedSuccessfully = sut.isPublishedSuccessfully(publishStatus);
        //then
        assertThat(publishedSuccessfully, is(true));
    }

    @Test
    public void shouldReturnSuccessfullyPublishedFlagForOldStatus() {
        //give
        final String publishStatus = "Publish Step Completed";
        //when
        final boolean publishedSuccessfully = sut.isPublishedSuccessfully(publishStatus);
        //then
        assertThat(publishedSuccessfully, is(true));
    }

    @Test
    public void shouldReturnFalseIfNotSuccess() {
        //give
        final String publishStatus = "sendEmailNotificationXppStep : FAILED";
        //when
        final boolean publishedSuccessfully = sut.isPublishedSuccessfully(publishStatus);
        //then
        assertThat(publishedSuccessfully, is(false));
    }
}
