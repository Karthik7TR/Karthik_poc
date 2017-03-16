package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
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
    @Resource(name = "transformToHtmlTask")
    private TransformationToHtmlStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    private File original;
    private File expected;

    @Before
    public void setUp() throws URISyntaxException
    {
        original = new File(TransformationToHtmlStepIntegrationTest.class.getResource("sample.page").toURI());
        expected = new File(TransformationToHtmlStepIntegrationTest.class.getResource("expected.html").toURI());
    }

    @Test
    public void shouldTransformPartsToHtml() throws Exception
    {
        //given
        final File originalPagesDirectory = fileSystem.getOriginalPagesDirectory(step);
        FileUtils.forceMkdir(originalPagesDirectory);
        FileUtils.copyFileToDirectory(original, originalPagesDirectory);
        //when
        step.executeStep();
        //then
        final File html = fileSystem.getHtmlPageFile(step, "sample");
        assertThat(html, hasSameContentAs(expected));
    }
}
