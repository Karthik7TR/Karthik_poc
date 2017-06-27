package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class TransformationToHtmlStepIntegrationTest
{
    private static final String SAMPLE_DIVXML_PAGE = "sample.DIVXML.page";
    private static final String MATERIAL_NUMBER = "11111111";

    @Resource(name = "transformToHtmlTask")
    private TransformationToHtmlStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File original;
    private File expected;

    @Before
    public void setUp() throws URISyntaxException
    {
        original = new File(TransformationToHtmlStepIntegrationTest.class.getResource(SAMPLE_DIVXML_PAGE).toURI());
        expected = new File(TransformationToHtmlStepIntegrationTest.class.getResource("expected.html").toURI());
    }

    @Test
    public void shouldTransformPartsToHtml() throws Exception
    {
        //given
        final File originalPagesDirectory = mkdir(fileSystem.getOriginalPagesDirectory(step, MATERIAL_NUMBER));

        FileUtils.copyFileToDirectory(original, originalPagesDirectory);
        //when
        step.executeStep();
        //then
        final File html = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, SAMPLE_DIVXML_PAGE);
        assertThat(html, hasSameContentAs(expected));
    }
}
