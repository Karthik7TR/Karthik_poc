package com.thomsonreuters.uscl.ereader.gather.img.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class NovusImageMetadataParserImplTest
{
    @InjectMocks
    private NovusImageMetadataParserImpl parser;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldParseCorrect() throws Exception
    {
        // given
        final File correct =
            new File(NovusImageMetadataParserImplTest.class.getResource("imageMetadata_correct.xml").toURI());
        final String metadataStr = FileUtils.readFileToString(correct);
        // when
        final ImgMetadataInfo metadataInfo = parser.parse(metadataStr);
        // then
        assertThat(metadataInfo.getWidth(), is(2402L));
    }

    @Test
    public void shouldThrowExceptionIfXmlIsIncorrect() throws Exception
    {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Cannot parse image metadata from Novus");
        final File incorrect =
            new File(NovusImageMetadataParserImplTest.class.getResource("imageMetadata_incorrect.xml").toURI());
        final String metadataStr = FileUtils.readFileToString(incorrect);
        // when
        parser.parse(metadataStr);
        // then
    }
}
