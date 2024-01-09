package com.thomsonreuters.uscl.ereader.gather.img.service;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.model.NovusImage;
import com.thomsonreuters.uscl.ereader.gather.img.util.NovusImageMetadataParser;
import com.thomsonreuters.uscl.ereader.gather.services.NovusFactory;
import com.thomsonreuters.uscl.ereader.gather.services.NovusUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import com.westgroup.novus.productapi.BLOB;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.NovusException;

@RunWith(MockitoJUnitRunner.class)
public final class NovusImageFinderImplTest {
    @InjectMocks
    private NovusImageFinderImpl service;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NovusFactory novusFactory;
    @Mock
    private NovusUtility novusUtility;
    @Mock
    private NovusImageMetadataParser parser;
    @Mock
    private Find find;
    @Mock
    private BLOB blob;

    @Before
    public void setUp() throws NovusException, GatherException {
        given(novusFactory.createNovus(false).getFind()).willReturn(find);
        given(novusUtility.getImgRetryCount()).willReturn("3");
        service.init();
    }

    @Test
    public void shouldFindImage() throws Exception {
        // given
        given(blob.getMimeType()).willReturn("image/png");
        given(blob.getMetaData()).willReturn("metadata");
        given(find.getBLOB(null, "imageId")).willReturn(blob);
        // when
        final NovusImage image = service.getImage("imageId");
        // then
        assertThat(image.getMediaType(), is(MediaType.IMAGE_PNG));
    }

    @Test
    public void shouldTryAgain() throws Exception {
        // given
        given(blob.getMimeType()).willReturn(" ", "image/png");
        given(blob.getMetaData()).willReturn("metadata");
        given(find.getBLOB(null, "imageId")).willReturn(blob);
        // when
        final NovusImage image = service.getImage("imageId");
        // then
        assertThat(image.getMediaType(), is(MediaType.IMAGE_PNG));
        then(find).should(times(2)).getBLOB(anyString(), anyString());
    }

    @Test
    public void shouldReturnNullIfFailed() throws Exception {
        // given
        given(blob.getMimeType()).willReturn("image/png");
        given(blob.getMetaData()).willReturn("");
        given(find.getBLOB(null, "imageId")).willReturn(blob);
        // when
        final NovusImage image = service.getImage("imageId");
        // then
        assertThat(image, nullValue());
        then(find).should(times(3)).getBLOB(anyString(), anyString());
    }
}
