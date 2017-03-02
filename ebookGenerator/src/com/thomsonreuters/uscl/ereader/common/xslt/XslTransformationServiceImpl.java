package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("xslTransformationService")
public class XslTransformationServiceImpl implements XslTransformationService
{
    private static final Logger LOG = LogManager.getLogger(XslTransformationServiceImpl.class);

    @Override
    public void transform(final Transformer transformer, final File input, final File output)
    {
        try (final FileInputStream inputStream = FileUtils.openInputStream(input))
        {
            final Source source = new StreamSource(inputStream);
            final Result result = new StreamResult(output);
            transformer.transform(source, result);
            LOG.debug(String.format("File %s transformed to %s", input.getAbsolutePath(), output.getAbsolutePath()));
        }
        catch (final TransformerException | IOException e)
        {
            final String message = String.format("Transformation error. Cannot produce %s", output.getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }
}
