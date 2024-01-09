package com.thomsonreuters.uscl.ereader.xpp.strategy.type;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public final class HtmlDocumentFileNameFilterTest {
    private HtmlDocumentFileNameFilter sut;

    @Test
    public void shouldAcceptIfSameTypeAndName() {
        //given
        sut = new HtmlDocumentFileNameFilter(BundleFileType.MAIN_CONTENT, "some.DIVXML");
        //when
        final boolean accept = sut.accept(null, "some.DIVXML_123_sdghfbv23784kb.html");
        //then
        assertThat(accept, is(true));
    }

    @Test
    public void shouldNotAcceptIfDifferentName() {
        //given
        sut = new HtmlDocumentFileNameFilter(BundleFileType.MAIN_CONTENT, "some.DIVXML");
        //when
        final boolean accept = sut.accept(null, "some1.DIVXML_123_sdghfbv23784kb.html");
        //then
        assertThat(accept, is(false));
    }

    @Test
    public void shouldNotAcceptIfDifferentType() {
        //given
        sut = new HtmlDocumentFileNameFilter(BundleFileType.FRONT, "some.DIVXML");
        //when
        final boolean accept = sut.accept(null, "some.DIVXML_123_sdghfbv23784kb.html");
        //then
        assertThat(accept, is(false));
    }
}
