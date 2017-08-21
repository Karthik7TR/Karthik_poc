package com.thomsonreuters.uscl.ereader.xpp.utils.links;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class CiteQueryProcessorTest
{
    private final String expected = "https://1.next.westlaw.com/Link/Document/FullText?findType=L&pubNum=1000259&cite=WAST71.34.710&originatingDoc=I0d85b630627211e78600cc06009ba385&refType=LQ&originationContext=ebook";

    @Test
    public void shouldCreateLinkFromSiteQuery() throws Exception
    {
        //given
        final String input =
            "<cite.query w-seq-number=\"00397\" w-ref-type=\"LQ\" w-normalized-cite=\"WAST71.34.710\" w-pub-number=\"1000259\" ID=\"I0d85b630627211e78600cc06009ba385\"><t x=\"0\" y=\"0\" style=\"designator.2\">71.34.710</t></cite.query>";
        //when
        final String result = CiteQueryProcessor.getLink(input);
        //then
        assertTrue(result.equals(expected));
    }
}
