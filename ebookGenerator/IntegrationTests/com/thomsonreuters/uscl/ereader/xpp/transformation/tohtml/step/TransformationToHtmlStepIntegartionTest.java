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
public final class TransformationToHtmlStepIntegartionTest
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
        original = new File(TransformationToHtmlStepIntegartionTest.class.getResource("sample.part").toURI());
        expected = new File(TransformationToHtmlStepIntegartionTest.class.getResource("expected.html").toURI());
    }

    @Test
    public void shouldTransformPartsToHtml() throws Exception
    {
        //given
        final File originalPartsDirectory = fileSystem.getOriginalPartsDirectory(step);
        FileUtils.forceMkdir(originalPartsDirectory);
        FileUtils.copyFileToDirectory(original, originalPartsDirectory);
        //when
        step.executeStep();
        //then
        final File html = fileSystem.getToHtmlFile(step, "sample");
        assertThat(html, hasSameContentAs(expected));
    }
}
